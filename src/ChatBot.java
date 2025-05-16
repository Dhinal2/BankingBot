import java.util.HashMap;
import java.util.Random;

public class ChatBot {
    private HashMap<String, String> staticResponses;
    private HashMap<String, String> keywordResponses;
    private String userName;
    private Random random;

    public ChatBot() {
        staticResponses = new HashMap<>();
        keywordResponses = new HashMap<>();
        random = new Random();
        loadStaticResponses();
        loadKeywordResponses();
    }

    // Static Responses to user (greetings)
    private void loadStaticResponses() {
        staticResponses.put("hello", "Hello! How can I help you?");
        staticResponses.put("hi", "Hi there! What can I do for you?");
        staticResponses.put("thanks", "You're welcome!");
        staticResponses.put("good morning", "Good morning to you too!");
        staticResponses.put("goodbye", "Goodbye! Take care.");
        // Add more as needed
    }

     // Banking keyword-based responses
     private void loadKeywordResponses() {
        keywordResponses.put("balance", "You can check your balance via the mobile app or by visiting your nearest branch.");
        keywordResponses.put("loan", "We offer personal, home, and car loans with attractive interest rates.");
        keywordResponses.put("account", "We have savings, current, and fixed deposit accounts.");
        keywordResponses.put("transfer", "You can transfer money using our mobile app or by visiting a branch.");
        keywordResponses.put("atm", "You can locate the nearest ATM using our bank's website or app.");
        keywordResponses.put("credit card", "We offer various credit cards with reward points and cashback.");
        keywordResponses.put("interest", "Our interest rates vary depending on the type of account or loan. Please specify.");
        keywordResponses.put("open account", "You can open an account online or visit the nearest branch with your ID.");
        keywordResponses.put("help", "I'm here to assist you with banking-related queries. You can ask about loans, accounts, ATMs, and more.");
    }


    public String getResponse(String input) {
        input = input.toLowerCase();

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
            "Hmm... I'm not sure I understand.",
            "Could you please rephrase that?",
            "I'm still learning. Can you ask that another way?"
        };
        return fallback[random.nextInt(fallback.length)];
    }
}
