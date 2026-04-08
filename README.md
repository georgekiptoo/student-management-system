# Student Management System 🎓

## 📌 Descriere
Aplicație desktop Java pentru gestionarea evidenței studenților, construită cu Swing și SQLite.
Suportă adăugarea, editarea, ștergerea, căutarea și filtrarea studenților, cu export CSV
și un set de date demo la prima rulare.

## 🚀 Funcționalități
- Interfață grafică completă cu Java Swing
- Bază de date SQLite locală (fără server)
- Adăugare, editare și ștergere înregistrări studenți
- Căutare și filtrare după nume, notă, materie și scor
- Export date în format CSV
- Date demo inserate automat la prima rulare
- Afișare statistici bază de date (total, medie, max, min)

## 🛠️ Tehnologii
- Java 11+
- Java Swing (`JFrame`, `JTable`, `JPanel`, `JDialog`)
- SQLite via JDBC (`sqlite-jdbc-3.51.3.0.jar`)
- `PreparedStatement` pentru operații SQL sigure

## ▶️ Cum se rulează

1. Descarcă SQLite JDBC driver:
[sqlite-jdbc-3.51.3.0.jar](https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.1.0/sqlite-jdbc-3.51.3.0.jar)
și pune-l în folderul rădăcină al proiectului.

2. Clonează repository-ul:
```bash
git clone https://github.com/georgekiptoo/student-management-system.git
cd student-management-system
```

3. Compilează proiectul:

**Mac/Linux:**
```bash
javac -cp .:sqlite-jdbc-3.51.3.0.jar -sourcepath src src/Main.java -d out
```
**Windows:**
```bash
javac -cp .;sqlite-jdbc-3.51.3.0.jar -sourcepath src src/Main.java -d out
```

4. Rulează aplicația:

**Mac/Linux:**
```bash
java -cp out:sqlite-jdbc-3.51.3.0.jar Main
```
**Windows:**
```bash
java -cp out;sqlite-jdbc-3.51.3.0.jar Main
```

> **Cerințe:** Java 11 sau mai nou instalat pe sistem.

## 📷 Capturi de ecran
*(Adaugă capturi de ecran ale aplicației în rulare)*

## 💡 Ce am învățat
- **Java Swing & GUI**: Am construit o interfață grafică completă folosind `JFrame`, `JTable`, `JPanel` și layout managers pentru organizarea componentelor vizual.
- **Conectare la bază de date**: Am integrat SQLite prin JDBC, gestionând conexiunea, crearea tabelelor și operațiile CRUD cu `PreparedStatement`.
- **Arhitectură MVC**: Am separat logica bazei de date (`DatabaseManager`) de modelul de date (`Student`) și interfața grafică (`StudentManagerUI`).
- **Filtrare dinamică SQL**: Am construit interogări SQL dinamice cu parametri opționali pentru căutare și filtrare combinată.
- **Export CSV**: Am implementat exportul datelor într-un fișier CSV folosind `FileWriter`.
- **Thread safety în Swing**: Am folosit `SwingUtilities.invokeLater` pentru lansarea corectă a aplicației pe Event Dispatch Thread.

## Author
George K. — CS2 Final Project — Transilvania University of Brașov — 2025
