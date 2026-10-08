package com.studyplanner.util;

import com.studyplanner.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to inspect and display database tables, schemas, and live records
 * directly in the console for evaluations, demonstrations, and debugging.
 */
public class DatabaseViewer {

    private static final String[] TABLES = {
            "Student",
            "Subject",
            "Exam",
            "StudyTask",
            "Progress",
            "StudySession"
    };

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("                   AI STUDY PLANNER - DATABASE INSPECTOR                       ");
        System.out.println("================================================================================");

        DatabaseConnection db = DatabaseConnection.getInstance();
        System.out.println("Database Engine: " + db.getCurrentDatabaseType());
        System.out.println("Status Message : " + db.getConnectionStatusMessage());
        System.out.println("MySQL Active   : " + db.isUsingMySQL());
        System.out.println("================================================================================\n");

        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String table : TABLES) {
                printTableData(stmt, table);
            }

            System.out.println("================================================================================");
            System.out.println("                ALL TABLES AND RECORDS SUCCESSFULLY DISPLAYED                  ");
            System.out.println("================================================================================");

        } catch (Exception e) {
            System.err.println("[DatabaseViewer Error] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printTableData(Statement stmt, String tableName) {
        System.out.println(">>> TABLE: [" + tableName + "]");
        try (ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            List<String> headers = new ArrayList<>();
            List<Integer> colWidths = new ArrayList<>();

            for (int i = 1; i <= colCount; i++) {
                String colName = meta.getColumnName(i);
                headers.add(colName);
                colWidths.add(Math.max(colName.length(), 10));
            }

            List<List<String>> rows = new ArrayList<>();
            while (rs.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= colCount; i++) {
                    String val = rs.getString(i);
                    if (val == null) val = "NULL";
                    row.add(val);
                    colWidths.set(i - 1, Math.max(colWidths.get(i - 1), Math.min(val.length(), 40)));
                }
                rows.add(row);
            }

            // Print Header Border
            printBorder(colWidths);

            // Print Column Headers
            StringBuilder headerLine = new StringBuilder("|");
            for (int i = 0; i < colCount; i++) {
                headerLine.append(" ").append(padRight(headers.get(i), colWidths.get(i))).append(" |");
            }
            System.out.println(headerLine);

            printBorder(colWidths);

            // Print Rows
            if (rows.isEmpty()) {
                System.out.println("|  (No records found in this table)  |");
            } else {
                for (List<String> row : rows) {
                    StringBuilder rowLine = new StringBuilder("|");
                    for (int i = 0; i < colCount; i++) {
                        String cell = row.get(i);
                        if (cell.length() > 40) {
                            cell = cell.substring(0, 37) + "...";
                        }
                        rowLine.append(" ").append(padRight(cell, colWidths.get(i))).append(" |");
                    }
                    System.out.println(rowLine);
                }
            }

            printBorder(colWidths);
            System.out.println("Total Rows: " + rows.size() + "\n");

        } catch (Exception e) {
            System.out.println("  [Notice] Table " + tableName + " query failed: " + e.getMessage() + "\n");
        }
    }

    private static void printBorder(List<Integer> widths) {
        StringBuilder border = new StringBuilder("+");
        for (int w : widths) {
            border.append("-".repeat(w + 2)).append("+");
        }
        System.out.println(border);
    }

    private static String padRight(String s, int n) {
        if (s.length() >= n) return s;
        return s + " ".repeat(n - s.length());
    }
}
