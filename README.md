# QuickChat

QuickChat is a Java console application for registering and logging in a user, then sending, reviewing, and managing messages.

## Features

- Register and log in with a username and password.
- Validate usernames, password complexity, and phone numbers.
- Send, disregard, or store messages.
- Generate message IDs and hashes.
- Review sent messages and manage stored messages:
  - Display stored-message recipients.
  - Find the longest stored message.
  - Search by message ID or recipient.
  - Delete a stored message by its hash.
  - Display a stored-message report.
- Save stored messages to `storedMessages.json` so they are available after restarting the app.

## Requirements

- Java JDK 25
- Apache Maven

Check that Java and Maven are available from your terminal:

```powershell
java -version
mvn -version
```

## Run the application

Open PowerShell or Command Prompt in the project folder:

```powershell
cd "C:\Users\sizwe\Desktop\chat app"
mvn exec:java "-Dexec.mainClass=app.App"
```

On Windows, you can alternatively double-click `run.bat` from File Explorer.
Maven may take longer the first time while it downloads dependencies.

## Use the application

1. Register with your first name, last name, username, password, and phone number.
2. Log in using the same username and password.
3. Use the main menu to send messages, review sent messages, open stored-message options, or quit.
4. When sending, enter the number of messages, recipient, message text, and whether to send, disregard, or store each message.
5. In the stored-message menu, choose an operation or return to the main menu.

### Input rules

- Username: must contain `_` and be no more than 5 characters (for example, `ava_1`).
- Password: at least 8 characters and must contain an uppercase letter, a digit, and a special character (for example, `Passw0rd!`).
- Phone number: either an international number such as `+27838968976` or a 10-digit local number such as `0838968976`.
- Message: 1 to 250 characters and cannot be blank.
- Message numbering starts at 0.

### Example test session

Enter each line when the corresponding prompt appears:

```text
Ava
Smith
ava_1
Passw0rd!
0838968976
ava_1
Passw0rd!
1
2
+27834557896
Hello from QuickChat!
1
0838968976
This is a stored test message.
3
2
4
f
g
3
```

The test session registers and logs in, sends one message, stores another, displays sent messages and a stored-message report, then exits.

## Run tests

From the project folder:

```powershell
mvn test
```

The tests cover registration/login validation and message creation, searching, deletion, persistence, and numbering.

## Project layout

```text
src/
  main/java/app/
    App.java             Application entry point and console menus
    Login.java           Registration and login validation
    Message.java         Message data and hash
    MessageManager.java  Message operations and JSON persistence
  test/java/app/
    LoginTest.java
    MessageManagerTest.java
pom.xml                  Maven dependencies and build configuration
run.bat                  Windows launcher
```

## Notes

- User accounts exist only in memory and must be registered again each time the app starts.
- Stored messages are written to `storedMessages.json` in the current project directory.
- This is a learning/demo console app; it is not intended for production use or to protect real credentials.
