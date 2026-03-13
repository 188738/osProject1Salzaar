import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Driver {
    private static PrintWriter encIn;
    private static BufferedReader encOut;
    private static PrintWriter logIn;
    private static ArrayList<String> history = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Driver <logfile>");
            System.exit(1);
        }

        String logFile = args[0];

        ProcessBuilder loggerPB = new ProcessBuilder("java", "Logger", logFile);
        loggerPB.redirectErrorStream(true);
        Process loggerProcess = loggerPB.start();
        logIn = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(loggerProcess.getOutputStream())), true);

        ProcessBuilder encPB = new ProcessBuilder("java", "Encryption");
        encPB.redirectErrorStream(false);
        Process encProcess = encPB.start();
        encIn = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(encProcess.getOutputStream())), true);
        encOut = new BufferedReader(new InputStreamReader(encProcess.getInputStream()));

        log("START", "Driver started");

        boolean running = true;
        while (running) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. password");
            System.out.println("2. encrypt");
            System.out.println("3. decrypt");
            System.out.println("4. history");
            System.out.println("5. quit");
            System.out.print("Enter command: ");

            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "password": case "1":
                    handlePassword();
                    break;
                case "encrypt": case "2":
                    handleEncrypt();
                    break;
                case "decrypt": case "3":
                    handleDecrypt();
                    break;
                case "history": case "4":
                    showHistory();
                    break;
                case "quit": case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Unknown command.");
            }
        }

        log("QUIT", "Driver exiting");
        encIn.println("QUIT");
        logIn.println("QUIT");
        encProcess.waitFor();
        loggerProcess.waitFor();
        System.out.println("Goodbye!");
    }

    private static void handlePassword() throws Exception {
        String input = getStringFromUserOrHistory("password");
        if (input == null) return;
        if (!input.matches("[a-zA-Z]+")) {
            System.out.println("Error: Only letters allowed.");
            return;
        }
        encIn.println("PASSKEY " + input.toUpperCase());
        String response = encOut.readLine();
        log("PASSWORD", "Password set");
        System.out.println("Password set successfully.");
    }

    private static void handleEncrypt() throws Exception {
        String input = getStringFromUserOrHistory("encrypt");
        if (input == null) return;
        if (!input.matches("[a-zA-Z]+")) {
            System.out.println("Error: Only letters allowed.");
            return;
        }
        if (!history.contains(input)) history.add(input);
        log("ENCRYPT", "Encrypting string");
        encIn.println("ENCRYPT " + input.toUpperCase());
        String response = encOut.readLine();
        if (response.startsWith("RESULT")) {
            String result = response.substring(7);
            System.out.println("Encrypted: " + result);
            history.add(result);
            log("RESULT", "Encryption result: " + result);
        } else {
            System.out.println("Error: " + response);
            log("ERROR", response);
        }
    }

    private static void handleDecrypt() throws Exception {
        String input = getStringFromUserOrHistory("decrypt");
        if (input == null) return;
        if (!input.matches("[a-zA-Z]+")) {
            System.out.println("Error: Only letters allowed.");
            return;
        }
        if (!history.contains(input)) history.add(input);
        log("DECRYPT", "Decrypting string");
        encIn.println("DECRYPT " + input.toUpperCase());
        String response = encOut.readLine();
        if (response.startsWith("RESULT")) {
            String result = response.substring(7);
            System.out.println("Decrypted: " + result);
            history.add(result);
            log("RESULT", "Decryption result: " + result);
        } else {
            System.out.println("Error: " + response);
            log("ERROR", response);
        }
    }

    private static void showHistory() {
        if (history.isEmpty()) {
            System.out.println("History is empty.");
            return;
        }
        System.out.println("--- History ---");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". " + history.get(i));
        }
        log("HISTORY", "History displayed");
    }

    // Returns a string either from history or new input
    private static String getStringFromUserOrHistory(String context) {
        if (!history.isEmpty()) {
            System.out.println("1. Enter new string");
            System.out.println("2. Use string from history");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("2")) {
                showHistory();
                System.out.print("Select number: ");
                try {
                    int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
                    if (index >= 0 && index < history.size()) {
                        return history.get(index);
                    } else {
                        System.out.println("Invalid selection.");
                        return null;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input.");
                    return null;
                }
            }
        }
        System.out.print("Enter string for " + context + ": ");
        return scanner.nextLine().trim();
    }

    private static void log(String action, String message) {
        logIn.println(action + " " + message);
    }
}