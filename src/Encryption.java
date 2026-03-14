import java.util.Scanner;

public class Encryption {
    private static String passkey = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            int spaceIndex = line.indexOf(' ');
            String command = (spaceIndex == -1) ? line : line.substring(0, spaceIndex);
            String argument = (spaceIndex == -1) ? "" : line.substring(spaceIndex + 1);

            switch (command) {
                case "PASSKEY":
                    passkey = argument;
                    System.out.println("RESULT");
                    System.out.flush();
                    break;

                case "ENCRYPT":
                    if (passkey == null || passkey.isEmpty()) {
                        System.out.println("ERROR Password not set");
                    } else {
                        System.out.println("RESULT " + vigenereEncrypt(argument, passkey));
                    }
                    System.out.flush();
                    break;

                case "DECRYPT":
                    if (passkey == null || passkey.isEmpty()) {
                        System.out.println("ERROR Password not set");
                    } else {
                        System.out.println("RESULT " + vigenereDecrypt(argument, passkey));
                    }
                    System.out.flush();
                    break;

                case "QUIT":
                    return;

                default:
                    System.out.println("ERROR Unknown command: " + command);
                    System.out.flush();
            }
        }
    }

    private static String vigenereEncrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                int textVal = Character.toUpperCase(c) - 'A';
                int keyVal  = Character.toUpperCase(key.charAt(keyIndex % key.length())) - 'A';
                result.append((char) ('A' + (textVal + keyVal) % 26));
                keyIndex++;
            }
        }
        return result.toString();
    }

    private static String vigenereDecrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                int textVal = Character.toUpperCase(c) - 'A';
                int keyVal  = Character.toUpperCase(key.charAt(keyIndex % key.length())) - 'A';
                result.append((char) ('A' + (textVal - keyVal + 26) % 26));
                keyIndex++;
            }
        }
        return result.toString();
    }
}