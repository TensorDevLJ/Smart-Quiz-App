

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
=======
# SmartQuiz - Java Desktop Application

A comprehensive quiz application built with Java Swing and SQLite, featuring admin panel for question management and user interface for taking quizzes.

## 🚀 Features

### Admin Features
- **Question Management**: Add, edit, and delete quiz questions
- **Category Management**: Organize questions by categories
- **Difficulty Levels**: Set questions as Easy, Medium, or Hard
- **Question Statistics**: View total questions and categories

### User Features
- **User Registration & Login**: Secure authentication with password hashing
- **Quiz Taking**: Interactive quiz interface with timer
- **Multiple Categories**: Choose from various question categories
- **Difficulty Selection**: Select preferred difficulty level
- **Score Tracking**: View detailed results and performance history
- **Leaderboard**: Compare scores with other users

### Technical Features
- **SQLite Database**: Local database with automatic initialization
- **Password Security**: BCrypt hashing for secure password storage
- **Modern UI**: Clean Java Swing interface with system look and feel
- **Maven Build**: Easy dependency management and building
- **Error Handling**: Comprehensive error handling and user feedback

## 🛠 Tech Stack

- **Java 17+**: Core programming language
- **Java Swing**: GUI framework
- **SQLite**: Local database storage
- **JDBC**: Database connectivity
- **BCrypt**: Password hashing
- **Maven**: Build and dependency management

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

Check your versions:

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
=======
## 🚀 Installation & Setup

1. **Build the project:**
   ```bash
   mvn clean package
   ```

2. **Run the application:**
   ```bash
   java -jar target/smartquiz-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## 🔑 Default Login Credentials

**Admin Account:**
- Username: `admin`
- Password: `admin123`

## 📁 Project Structure

```
smartquiz/
├── pom.xml                          # Maven configuration
├── src/main/java/com/smartquiz/
│   ├── Main.java                    # Application entry point
│   ├── models/
│   │   ├── User.java               # User model
│   │   ├── Question.java           # Question model
│   │   └── QuizResult.java         # Quiz result model
│   ├── database/
│   │   └── DatabaseManager.java    # Database operations
│   ├── ui/
│   │   ├── LoginFrame.java         # Login interface
│   │   ├── AdminPanel.java         # Admin dashboard
│   │   ├── UserPanel.java          # User dashboard
│   │   └── QuizFrame.java          # Quiz taking interface
│   └── utils/
│       └── PasswordUtils.java      # Password utilities
└── quiz_app.db                     # SQLite database (auto-created)
```

## 🗄 Database Schema

### Users Table
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key |
| username | TEXT | Unique username |
| email | TEXT | User email |
| password_hash | TEXT | BCrypt hashed password |
| role | TEXT | 'admin' or 'user' |
| created_at | TEXT | Registration timestamp |

### Questions Table
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key |
| category | TEXT | Question category |
| question_text | TEXT | The question |
| option_a | TEXT | First option |
| option_b | TEXT | Second option |
| option_c | TEXT | Third option |
| option_d | TEXT | Fourth option |
| correct_answer | INTEGER | Correct option (0-3) |
| difficulty | TEXT | 'easy', 'medium', 'hard' |
| created_at | TEXT | Creation timestamp |

### Quiz Results Table
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER | Primary key |
| user_id | INTEGER | Foreign key to users |
| category | TEXT | Quiz category |
| score | INTEGER | Number of correct answers |
| total_questions | INTEGER | Total questions in quiz |
| time_spent | INTEGER | Time taken in seconds |
| difficulty | TEXT | Quiz difficulty |
| completed_at | TEXT | Completion timestamp |

## 🎮 How to Use

### For Administrators:
1. Login with admin credentials
2. Use the Admin Panel to:
   - Add new questions with multiple choice options
   - Set categories and difficulty levels
   - View and manage existing questions
   - Delete questions if needed

### For Users:
1. Register a new account or login
2. Select quiz preferences:
   - Choose category (or all categories)
   - Select difficulty level
   - Set number of questions
3. Take the quiz:
   - Answer questions within the time limit
   - View immediate feedback
   - See final score and performance
4. View quiz history and statistics

## 🔧 Configuration

### Changing Quiz Settings:
- **Questions per quiz**: Modify the count in `UserPanel.java`
- **Time per question**: Adjust timer in `QuizFrame.java`
- **Database location**: Change `DB_URL` in `DatabaseManager.java`

### Adding Sample Data:
The application automatically creates sample questions on first run. To add more:
1. Use the Admin Panel interface, or
2. Modify `insertSampleQuestions()` in `DatabaseManager.java`

## 🚀 Building for Distribution

Create a standalone executable JAR:
```bash
mvn clean package
```

The executable JAR will be created at:
`target/smartquiz-1.0-SNAPSHOT-jar-with-dependencies.jar`

## 🔒 Security Features

- **Password Hashing**: Uses BCrypt with salt for secure password storage
- **Input Validation**: Validates user inputs to prevent SQL injection
- **Role-based Access**: Separate admin and user interfaces
- **Session Management**: Secure user session handling

## 🐛 Troubleshooting

### Common Issues:

**Database Connection Error:**
- Ensure write permissions in the application directory
- Check if `quiz_app.db` is created successfully

**Build Failures:**
- Verify Java 17+ is installed
- Check Maven configuration
- Ensure internet connection for dependency downloads

**UI Display Issues:**
- Try different look and feel settings
- Check system compatibility with Java Swing

## 📈 Future Enhancements

- **Web Interface**: Convert to Spring Boot with REST APIs
- **Advanced Analytics**: Detailed performance metrics
- **Question Import/Export**: CSV/JSON import functionality
- **Multimedia Questions**: Support for images and audio
- **Timed Quizzes**: Full quiz time limits
- **Question Pools**: Advanced question selection algorithms

## 📄 License

This project is open source and available under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request


## 📸 Application Screenshots

![Quiz Login Screen](WhatsApp_Image_2025-08-09_at_21.10.54_e42a2209.jpg)
![Admin Panel](WhatsApp_Image_2025-08-09_at_21.11.18_677a837b.jpg)
![Add Question](WhatsApp_Image_2025-08-09_at_21.12.06_be9d7c26.jpg)
![User Quiz Taking](WhatsApp_Image_2025-08-09_at_21.12.30_27b4a040.jpg)
![Score Display](WhatsApp_Image_2025-08-09_at_21.13.38_10baa622.jpg)
![Leaderboard](WhatsApp_Image_2025-08-09_at_21.13.54_8dfaae4d.jpg)


---


**Happy Quizzing!** 🎯

