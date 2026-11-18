package app;

import java.util.Objects;

public class Message {

    private String messageId;
    private int messageNumber;
    private String recipient;
    private String text;
    private String flag;
    private String hash;

    private Message() {
    }

    public Message(String messageId, int messageNumber, String recipient, String text, String flag) {
        if (messageId == null || messageId.isBlank() || messageId.length() > 10) {
            throw new IllegalArgumentException("Message ID must contain between 1 and 10 characters.");
        }
        if (messageNumber < 0) {
            throw new IllegalArgumentException("Message number cannot be negative.");
        }
        if (!MessageManager.isValidRecipient(recipient)) {
            throw new IllegalArgumentException("Recipient phone number is invalid.");
        }
        if (!MessageManager.isValidMessage(text)) {
            throw new IllegalArgumentException("Message text must contain 1 to 250 non-whitespace characters.");
        }

        this.messageId = messageId;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.flag = Objects.requireNonNull(flag, "flag");
        this.hash = createHash(messageId, messageNumber, text);
    }

    public static String createHash(String id, int number, String text) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(text, "text");
        String prefix = id.substring(0, Math.min(2, id.length()));
        String[] words = text.trim().split("\\s+");
        String first = cleanWord(words[0]);
        String last = cleanWord(words[words.length - 1]);
        return (prefix + ":" + number + ":" + first + last).toUpperCase();
    }

    private static String cleanWord(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    public String getMessageId() {
        return messageId;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getText() {
        return text;
    }

    public String getFlag() {
        return flag;
    }

    public String getHash() {
        return hash;
    }

    @Override
    public String toString() {
        return "Hash: " + hash + " | ID: " + messageId + " | Recipient: " + recipient
                + " | Message: " + text + " | Flag: " + flag;
    }
}
