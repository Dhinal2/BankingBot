import java.awt.*;
import javax.swing.*;

public class ChatBotGUI {

    private final ChatBot bot;
    private final AvatarManager avatars = new AvatarManager();

    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private String userName = "User";
    private boolean awaitingName = true;  // Track if we're waiting for name input
    private JLabel avatarLabel;
    private boolean waitingForSavingTips = false;

    

    public ChatBotGUI() {
        bot = new ChatBot();
        initGUI();
    }

    private void initGUI() {
        frame = new JFrame("BankBot - Your Banking Assistant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 600);

        // Avatar at top
        avatarLabel = new JLabel(avatars.get("happy"));
        frame.getContentPane().add(avatarLabel, BorderLayout.NORTH);

        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        frame.getContentPane().add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Input panel
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.addActionListener(_ -> sendMessage());

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(_ -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

        chatArea.append("BankBot: Hello! I'm your banking assistant. What is your name?\n");
        frame.setVisible(true);
    }

    //User inputs messages area
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

    String lower = input.toLowerCase();

    //If user says ok for saving tips when balance is lower than 1000
    if (waitingForSavingTips && (lower.equals("yes") || lower.equals("ok"))) {
        chatArea.append("BankBot: Here are some saving tips:\n");
        chatArea.append(" • Track your expenses daily\n");
        chatArea.append(" • Cut down on non-essentials\n");
        chatArea.append(" • Set small weekly savings goals\n");
        waitingForSavingTips = false;  // Reset the flag
        return;
    }
       
    // account-type request
    if (lower.contains("account type") || lower.contains("type of account")) {
        String type = bot.lookupUserField(userName, "type");

        if (type != null) {
            chatArea.append("BankBot: You have a \"" + type + "\" account.\n");
        } else {
            // Fallback to general info about account types from knowledge base
            String response = bot.getResponse("account");
            chatArea.append("BankBot: " + response + "\n");
        }
        return;
    }


    // Balance with Image
    if (lower.contains("balance")) {
    String balStr = bot.lookupUserField(userName, "balance");

    if (balStr == null) {
        // User not found
        avatarLabel.setIcon(avatars.get("sad"));
        chatArea.append("BankBot: You are currently not registered with us, so you are in Guest Mode.\n");
        chatArea.append("BankBot: Register an account with us at the nearest branch so you can enjoy our full services.\n");
        chatArea.append("BankBot: Can I help you with anything else?\n");
         // Start the timer to change back to happy for unregistered users
        new Timer(3000, _ -> avatarLabel.setIcon(avatars.get("happy"))).start();
        return;
    }

    try {
        double bal = Double.parseDouble(balStr);
        chatArea.append("BankBot: Your balance is Rs. " + balStr + "\n");

        if (bal < 1000) {
            avatarLabel.setIcon(avatars.get("sad"));
            chatArea.append("BankBot: Your balance seems a bit low. Would you like some saving tips?");
            waitingForSavingTips = true; // Set flag to wait for confirmation
            new Timer(3000, _ -> avatarLabel.setIcon(avatars.get("happy"))).start();
        } else {
            avatarLabel.setIcon(avatars.get("celebrate"));
            chatArea.append("BankBot: Wow, your balance looks healthy!\n");
            new Timer(3000, _ -> avatarLabel.setIcon(avatars.get("happy"))).start();
        }
    } catch (NumberFormatException ex) {
        chatArea.append("BankBot: Hmm, something's wrong with your balance record.\n");
        new Timer(3000, _ -> avatarLabel.setIcon(avatars.get("happy"))).start();
    }
    return;
}

    // account-number request
    if (lower.contains("account number")) {
        String acc = bot.lookupUserField(userName, "accountNumber");
        chatArea.append("BankBot: Your account number is " + acc + "\n");
        return;
    }
    // branch request
    if (lower.contains("branch")) {
        String br = bot.lookupUserField(userName, "branch");
        chatArea.append("BankBot: Your branch is " + br + "\n");
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
