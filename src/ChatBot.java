import java.io.*;
import java.util.HashMap;
import java.util.Random;

public class ChatBot {
    private final HashMap<String, String> staticResponses;
    private final HashMap<String, String> keywordResponses;
    private final HashMap<String, String> lemmas;
    private String userName;
    private final Random random;
    private final HashMap<String, String> branchInfo = new HashMap<>();
    private final String BRANCH_FILE = "Branches.txt";


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
        loadBranchInfo();

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
        keywordResponses.put("loan", "We offer personal, home, and car loans with attractive interest rates.");
        keywordResponses.put("account", "We have savings, current, and fixed deposit accounts.");
        keywordResponses.put("current account", "Would you like to open a current account?");
        keywordResponses.put("savings account", "Would you like to open a savings account?");
        keywordResponses.put("fixed deposit", "Would you like to open a fixed deposit account?");
        keywordResponses.put("transfer", "You can transfer money using our mobile app or by visiting a branch.");
        keywordResponses.put("atm", "You can locate the nearest ATM using our bank's website or app.");
        keywordResponses.put("credit card", "We offer various credit cards with reward points and cashback.");
        keywordResponses.put("interest", "Our interest rates vary depending on the type of account or loan. Please specify.");
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

            // Check for branch lookup
            if (input.contains("branch")) {
            for (String location : branchInfo.keySet()) {
            if (input.contains(location)) {
            return "Our " + capitalize(location) + " branch is located at: " + branchInfo.get(location);
                }
            }
            return "Please specify a branch location form the provided list. (Kandy, Colombo, Galle, Trinco, Hambantota)";
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
        lemmas.put("branches", "branch");
        
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

<<<<<<< HEAD
    private void loadBranchInfo() {
    File file = new File(BRANCH_FILE);
    if (!file.exists()) return;

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                String location = parts[0].trim().toLowerCase();
                String address = parts[1].trim();
                branchInfo.put(location, address);
            }
        }
    } catch (IOException e) {
        System.out.println("Error loading branch info: " + e.getMessage());
    }
}

    private String capitalize(String input) {
    return input.substring(0, 1).toUpperCase() + input.substring(1);
}
=======
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
    
        /**
     * Look up a given field for the specified user in data/users.txt.
     * @param name  the user's name (case-insensitive)
     * @param field one of: name, accountNumber, type, branch, balance
     * @return the field value or an error message
     */
    public String lookupUserField(String name, String field) {
        File f = new File("Users.txt");
        if (!f.exists()) return "No user database found.";
        try (BufferedReader rdr = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = rdr.readLine()) != null) {
                if (line.toLowerCase().contains("name=" + name.toLowerCase())) {
                    // split each key=value pair
                    for (String kv : line.split(",")) {
                        String[] parts = kv.split("=", 2);
                        if (parts[0].trim().equalsIgnoreCase(field)) {
                            return parts[1].trim();
                        }
                    }
                    return "I found you, but couldn’t find your “" + field + ".”";
                }
            }
            return "I don’t have a record for “" + name + ".”";
        } catch (IOException e) {
            return "Error reading user data.";
        }
    }
>>>>>>> 47938197f14aaf10d216903992e0f6c585194907

        
}
