# Student Management System (Java + MySQL)

## Features
- Add a student
- View all students in a JTable
- Search by student ID or part of name
- Update selected student
- Delete a student after confirmation
- Input validation and parameterized SQL statements

## Requirements
- JDK 8 or later
- MySQL Server
- NetBeans (or another Java IDE)
- MySQL Connector/J JDBC driver

## Setup
1. Start MySQL Server.
2. Open `database.sql` in MySQL Workbench and execute it.
3. In `StudentManagementSystem.java`, set `PASSWORD` to your MySQL root password.
   If your MySQL account uses another username, change `USER` too.
4. Add MySQL Connector/J to the project:
   - Download MySQL Connector/J from the official MySQL website.
   - NetBeans: right-click project > Properties > Libraries > Add JAR/Folder.
   - Select the downloaded `mysql-connector-j-<version>.jar`.
5. Create a Java Application project named `StudentManagementSystem` in NetBeans.
6. Put `StudentManagementSystem.java` in the Source Packages (default package).
7. Run the file.

## If using command line
Place the MySQL Connector/J JAR in the same folder as the Java file, then compile/run:

Windows:
javac -cp ".;mysql-connector-j-<version>.jar" StudentManagementSystem.java
java -cp ".;mysql-connector-j-<version>.jar" StudentManagementSystem

Replace `<version>` with the actual downloaded JAR filename.

## Expected workflow
- Add Student: enter name, department, email and phone, then click Add Student. ID is auto-generated.
- Select a row to load its values into the fields.
- Update: edit fields and click Update.
- Delete: select a row and click Delete; confirm the prompt.
- Search: enter a numeric ID or part of a name and click Search.
- Refresh or Show All displays all records.

## Troubleshooting
- `No suitable driver`: MySQL Connector/J JAR is not added to the project.
- `Access denied for user`: correct USER/PASSWORD in the Java file.
- `Unknown database student_db`: execute `database.sql`.
- `Communications link failure`: start MySQL Server and verify port 3306.
