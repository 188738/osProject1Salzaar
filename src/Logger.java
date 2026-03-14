import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Logger {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: java Logger <logfile>");
            System.exit(1);
        }

        String logFile = args[0];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
             Scanner scanner = new Scanner(System.in)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                int spaceIndex = line.indexOf(' ');
                String action  = (spaceIndex == -1) ? line : line.substring(0, spaceIndex);
                String message = (spaceIndex == -1) ? "" : line.substring(spaceIndex + 1);

                String timestamp = LocalDateTime.now().format(formatter);
                writer.println(timestamp + " [" + action + "] " + message);
                writer.flush();

                if (action.equals("QUIT")) break;
            }
        }
    }
}