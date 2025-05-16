import java.util.HashMap;
import java.util.Random;

public class ChatBot {
    private HashMap<String, String> staticResponses;
    private String userName;
    private Random random;

    public ChatBot() {
        staticResponses = new HashMap<>();
        random = new Random();
        loadStaticResponses();
    }

    // Static Responses to user 
    private void loadStaticResponses() {
        staticResponses.put("hello", "Hello! How can I help you?");
        staticResponses.put("hi", "Hi there! What can I do for you?");
        staticResponses.put("thanks", "You're welcome!");
        staticResponses.put("good morning", "Good morning to you too!");
        staticResponses.put("goodbye", "Goodbye! Take care.");
        // Add more as needed
    }

    public String getResponse(String input) {
        input = input.toLowerCase();

        // If user introduces their name
        if (input.contains("my name is")) {
            userName = input.replace("my name is", "").trim();
            return "Nice to meet you, " + userName + "!";
        }

        //if user Enters nothing
        if (input == null || input.trim().isEmpty()){
            return "Please enter something";
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

        // Fallback random response
        String[] fallback = {
            "Hmm... I'm not sure I understand.",
            "Could you please rephrase that?",
            "I'm still learning. Can you ask that another way?"
        };
        return fallback[random.nextInt(fallback.length)];
    }
}
