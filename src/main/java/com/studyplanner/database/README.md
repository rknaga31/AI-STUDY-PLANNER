# Database Connection Package (`com.studyplanner.database`)

This package manages JDBC connectivity, dual MySQL/SQLite engine switching, and automated schema migrations.

## Modules & Classes
- **[`DatabaseConnection.java`](file:///c:/Users/rnaga/.antigravity-ide/studyplanner/src/main/java/com/studyplanner/database/DatabaseConnection.java)**:
  - Thread-safe Singleton managing the JDBC connection lifecycle.
  - Tries MySQL connection first via `db.properties`; falls back automatically to embedded SQLite (`study_planner.db`).
  - Auto-executes `schema.sql` on initial boot.
  - Provides runtime query methods: `isUsingMySQL()`, `getCurrentDatabaseType()`.

## Packages Used
- `java.sql.*` (`Connection`, `DriverManager`, `PreparedStatement`, `Statement`, `SQLException`)
- `java.util.Properties`
- `com.mysql.cj.jdbc.Driver`
- `org.sqlite.JDBC`
