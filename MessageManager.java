package app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MessageManager {

    public static final String SENT = "Sent";
    public static final String STORED = "Stored";
    public static final String DISREGARD = "Disregard";

    private final ArrayList<Message> sentMessages = new ArrayList<>();
    private final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private final ArrayList<Message> storedMessages = new ArrayList<>();
    private final ArrayList<String> messageHashes = new ArrayList<>();
    private final ArrayList<String> messageIDs = new ArrayList<>();

    private int nextMessageNumber;
    private final Path jsonPath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public MessageManager(String jsonPath) {
        this.jsonPath = Path.of(jsonPath);
    }

    public static boolean isValidRecipient(String recipient) {
        return Login.isValidCellNumber(recipient);
    }

    public static boolean isValidMessage(String text) {
        return text != null && !text.trim().isEmpty() && text.length() <= 250;
    }

    public static String generateId() {
        StringBuilder id = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            id.append(ThreadLocalRandom.current().nextInt(10));
        }
        return id.toString();
    }

    public Message addMessage(String recipient, String text, String flag) {
        return addMessage(generateId(), recipient, text, flag);
    }

    public Message addMessage(String id, String recipient, String text, String flag) {
        if (!SENT.equals(flag) && !DISREGARD.equals(flag) && !STORED.equals(flag)) {
            throw new IllegalArgumentException("Unknown message status: " + flag);
        }
        Message message = new Message(id, nextMessageNumber, recipient, text, flag);
        nextMessageNumber++;
        messageHashes.add(message.getHash());
        messageIDs.add(message.getMessageId());

        switch (flag) {
            case SENT -> sentMessages.add(message);
            case DISREGARD -> disregardedMessages.add(message);
            case STORED -> {
                storedMessages.add(message);
                saveStoredToJson();
            }
            default -> throw new IllegalStateException("Validated message status was not handled.");
        }
        return message;
    }

    public void saveStoredToJson() {
        try {
            Path parent = jsonPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer writer = Files.newBufferedWriter(jsonPath)) {
                gson.toJson(storedMessages, writer);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write stored messages to " + jsonPath, e);
        }
    }

    public void loadStoredFromJson() {
        if (Files.notExists(jsonPath)) {
            return;
        }

        Type listType = new TypeToken<ArrayList<Message>>() { }.getType();
        try (Reader reader = Files.newBufferedReader(jsonPath)) {
            List<Message> loaded = gson.fromJson(reader, listType);
            if (loaded == null) {
                return;
            }
            storedMessages.clear();
            for (Message message : loaded) {
                storedMessages.add(message);
                if (!messageHashes.contains(message.getHash())) {
                    messageHashes.add(message.getHash());
                }
                if (!messageIDs.contains(message.getMessageId())) {
                    messageIDs.add(message.getMessageId());
                }
                nextMessageNumber = Math.max(nextMessageNumber, message.getMessageNumber() + 1);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read stored messages from " + jsonPath, e);
        }
    }

    public String displayStoredSendersAndRecipients() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder result = new StringBuilder();
        for (Message message : storedMessages) {
            result.append("Sender: Developer | Recipient: ").append(message.getRecipient())
                    .append(System.lineSeparator());
        }
        return result.toString().trim();
    }

    public String displayLongestStoredMessage() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        Message longest = storedMessages.get(0);
        for (Message message : storedMessages) {
            if (message.getText().length() > longest.getText().length()) {
                longest = message;
            }
        }
        return longest.getText();
    }

    public String searchByMessageId(String id) {
        for (Message message : allMessages()) {
            if (message.getMessageId().equals(id)) {
                return message.getText();
            }
        }
        return "Message ID not found.";
    }

    public String searchByMessageIdDetails(String id) {
        for (Message message : allMessages()) {
            if (message.getMessageId().equals(id)) {
                return "Recipient: " + message.getRecipient() + " | Message: " + message.getText();
            }
        }
        return "Message ID not found.";
    }

    public ArrayList<String> searchStoredByRecipient(String recipient) {
        ArrayList<String> results = new ArrayList<>();
        for (Message message : storedMessages) {
            if (message.getRecipient().equals(recipient)) {
                results.add(message.getText());
            }
        }
        return results;
    }

    public String deleteByHash(String hash) {
        for (int i = 0; i < storedMessages.size(); i++) {
            Message message = storedMessages.get(i);
            if (message.getHash().equals(hash)) {
                storedMessages.remove(i);
                messageHashes.remove(hash);
                messageIDs.remove(message.getMessageId());
                saveStoredToJson();
                return "Message: \"" + message.getText() + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    public String displayReport() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder report = new StringBuilder("===== STORED MESSAGES REPORT =====\n");
        int number = 1;
        for (Message message : storedMessages) {
            report.append("\nMessage ").append(number++).append("\n")
                    .append("  Message Hash: ").append(message.getHash()).append("\n")
                    .append("  Message ID:   ").append(message.getMessageId()).append("\n")
                    .append("  Recipient:    ").append(message.getRecipient()).append("\n")
                    .append("  Message:      ").append(message.getText()).append("\n");
        }
        return report.toString();
    }

    private ArrayList<Message> allMessages() {
        ArrayList<Message> all = new ArrayList<>(sentMessages);
        all.addAll(storedMessages);
        all.addAll(disregardedMessages);
        return all;
    }

    private static ArrayList<String> texts(List<Message> messages) {
        ArrayList<String> result = new ArrayList<>();
        for (Message message : messages) {
            result.add(message.getText());
        }
        return result;
    }

    public ArrayList<String> getSentMessageTexts() {
        return texts(sentMessages);
    }

    public ArrayList<String> getStoredMessageTexts() {
        return texts(storedMessages);
    }

    public ArrayList<String> getDisregardedMessageTexts() {
        return texts(disregardedMessages);
    }

    public ArrayList<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public ArrayList<Message> getStoredMessages() {
        return new ArrayList<>(storedMessages);
    }

    public ArrayList<Message> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages);
    }

    public ArrayList<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public ArrayList<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }
}
