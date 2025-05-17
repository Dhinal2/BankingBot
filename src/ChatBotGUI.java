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
    

    public ChatBotGUI() {
        bot = new ChatBot();
        initGUI();
    }

    private void initGUI() {
        frame = new JFrame("BankBot - Your Banking Assistant");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);

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
        inputField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

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

        String lower = input.toLowerCase();

    // account-type request
    if (lower.contains("account type") || lower.contains("type of account")) {
        String type = bot.lookupUserField(userName, "type");
        chatArea.append("BankBot: You have a “" + type + "” account.\n");
        return;
    }


    //Balance with Image 
    if (lower.contains("balance")) {
        String balStr = bot.lookupUserField(userName, "balance");
        double bal = Double.parseDouble(balStr);
        chatArea.append("BankBot: Your balance is Rs. " + balStr + "\n");
    
        if (bal < 1000) {
            // Sad mood + tips
            avatarLabel.setIcon(new ImageIcon("avatar_sad.png"));
            chatArea.append("BankBot: I’m sorry to hear that. Here are some saving tips:\n");
            chatArea.append(" • Track your expenses daily\n");
            chatArea.append(" • Cut down on non-essentials\n");
            chatArea.append(" • Set small weekly savings goals\n");
        } else {
            // Celebrating mood
            avatarLabel.setIcon(new ImageIcon("cel.png"));
            chatArea.append("BankBot: Wow, your balance looks healthy! 🎉\n");
        }
        // After a short delay, return to happy face
        new Timer(3000, e -> avatarLabel.setIcon(new ImageIcon("avatar_happy.png")))
          .start();
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

    // Ask for balance
    if (lower.contains("balance")) {
        String result = bot.getUserInfo(userName, "balance");
        chatArea.append("BankBot: Your current balance is Rs. " + result + "\n");
        return;
    }

    // Ask for account number
    if (lower.contains("account number")) {
        String result = bot.getUserInfo(userName, "accountNumber");
        chatArea.append("BankBot: Your account number is " + result + "\n");
        return;
    }

    // Ask for branch
    if (lower.contains("branch")) {
        String result = bot.getUserInfo(userName, "branch");
        chatArea.append("BankBot: Your branch is " + result + "\n");
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
