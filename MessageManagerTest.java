package app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageManagerTest {

    @TempDir
    Path tempDir;

    private MessageManager manager;
    private Message storedMessage;

    @BeforeEach
    void setUp() {
        manager = new MessageManager(tempDir.resolve("storedMessages.json").toString());
        manager.addMessage("1234567890", "+27834557896", "Did you get the cake?", MessageManager.SENT);
        storedMessage = manager.addMessage("2345678901", "+27838884567",
                "Where are you? You are late! I have asked you to be on time.", MessageManager.STORED);
        manager.addMessage("3456789012", "+27834484567", "Yohoooo, I am at your gate.", MessageManager.DISREGARD);
        manager.addMessage("0838884567", "0838884567", "It is dinner time!", MessageManager.SENT);
        manager.addMessage("5678901234", "+27838884567", "Ok, I am leaving without you.", MessageManager.STORED);
    }

    @Test
    void correctlyPopulatesSentMessages() {
        assertEquals(Arrays.asList("Did you get the cake?", "It is dinner time!"),
                manager.getSentMessageTexts());
    }

    @Test
    void numbersMessagesStartingAtZero() {
        assertEquals(0, manager.getSentMessages().get(0).getMessageNumber());
        assertEquals("23:1:WHERETIME", storedMessage.getHash());
    }

    @Test
    void displaysLongestStoredMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                manager.displayLongestStoredMessage());
    }

    @Test
    void searchesForMessageById() {
        assertEquals("It is dinner time!", manager.searchByMessageId("0838884567"));
    }

    @Test
    void searchesStoredMessagesForRecipient() {
        ArrayList<String> expected = new ArrayList<>(Arrays.asList(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."));
        assertEquals(expected, manager.searchStoredByRecipient("+27838884567"));
    }

    @Test
    void deletesStoredMessageUsingHash() {
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                manager.deleteByHash(storedMessage.getHash()));
        assertFalse(manager.getMessageHashes().contains(storedMessage.getHash()));
    }

    @Test
    void displaysReportWithStoredMessageDetails() {
        String report = manager.displayReport();
        assertTrue(report.contains(storedMessage.getHash()));
        assertTrue(report.contains("+27838884567"));
        assertTrue(report.contains("Ok, I am leaving without you."));
    }

    @Test
    void reloadsStoredMessagesAndContinuesNumbering() {
        MessageManager reloaded = new MessageManager(tempDir.resolve("storedMessages.json").toString());
        reloaded.loadStoredFromJson();

        assertEquals(2, reloaded.getStoredMessages().size());
        Message next = reloaded.addMessage("+27834557896", "A later message", MessageManager.SENT);
        assertEquals(5, next.getMessageNumber());
    }

    @Test
    void rejectsInvalidMessageStatusWithoutAddingMessage() {
        assertThrows(IllegalArgumentException.class,
                () -> manager.addMessage("+27834557896", "Test message", "Unknown"));
        assertEquals(2, manager.getSentMessages().size());
    }
}
