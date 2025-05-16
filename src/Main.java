import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            ChatBot bot = new ChatBot();

            System.out.println("BankBot: Hello! I'm your banking assistant. What is your name?");
            System.out.print("You: ");
            String userInput = scanner.nextLine();
            String response = bot.getResponse("my name is " + userInput); // Register name internally

            System.out.println("BankBot: " + response);

            String userName = userInput.trim();

            while (true) {
                System.out.print(userName + ": ");
                userInput = scanner.nextLine();

                if (userInput.equalsIgnoreCase("bye")) {
                    System.out.println("BankBot: Goodbye, " + userName + "! Have a nice day.");
                    break;
                }

                response = bot.getResponse(userInput);
                System.out.println("BankBot: " + response);
            }
        }
    }
}
