
# 🎯 SmartQuiz - Java Swing + SQLite (Maven)

SmartQuiz is a simple **desktop quiz application** built using **Java Swing** for the UI and **SQLite** (via JDBC) for data storage.  
It comes with an **Admin Panel** for adding quiz questions and a **User Panel** for taking quizzes.

---

## 📌 Features
- Admin panel to **add quiz questions**
- User registration & login
- Randomized quiz questions
- Score tracking
- SQLite local database (auto-created on first run)
- Maven build system for easy dependency management

---

## 🛠 Tech Stack
- **Java 17+**
- **Java Swing** (UI)
- **SQLite** (Database)
- **JDBC** (Database connectivity)
- **Maven** (Build & Dependency Management)

---

## 🚀 How to Run

### 1️⃣ Prerequisites
- **Java 17+** installed → [Download Java](https://adoptium.net/)
- **Maven** installed → [Download Maven](https://maven.apache.org/download.cgi)

Check versions:
```bash
java -version
mvn -version
```

---

### 2️⃣ Download & Extract
- Download the `smartquiz.zip` file
- Extract it to your desired folder

---

### 3️⃣ Build the Project
In the project root (where `pom.xml` is located), run:
```bash
mvn clean package
```
This will create a runnable JAR file inside:
```
target/smartquiz-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

### 4️⃣ Run the Application
```bash
java -jar target/smartquiz-1.0-SNAPSHOT-jar-with-dependencies.jar
```
On first run:
- The file `quiz_app.db` will be created in the project root
- Required tables will be generated automatically

---

## 🔑 Default Admin Login
- **Username:** `admin`
- **Password:** `admin`

You can then add questions using the Admin Panel.

---

## 📝 Notes
- **Security:** Passwords are stored in plain text for simplicity.  
  In a real application, use **password hashing**.
- To change the number of quiz questions, modify:
```java
fetchRandomQuestions(5)
```
in `UserPanel.java`.

---

## 📦 Running in VS Code
1. Open VS Code
2. Install **Java Extension Pack** and **Maven for Java** extensions
3. Open the project folder
4. Right-click `Main.java` → **Run Java**

---

## 📜 License
This is a demo application. You can use and modify it freely.

---

💡 **Tip:** Use SQLite Browser to inspect or edit the `quiz_app.db` database.
