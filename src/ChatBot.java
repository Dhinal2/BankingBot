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
        loadDynamicResponsesFromFile();
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

     // Banking keyword-based responses method to load dynamic responses
     private void loadDynamicResponsesFromFile() {
        File file = new File("knowledgebase.txt");
        if (!file.exists()) return;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("=")) {
                    String[] parts = line.split("=", 2);
                    String keyword = parts[0].trim().toLowerCase();
                    String response = parts[1].trim();
                    keywordResponses.put(keyword, response);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading dynamic responses: " + e.getMessage());
        }
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
    
    // Basic stemmer to remove common plural and suffix forms
    private String stemWord(String word) {
        if (word.endsWith("ies")) {
            return word.substring(0, word.length() - 3) + "y";  // e.g. "policies" -> "policy"
        } else if (word.endsWith("s") && word.length() > 3) {
            return word.substring(0, word.length() - 1);  // e.g. "accounts" -> "account"
        } else if (word.endsWith("ing")) {
            return word.substring(0, word.length() - 3);  // e.g. "transferring" -> "transfer"
        } else if (word.endsWith("ed")) {
            return word.substring(0, word.length() - 2);  // e.g. "transferred" -> "transfer"
        }
        return word;
    }


    // Lematization method to load common variations
    private void loadLemmas() {
        lemmas.put("loans", "loan");
        lemmas.put("acc", "account");
        lemmas.put("balances", "balance");
        lemmas.put("bal", "balance");
        lemmas.put("branches", "branch");
        lemmas.put("what is your name", "whats your name");
    }
    
    // Method to apply lematization words
    private String lemmatize(String input) {
        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();
    
        for (String word : words) {
            // First check dictionary
            String lemma = lemmas.getOrDefault(word, word);
    
            // Then stem the result (unless it was already in dictionary)
            if (!lemmas.containsKey(word)) {
                lemma = stemWord(lemma);
            }
    
            result.append(lemma).append(" ");
        }
    
        return result.toString().trim();
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
