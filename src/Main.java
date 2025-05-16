import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)){
            ChatBot bot = new ChatBot();

        System.out.println("BankBot: Hello! I'm your banking assistant. What is your name?");
            
        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("bye")) {
                System.out.println("BankBot: Goodbye! Have a nice day.");
                break;
            }

            String response = bot.getResponse(userInput);
            System.out.println("BankBot: " + response);
        }
        
    }

    }
}
