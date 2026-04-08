# Student Management System

A Java desktop application for managing student records, built with Swing and SQLite.
Supports adding, editing, deleting, searching, and filtering students, with CSV export
and a demo dataset on first run.

## Requirements
- Java 11 or higher
- [sqlite-jdbc-3.51.3.0.jar](https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.51.3.0.jar)

## Setup
Download the SQLite jar and place it in the project root folder.

## Compile

**Mac/Linux:**
```bash
javac -cp .:sqlite-jdbc-3.51.3.0.jar -sourcepath src src/Main.java -d out
```

**Windows:**
```bash
javac -cp .;sqlite-jdbc-3.51.3.0.jar -sourcepath src src/Main.java -d out
```

## Run

**Mac/Linux:**
```bash
java -cp out:sqlite-jdbc-3.51.3.0.jar Main
```

**Windows:**
```bash
java -cp out;sqlite-jdbc-3.51.3.0.jar Main
```

## Notes
Data is saved automatically in `students.db`
Demo data is inserted automatically on first run
