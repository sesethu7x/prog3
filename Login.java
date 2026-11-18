package app;

import java.util.ArrayList;
import java.util.List;

public class Login {

    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private final String cellPhoneNumber;

    private static final List<Login> registeredUsers = new ArrayList<>();

    private boolean loginSuccessful;
    private String loggedInFirstName;
    private String loggedInLastName;

    public Login(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public boolean checkUserName() {
        return username != null && username.contains("_") && username.length() <= 5;
    }

    public boolean checkPasswordComplexity() {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        return hasUpper && hasDigit && hasSpecial;
    }

    public static boolean isValidCellNumber(String number) {
        return number != null
                && (number.matches("^\\+\\d{1,3}\\d{9}$") || number.matches("^0\\d{9}$"));
    }

    public boolean checkCellPhoneNumber() {
        return isValidCellNumber(cellPhoneNumber);
    }

    public String registerUser() {
        boolean userOk = checkUserName();
        boolean passOk = checkPasswordComplexity();
        boolean cellOk = checkCellPhoneNumber();

        StringBuilder message = new StringBuilder();
        message.append(userOk
                ? "Username successfully captured."
                : "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
        message.append(System.lineSeparator());
        message.append(passOk
                ? "Password successfully captured."
                : "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
        message.append(System.lineSeparator());
        message.append(cellOk
                ? "Cell phone number successfully added."
                : "Cell number is incorrectly formatted; use an international number or a 10-digit local number.");

        if (userOk && passOk && cellOk) {
            registeredUsers.add(this);
        }

        return message.toString();
    }

    public boolean loginUser(String usernameInput, String passwordInput) {
        for (Login user : registeredUsers) {
            if (user.username.equals(usernameInput) && user.password.equals(passwordInput)) {
                loginSuccessful = true;
                loggedInFirstName = user.firstName;
                loggedInLastName = user.lastName;
                return true;
            }
        }
        loginSuccessful = false;
        loggedInFirstName = null;
        loggedInLastName = null;
        return false;
    }

    public String returnLoginStatus() {
        if (loginSuccessful) {
            return "Welcome " + loggedInFirstName + ", " + loggedInLastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public static void clearRegisteredUsers() {
        registeredUsers.clear();
    }
}
