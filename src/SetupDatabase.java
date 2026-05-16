
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Scanner;

public class SetupDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/agro_connect?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "";

        System.out.println("=== Database Setup Tool ===");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✓ Connected to database!");
            Statement stmt = conn.createStatement();

            // Files to execute
            String[] files = { "database/schema.sql", "database/sample_data.sql" };

            for (String filePath : files) {
                System.out.println("\nExecuting: " + filePath);
                try {
                    String content = readFile(filePath);

                    // Split by semicolon, but be careful with delimiters.
                    // Simple split by ";" might fail if strings contain semicolon.
                    // But for this schema/sample data it should be fine.
                    String[] queries = content.split(";");

                    int count = 0;
                    for (String query : queries) {
                        query = query.trim();
                        if (query.isEmpty())
                            continue;

                        try {
                            stmt.execute(query);
                            count++;
                        } catch (Exception e) {
                            if (!query.startsWith("--")) {
                                System.err.println("Failed to execute query: "
                                        + query.substring(0, Math.min(query.length(), 50)) + "...");
                                System.err.println("Error: " + e.getMessage());
                            }
                        }
                    }
                    System.out.println("✓ Executed " + count + " statements.");

                } catch (Exception e) {
                    System.err.println("Failed to read/execute file: " + filePath);
                    e.printStackTrace();
                }
            }

            System.out.println("\n=== Database Setup Complete! ===");
            System.out.println("You can now login with:");
            System.out.println("Farmer: 9876543210 / farmer123");
            System.out.println("Merchant: 9123456789 / merchant123");

        } catch (Exception e) {
            System.err.println("✗ Setup Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String readFile(String path) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Skip comments
                if (line.trim().startsWith("--"))
                    continue;
                sb.append(line).append(" ");
            }
        }
        return sb.toString();
    }
}
