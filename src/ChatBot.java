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
        // Add more as needed
    }

     // Banking keyword-based responses method
     private void loadKeywordResponses() {
        keywordResponses.put("balance", "You can check your balance via the mobile app or by visiting your nearest branch.");
        keywordResponses.put("loan", "We offer personal, home, and car loans with attractive interest rates.");
        keywordResponses.put("account", "We have savings, current, and fixed deposit accounts.");
        keywordResponses.put("current account", "Would you like to open a current account?");
        keywordResponses.put("savings account", "Would you like to open a savings account?");
        keywordResponses.put("fixed deposit", "Would you like to open a fixed deposit account?");
        keywordResponses.put("transfer", "You can transfer money using our mobile app or by visiting a branch.");
        keywordResponses.put("atm", "You can locate the nearest ATM using our bank's website or app.");
        keywordResponses.put("credit card", "We offer various credit cards with reward points and cashback.");
        keywordResponses.put("interest", "Our interest rates vary depending on the type of account or loan. Please specify.");
        keywordResponses.put("open account", "You can open an account online or visit the nearest branch with your ID.");
        keywordResponses.put("help", "I'm here to assist you with banking-related queries. You can ask about loans, accounts, ATMs, and more.");
    }

    // Get the user Response
    public String getResponse(String input) {
        input = input.toLowerCase();
        input = lemmatize(input);

         // Check if it's a learning command
         if (handleLearning(input)) {
        return "Thanks! I've learned something new.";
        }
        
        //if user Enters nothing
        if (input == null || input.trim().isEmpty()){
            return "You have not asked anything";
        }

        // Name memory
        if (input.contains("my name is")) {
            userName = input.replace("my name is", "").trim();
            return "Nice to meet you, " + userName + "!";
        }

        // Respond with name if user asks
        if (input.contains("what is my name") || input.contains("do you know my name")) {
            return (userName != null) ? "Your name is " + userName + "." : "I don't know your name yet!";
        }

        // Check static responses
        for (String key : staticResponses.keySet()) {
            if (input.contains(key)) {
                return staticResponses.get(key);
            }
        }

        //Check the banking keyword
        for (String key : keywordResponses.keySet()) {
            if (input.contains(key)) {
                return keywordResponses.get(key);
            }
        }

        // Fallback random response
        String[] fallback = {
        "Hmm... I'm not sure I understand. You can teach me using: learn: your question = your answer",
        "I'm still learning. Want to teach me something new? Just type: learn: your question = your answer",
        "Sorry, I don't have an answer for that. You can add one using: learn: your question = your answer"
        };
        return fallback[random.nextInt(fallback.length)];
    }

    // Lematization method to load common variations
    private void loadLemmas() {
        lemmas.put("loans", "loan");
        lemmas.put("transferring", "transfer");
        lemmas.put("transferred", "transfer");
        lemmas.put("opened", "open");
        lemmas.put("opn", "open");
        lemmas.put("accounts", "account");
        lemmas.put("acc", "account");
        lemmas.put("balances", "balance");
        lemmas.put("bal", "balance");
        lemmas.put("fd", "fixed deposit");
        lemmas.put("rate", "interest");
        
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

        
}
