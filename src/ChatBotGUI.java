import java.awt.*;
import javax.swing.*;

public class ChatBotGUI {

    private final ChatBot bot;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private String userName = "User";
    private boolean awaitingName = true;  // Track if we're waiting for name input

    public ChatBotGUI() {
        bot = new ChatBot();
        initGUI();
    }

    private void initGUI() {
        frame = new JFrame("BankBot - Your Banking Assistant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

        // Initial greeting
        chatArea.append("BankBot: Hello! I'm your banking assistant. What is your name?\n");

        frame.setVisible(true);
    }

    private void sendMessage() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) return;

        chatArea.append(userName + ": " + input + "\n");
        inputField.setText("");

        // Handle exit
        if (input.equalsIgnoreCase("bye")) {
            chatArea.append("BankBot: Goodbye, " + userName + "! Have a nice day.\n");
            inputField.setEnabled(false);
            return;
        }

        // Handle name assignment only once
        if (awaitingName) {
            userName = input;
            String response = bot.getResponse("my name is " + userName);
            chatArea.append("BankBot: " + response + "\n");
            chatArea.append("BankBot: How can I help you today?\n");
            awaitingName = false; // name has been set
            return;
        }

        // Handle learning
        if (bot.handleLearning(input)) {
            chatArea.append("BankBot: Thank you! I've learned something new.\n");
            return;
        }

        // Normal response
        String response = bot.getResponse(input);
        chatArea.append("BankBot: " + response + "\n");

        // Hint if fallback
        if (response.contains("I'm not sure") || response.contains("teach me")) {
            chatArea.append("BankBot: You can help me learn! Use: learn: your question = your answer\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatBotGUI::new);
    }
}
