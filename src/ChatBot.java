import java.io.*;
import java.util.HashMap;
import java.util.Random;

public class ChatBot {
    private final HashMap<String, String> staticResponses;
    private final HashMap<String, String> keywordResponses;
    private final HashMap<String, String> lemmas;
    private String userName;
    private final Random random;


        private final String LEARNED_FILE = "learned.txt"; // File to store learned Q&A

    //Constructors
    public ChatBot() {
        staticResponses = new HashMap<>();
        keywordResponses = new HashMap<>();
        random = new Random();
        loadStaticResponses();
        loadKeywordResponses();
        lemmas = new HashMap<>();
        loadLemmas();
        loadLearnedResponses(); // Load responses from file

    }

    // Static Responses to user (greetings method)
    private void loadStaticResponses() {
        staticResponses.put("hello", "Hello! How can I help you?");
        staticResponses.put("hi", "Hi there! What can I do for you?");
        staticResponses.put("thanks", "You're welcome!");
        staticResponses.put("good morning", "Good morning to you too!");
        staticResponses.put("goodbye", "Goodbye! Take care.");
        staticResponses.put("how are you", "I'm good how are you?");
        staticResponses.put("good", "Glad to hear! \n How can I help?");
        staticResponses.put("bad", "I'm sorry to hear that \n How can I help?");
        staticResponses.put("whats your name", "My name is BankingBot! \n How can I help you?");
        
    }

     // Banking keyword-based responses method
     private void loadKeywordResponses() {
        keywordResponses.put("loan", "We offer personal, home, and car loans with attractive interest rates.");
        keywordResponses.put("account", "We have savings, current, and fixed deposit accounts.\nWhat account type would you like to open?");
        keywordResponses.put("current account", "If you would like to open a current account, visit the nearest Branch or Download our App");
        keywordResponses.put("savings account", "If you would like to open a savings account, visit the nearest Branch or Download our App");
        keywordResponses.put("fixed deposit", "If you would like to open a fixed deposit, visit the nearest Branch or Download our App");
        keywordResponses.put("transfer", "You can transfer money using our mobile app or by visiting a branch.");
        keywordResponses.put("atm", "You can locate the nearest ATM using our bank's website or app.");
        keywordResponses.put("credit card", "We offer various credit cards with reward points and cashback.");
        keywordResponses.put("interest", "Our interest rates vary depending if it is an account or loan rate. Please specify.");
        keywordResponses.put("help", "I'm here to assist you with banking-related queries. You can ask about loans, accounts, ATMs, and more.");
        keywordResponses.put("loan rate", "We offer 1 year loans with only 2.5% intrest rates ");
        keywordResponses.put("account rate", "Fixed Deposit Rates include 8% for 1 year");
        keywordResponses.put("personal loans", "We off Personal loans upto a year!");
        keywordResponses.put("home loans", "Home loans upto a 5 years!");
        keywordResponses.put("car loans", "Car loans upto a 3 years!");
    }

    // Get the user Response
    public String getResponse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "Go ahead and ask me anything you want!";
        }
    
        
        final String processedInput = lemmatize(input.toLowerCase());
    
        // Check if it's a learning command
        if (handleLearning(processedInput)) {
            return "Thanks! I've learned something new.";
        }
    
        if (processedInput.contains("my name is")) {
            userName = processedInput.replace("my name is", "").trim();
            return "Nice to meet you, " + userName + "!";
        }
    
        if (processedInput.contains("what is my name") || processedInput.contains("do you know my name")) {
            return (userName != null) ? "Your name is " + userName + "." : "I don't know your name yet!";
        }
    
        for (String key : staticResponses.keySet()) {
            if (processedInput.contains(key)) {
                return staticResponses.get(key);
            }
        }

        for (String key : keywordResponses.keySet()) {
            if (processedInput.contains(key)) {
                return keywordResponses.get(key);
            }
        }
    
        // Fallback
        String[] fallback = {
            "Hmm... I'm not sure I understand. You can teach me using: learn: your question = your answer",
            "I'm still learning. Want to teach me something new? Just type: learn: your question = your answer",
            "Sorry, I don't have an answer for that. You can add one using: learn: your question = your answer"
        };
        return fallback[random.nextInt(fallback.length)];
    }
    

    // Lematization method to load common variations
    private void loadLemmas() {
        lemmas.put("transferring", "transfer");
        lemmas.put("transferred", "transfer");
        lemmas.put("accounts", "account");
        lemmas.put("saving", "savings account");
        lemmas.put("savings", "savings account");
        lemmas.put("current", "current account");
        lemmas.put("acc", "account");
        lemmas.put("balances", "balance");
        lemmas.put("bal", "balance");
        lemmas.put("fd", "fixed deposit");
        lemmas.put("branches", "branch");
        lemmas.put("what is your name", "whats your name");
        lemmas.put("personal loan", "personal loans");
        lemmas.put("personal", "personal loans");
        lemmas.put("home loan", "home loans");
        lemmas.put("home", "home loans");
        lemmas.put("car loan", "car loans");
        lemmas.put("car", "car loans");
    }
    
    // Method to apply lematization words
    private String lemmatize(String input) {
        for (String word : lemmas.keySet()) {
            if (input.contains(word)) {
                input = input.replace(word, lemmas.get(word));
            }
        }
        return input;
    }
    
    // Load learned responses from file
    private void loadLearnedResponses() {
        File file = new File(LEARNED_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String question = parts[0].trim().toLowerCase();
                    String answer = parts[1].trim();
                    keywordResponses.put(question, answer);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading learned responses: " + e.getMessage());
        }
    }

    // Save new learned response
    private void saveLearnedResponse(String question, String answer) {
        try (FileWriter writer = new FileWriter(LEARNED_FILE, true)) {
            writer.write(question + "=" + answer + "\n");
        } catch (IOException e) {
            System.out.println("Error saving learned response: " + e.getMessage());
        }
    }

    // Train the bot with custom question = answer format
    public boolean handleLearning(String input) {
        if (input.toLowerCase().startsWith("learn:")) {
            String[] parts = input.substring(6).split("=", 2);
            if (parts.length == 2) {
                String question = lemmatize(parts[0].trim().toLowerCase());
                String answer = parts[1].trim();
                keywordResponses.put(question, answer);
                saveLearnedResponse(question, answer);
                return true;
            }
        }
        return false;
    }

    //Read Usernames from textfiles to check bank credentials
    public String getUserInfo(String name, String field) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains("name=" + name.toLowerCase())) {
                    String[] parts = line.split(",");
                    for (String part : parts) {
                        String[] keyValue = part.split("=");
                        if (keyValue[0].trim().equalsIgnoreCase(field)) {
                            return keyValue[1].trim();
                        }
                    }
                    return "I found your record, but couldn’t locate your " + field;
                }
            }
            return "I couldn’t find your record. Are you registered?";
        } catch (IOException e) {
            return "Sorry, I couldn't read the user data file.";
        }
    }
    
    //Look up Username from text file
    public String lookupUserField(String name, String field) {
        File f = new File("Users.txt");
        if (!f.exists()) return null;  // File not found → treat as guest
    
        try (BufferedReader rdr = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = rdr.readLine()) != null) {
                if (line.toLowerCase().contains("name=" + name.toLowerCase())) {
                    // split each key=value pair
                    for (String kv : line.split(",")) {
                        String[] parts = kv.split("=", 2);
                        if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(field)) {
                            return parts[1].trim();
                        }
                    }
                    return null;  // name found but field not found
                }
            }
            return null;  // user not found
        } catch (IOException e) {
            return null;  // error reading file
        }
    }
    
}
