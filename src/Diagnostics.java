
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Diagnostics {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/agro_connect?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "";

        System.out.println("Diagnostics: Connecting to " + url);
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✓ Connected to database!");

            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "%", new String[] { "TABLE" });

            List<String> foundTables = new ArrayList<>();
            System.out.println("\nFound Tables:");
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println(" - " + tableName);
                foundTables.add(tableName.toLowerCase());
            }

            String[] required = { "farmer", "merchant", "product", "orders" };
            boolean missing = false;
            for (String req : required) {
                if (!foundTables.contains(req)) {
                    System.out.println("✗ MISSING TABLE: " + req);
                    missing = true;
                }
            }

            if (missing) {
                System.out.println("\nCONCLUSION: Database schema is incomplete. Please import 'schema.sql'.");
            } else {
                System.out.println("\nCONCLUSION: Database schema looks correct.");
            }

        } catch (SQLException e) {
            System.out.println("✗ Connection Failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
