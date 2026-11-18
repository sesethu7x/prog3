package app;

import java.util.ArrayList;
import java.util.Scanner;

public class App {

    private static final String STORAGE_FILE = "storedMessages.json";

    private App() {
    }

    public static void main(String[] args) {
        MessageManager manager = new MessageManager(STORAGE_FILE);
        manager.loadStoredFromJson();

        try (Scanner scanner = new Scanner(System.in)) {
            Login user = register(scanner);
            login(scanner, user);
            runMenu(scanner, manager);
        }
    }

    private static Login register(Scanner scanner) {
        while (true) {
            System.out.println("=== Register ===");
            System.out.print("First name: ");
            String firstName = scanner.nextLine().trim();
            System.out.print("Last name: ");
            String lastName = scanner.nextLine().trim();
            System.out.print("Username (must contain '_' and be <= 5 characters): ");
            String username = scanner.nextLine().trim();
            System.out.print("Password (8+ characters, uppercase, number, special character): ");
            String password = scanner.nextLine();
            System.out.print("Cell number (international or 10-digit local): ");
            String cellNumber = scanner.nextLine().trim();

            Login user = new Login(firstName, lastName, username, password, cellNumber);
            System.out.println(user.registerUser());
            if (user.checkUserName() && user.checkPasswordComplexity() && user.checkCellPhoneNumber()) {
                return user;
            }
            System.out.println("\nPlease try registering again.\n");
        }
    }

    private static void login(Scanner scanner, Login user) {
        System.out.println("\n=== Login ===");
        while (true) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            boolean loggedIn = user.loginUser(username, password);
            System.out.println(user.returnLoginStatus());
            if (loggedIn) {
                return;
            }
        }
    }

    private static void runMenu(Scanner scanner, MessageManager manager) {
        boolean running = true;
        System.out.println("\nWelcome to QuickChat.");
        while (running) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1) Send messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored messages");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> sendMessages(scanner, manager);
                case "2" -> showSentMessages(manager);
                case "3" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                case "4" -> storedMessagesMenu(scanner, manager);
                default -> System.out.println("Invalid option, try again.");
            }
        }
    }

    private static void sendMessages(Scanner scanner, MessageManager manager) {
        int count = readNonNegativeInt(scanner, "How many messages do you want to enter? ");
        for (int i = 0; i < count; i++) {
            System.out.println("\n--- Message " + (i + 1) + " of " + count + " ---");
            String recipient = readRecipient(scanner);
            String text = readMessage(scanner);
            String status = readMessageStatus(scanner);
            Message message = manager.addMessage(recipient, text, status);
            System.out.println("Message " + status.toLowerCase() + ". ID: " + message.getMessageId()
                    + " | Hash: " + message.getHash());
        }
    }

    private static String readRecipient(Scanner scanner) {
        while (true) {
            System.out.print("Recipient cell number (international or 10-digit local): ");
            String recipient = scanner.nextLine().trim();
            if (MessageManager.isValidRecipient(recipient)) {
                return recipient;
            }
            System.out.println("Invalid number. Use an international number or a 10-digit local number.");
        }
    }

    private static String readMessage(Scanner scanner) {
        while (true) {
            System.out.print("Message (1-250 characters): ");
            String text = scanner.nextLine();
            if (MessageManager.isValidMessage(text)) {
                return text;
            }
            System.out.println("Message must contain 1-250 non-whitespace characters.");
        }
    }

    private static String readMessageStatus(Scanner scanner) {
        while (true) {
            System.out.print("1) Send  2) Disregard  3) Store -> ");
            switch (scanner.nextLine().trim()) {
                case "1":
                    return MessageManager.SENT;
                case "2":
                    return MessageManager.DISREGARD;
                case "3":
                    return MessageManager.STORED;
                default:
                    System.out.println("Invalid option. Choose 1, 2, or 3.");
            }
        }
    }

    private static void showSentMessages(MessageManager manager) {
        ArrayList<Message> sentMessages = manager.getSentMessages();
        if (sentMessages.isEmpty()) {
            System.out.println("No messages have been sent yet.");
            return;
        }
        sentMessages.forEach(System.out::println);
    }

    private static void storedMessagesMenu(Scanner scanner, MessageManager manager) {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== STORED MESSAGES =====");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID");
            System.out.println("d) Search for all messages stored for a recipient");
            System.out.println("e) Delete a message using its hash");
            System.out.println("f) Display report of all stored messages");
            System.out.println("g) Back to main menu");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim().toLowerCase()) {
                case "a" -> System.out.println(manager.displayStoredSendersAndRecipients());
                case "b" -> System.out.println(manager.displayLongestStoredMessage());
                case "c" -> {
                    System.out.print("Enter message ID: ");
                    System.out.println(manager.searchByMessageIdDetails(scanner.nextLine().trim()));
                }
                case "d" -> {
                    System.out.print("Enter recipient: ");
                    ArrayList<String> messages = manager.searchStoredByRecipient(scanner.nextLine().trim());
                    if (messages.isEmpty()) {
                        System.out.println("No stored messages for that recipient.");
                    } else {
                        messages.forEach(System.out::println);
                    }
                }
                case "e" -> {
                    System.out.print("Enter message hash: ");
                    System.out.println(manager.deleteByHash(scanner.nextLine().trim()));
                }
                case "f" -> System.out.println(manager.displayReport());
                case "g" -> back = true;
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static int readNonNegativeInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Continue to the shared input error message.
            }
            System.out.println("Please enter a whole number greater than or equal to 0.");
        }
    }
}
