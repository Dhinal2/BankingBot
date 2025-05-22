import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatBot {
    private final HashMap<String, String> staticResponses;
    private final HashMap<String, String> keywordResponses; // Initial knowledge base
    private final HashMap<String, String> learnedResponses; // Responses learned from users
    private final HashMap<String, String> lemmas;
    private String userName;
    private final Random random;

    private final String LEARNED_FILE = "learned.txt"; // File to store learned Q&A

    //Constructors
    public ChatBot() {
        staticResponses = new HashMap<>();
        keywordResponses = new HashMap<>();
        learnedResponses = new HashMap<>();
        random = new Random();
        loadStaticResponses();
        loadDynamicResponsesFromFile();
        lemmas = new HashMap<>();
        loadLemmas();
        loadLearnedResponses(); // Load responses from file
    }

    //Aray List for Random Greetings
    private final List<String> greetingResponses = Arrays.asList(
    "Hey there!",
    "Hello! How can I help you today?",
    "Hi! Good to see you.",
    "Greetings!",
    "Hey! What can I do for you?"
    );

    // Static Responses to user (greetings method)
    private void loadStaticResponses() {
        staticResponses.put("thank you", "You're welcome!");
        staticResponses.put("how are you", "I'm good how are you?");
        staticResponses.put("good", "Glad to hear! \n How can I help?");
        staticResponses.put("bad", "I'm sorry to hear that \n How can I help?");
        staticResponses.put("what is your name", "My name is BankingBot! \n How can I help you?");
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
        String[] inputWords = processedInput.split("\\s+"); // Tokenize input

        // 1. Handle greetings with random response
        if (processedInput.equals("hi") || processedInput.equals("hello") || processedInput.equals("hey")) {
            return greetingResponses.get(random.nextInt(greetingResponses.size()));
        }

        // 2. Check static responses (FULL MATCH FIRST)
        if (staticResponses.containsKey(processedInput)) {
            return staticResponses.get(processedInput);
        }

        // 3. Check static responses (CONTAINS)
        for (String key : staticResponses.keySet()) {
            if (processedInput.contains(key)) {
                return staticResponses.get(key);
            }
        }

        // 4. Check learned responses (prioritize more recent knowledge?)
        List<String> sortedLearnedKeys = new ArrayList<>(learnedResponses.keySet());
        sortedLearnedKeys.sort(Comparator.comparingInt(String::length).reversed()); // Sort by length
        for (String key : sortedLearnedKeys) {
            if (isWholeWordMatch(key, inputWords)) {
                return learnedResponses.get(key);
            }
        }

        // 5. Check initial keyword-based responses
        List<String> sortedKeywordKeys = new ArrayList<>(keywordResponses.keySet());
        sortedKeywordKeys.sort(Comparator.comparingInt(String::length).reversed());// Sort by length
        for (String key : sortedKeywordKeys) {
            if (isWholeWordMatch(key, inputWords)) {
                return keywordResponses.get(key);
            }
        }

        // 6. Handle learning command
        if (handleLearning(processedInput)) {
            return "Thanks! I've learned something new.";
        }

        // 7. Handle name-related inquiries
        if (input.toLowerCase().contains("my name is")) {
            Pattern pattern = Pattern.compile("my name is\\s+(.+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                userName = matcher.group(1).replaceAll("[^a-zA-Z\\s]", "").trim(); // Removes punctuation
                return "Nice to meet you, " + userName + "!";
            } else {
                return "Sorry, I didn’t catch your name. Please say it like: My name is Kevin.";
            }
        }
        

        if (processedInput.contains("what is my name") || processedInput.contains("do you know my name")) {
            return (userName != null) ? "Your name is " + userName + "." : "I don't know your name yet!";
        }

        // 8. Fallback
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
        lemmas.put("savings", "savings account");
        lemmas.put("current", "current account");
        lemmas.put("fixed", "fixed deposit");
        lemmas.put("fd", "fixed deposit");
        lemmas.put("personal", "personal loans");
        lemmas.put("home", "home loans");
        lemmas.put("car", "car loans");
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
                    learnedResponses.put(question, answer); // Store in learnedResponses
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
                learnedResponses.put(question, answer); // Store in learnedResponses
                saveLearnedResponse(question, answer);
                return true;
            }
        }
        return false;
    }

    // Look up User information from text file
    public String getUserInfo(String name, String field) {
        File f = new File("Users.txt");
        if (!f.exists()) return "Sorry, the user data file is missing.";

        try (BufferedReader rdr = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = rdr.readLine()) != null) {
                if (line.toLowerCase().contains("name=" + name.toLowerCase())) {
                    for (String kv : line.split(",")) {
                        String[] parts = kv.split("=", 2);
                        if (parts.length == 2 && parts[0].trim().equalsIgnoreCase(field)) {
                            return parts[1].trim();
                        }
                    }
                    return "I found your record, but couldn’t locate your " + field;
                }
            }
            return null; //"I couldn’t find a user with the name " + name +".";
        } catch (IOException e) {
            return "Sorry, I encountered an error while reading the user data.";
        }
    }


    //Tokenize input and match in order
    private boolean isWholeWordMatch(String keyword, String[] inputWords) {
        String[] keyWords = keyword.split("\\s+");
        if (keyWords.length > inputWords.length) {
            return false;
        }

        for (int i = 0; i <= inputWords.length - keyWords.length; i++) {
            boolean match = true;
            for (int j = 0; j < keyWords.length; j++) {
                if (!keyWords[j].equals(inputWords[i + j])) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }
}
