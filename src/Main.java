import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            ChatBot bot = new ChatBot();

            System.out.println("BankBot: Hello! I'm your banking assistant. What is your name?");
            System.out.print("You: ");
            String userInput = scanner.nextLine();
            String response = bot.getResponse("my name is " + userInput); // Register name
            System.out.println("BankBot: " + response);

            String userName = userInput.trim();

            while (true) {
                System.out.print(userName + ": ");
                userInput = scanner.nextLine().trim();

                if (userInput.equalsIgnoreCase("bye")) {
                    System.out.println("BankBot: Goodbye, " + userName + "! Have a nice day.");
                    break;
                }

                // Handle learning if user uses 'learn:' format
                if (bot.handleLearning(userInput)) {
                    System.out.println("BankBot: Thank you! I've learned something new.");
                    continue;
                }

                // Normal response
                response = bot.getResponse(userInput);
                System.out.println("BankBot: " + response);

                // Optional: Guide user if fallback response was returned
                if (response.contains("I'm not sure") || response.contains("Can you teach me")) {
                    System.out.println("BankBot: You can help me learn! Use: learn: your question = your answer");
                }
            }
        }
    }
}
