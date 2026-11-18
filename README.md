# Little Horizons ERP — Educational Management System

## 1. Project Title
**Little Horizons ERP** — Comprehensive Educational, Administrative, and Financial Management System for *Colegio Internacional Little Horizons S.A.S.*

---

## 2. Description
**Little Horizons ERP** is a console-based Java application backed by **MySQL 8.0+** developed to centralize and automate the daily academic, extracurricular, financial, and inventory operations of *Colegio Internacional Little Horizons S.A.S.* (serving over 500 students from preschool through 11th grade and 40 teachers).

### Problems Solved
- **Academic Data Loss:** Replaces manual paper gradebooks with a relational database enforcing valid Colombian grading scales (`0.0` to `5.0`) and automated period averages.
- **Extracurricular Disorganization:** Centralizes student enrollments in active clubs with automatic capacity validation and records event participation and competition rankings.
- **Billing & DIAN Compliance Chaos:** Automates payment registration and generates DIAN-compliant simulated electronic invoices with cryptographic **SHA-384 CUFE** codes.
- **Resource & Inventory Mismanagement:** Tracks library books, laboratory equipment, sports gear, and technology devices with transactional loan and return workflows separated for students and teachers.
- **Low Academic Visibility:** Provides executive reports powered by **Java Stream API** to detect students with high weekly absences (`> 3` absences in 7 days), overdue loans, and total revenue.

### Core Functionalities
- Complete student, guardian, and grade enrollment management (`Preescolar` to `11°`).
- Grade recording (`0.0–5.0`), historical grade queries, and daily attendance tracking (`Presente`, `Ausente`, `Tardanza`, `Excusa Médica`).
- Club catalog management with capacity limits and competitive/non-competitive event tracking.
- Payment processing with automatic DIAN electronic receipt output and SHA-384 CUFE persistence.
- Separate student (`prestamos_estudiante`) and teacher (`prestamos_profesor`) resource loan workflows with overdue detection.

---

## 3. Authors
- **Full Name:** Yojhan Vega
- **Email:** yoesmice@gmail.com
- **GitHub:** [https://github.com](https://github.com)

---

## 4. Technologies Used
- **Programming Language:** Java 21 (JDK 21 — Eclipse Temurin)
- **IDE:** IntelliJ IDEA Community Edition
- **Build & Dependency Management:** Apache Maven
- **Database Engine:** MySQL 8.0+
- **Database Administration Tool:** DBeaver CE
- **External Libraries:** `com.mysql:mysql-connector-j` (v8.4.0)

---

## 5. Prerequisites
To compile and run this project locally, ensure the following software is installed:
- **Java Development Kit (JDK):** Version 17 or higher (built and tested with **JDK 21**).
- **Database Server:** **MySQL Server 8.0 or higher is strictly required.** MySQL 8.0+ is mandatory so that column-level `CHECK` constraints (such as `CHECK (valor_nota >= 0.0 AND valor_nota <= 5.0)`) and modern indexing features are enforced properly.
- **Apache Maven:** Version 3.8+ (bundled with IntelliJ IDEA).
- **Git:** Version 2.30+ for repository cloning and version control.

---

## 6. Database Configuration

### 6.1. Database Creation
1. Open **DBeaver** or **MySQL Workbench** connected to your local MySQL 8.0+ instance.
2. Open the script located at `docs/database.sql`.
3. Execute the entire script (`Alt + X` in DBeaver). This creates the `erp_little_horizons` database (`utf8mb4_unicode_ci`) with **27 normalized tables** (the 22 core tables from the project specification plus 5 justified enhancements: `horarios_clase`, `valores_concepto_estudiante`, `obligaciones_pago`, `detalle_factura`, and `resolucion_facturacion`) and populates each primary table with at least 30 realistic, referentially consistent records.

### 6.2. Credential Configuration
Update your local MySQL credentials inside `src/main/java/com/littlehorizons/erp/util/DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/erp_little_horizons?useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Bogota";
private static final String USER = "your_mysql_user";
private static final String PASSWORD = "your_mysql_password";
```

### 6.3. JDBC Driver Installation
The MySQL JDBC driver is automatically resolved by Maven through `pom.xml`:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.4.0</version>
</dependency>
```

---

## 7. Installation and Execution

### 7.1. Clone the Repository
```bash
git clone https://github.com/little-horizons-erp.git
cd little-horizons-erp
```

### 7.2. Open the Project in the IDE
1. Launch **IntelliJ IDEA**.
2. Select **File > Open...** and choose the `little-horizons-erp` root directory containing `pom.xml`.
3. Allow Maven to download `mysql-connector-j` and index the source folders.

### 7.3. Compile
Compile the project from IntelliJ (`Ctrl + F9` / **Build > Build Project**) or via Maven CLI:
```bash
mvn clean compile
```

### 7.4. Run
Run the main entry point `com.littlehorizons.erp.Main` (`Shift + F10` in IntelliJ) or execute:
```bash
mvn exec:java -Dexec.mainClass="com.littlehorizons.erp.Main"
```

---

## 8. Project Structure
The codebase strictly adheres to the **Model-View-Controller (MVC)** architectural pattern, separating presentation, control flow, business rules, and persistence:

```text
little-horizons-erp/
├── docs/
│   └── database.sql                  # Complete DDL (27 tables) + DML (30+ records/table)
├── src/main/java/com/littlehorizons/erp/
│   ├── model/                        # Domain POJOs (Student, Guardian, Teacher, Subject,
│   │                                 # AcademicRecord, Attendance, Club, Event, Payment,
│   │                                 # Invoice, Resource, ResourceLoan)
│   ├── dao/                          # JDBC Data Access Objects using PreparedStatement &
│   │                                 # try-with-resources (StudentDAO, AcademicDAO,
│   │                                 # ExtracurricularDAO, FinanceDAO, ResourceDAO)
│   ├── service/                      # Business logic & Stream API analytics (AcademicService,
│   │                                 # ExtracurricularService, FinanceService, ResourceService)
│   ├── controller/                   # MVC flow orchestrator (MainController)
│   ├── view/                         # Console menus & DIAN invoice formatter (MainMenuView)
│   ├── util/                         # Shared utilities (DatabaseConnection, ErrorLogger,
│   │                                 # InputValidator, CufeGenerator)
│   └── Main.java                     # Application entry point
├── error_log.txt                     # Timestamped runtime & SQL exception log file
├── pom.xml                           # Maven configuration & MySQL Connector/J dependency
└── README.md                         # Project technical documentation
```

---

## 9. Design Principles and Patterns Applied

### 9.1. SOLID Principles
- **Single Responsibility Principle (SRP):** Every class has one clear reason to change. For example, `CufeGenerator` exclusively computes SHA-384 hashes, `ErrorLogger` exclusively writes exception logs to `error_log.txt`, and `MainMenuView` handles console formatting.
- **Open/Closed Principle (OCP):** Service classes (`AcademicService`, `FinanceService`, `ResourceService`) encapsulate business rules and Stream aggregations so new reports or validations can be added without modifying existing DAO SQL queries.
- **Liskov Substitution Principle (LSP):** All domain entities in `model/` adhere strictly to standard JavaBean contracts, allowing uniform processing across `List` and `Stream` pipelines.
- **Interface Segregation Principle (ISP):** Utility classes (`InputValidator`, `CufeGenerator`, `ErrorLogger`) expose small, purpose-built static methods rather than monolithic helper interfaces.
- **Dependency Inversion Principle (DIP):** `MainController` depends on high-level service abstractions (`AcademicService`, `FinanceService`, `ResourceService`) rather than interacting directly with low-level JDBC `Connection` or `ResultSet` objects.

### 9.2. Design Patterns
- **Singleton Pattern (`DatabaseConnection`):** Guarantees a single centralized configuration point for database connectivity (`DatabaseConnection.getInstance().getConnection()`), avoiding duplicated driver initialization across DAOs.
- **Data Access Object (DAO) Pattern (`StudentDAO`, `AcademicDAO`, `FinanceDAO`, `ResourceDAO`, `ExtracurricularDAO`):** Decouples SQL persistence logic and `ResultSet` mapping from business services and controllers.
- **Model-View-Controller (MVC) Pattern:** Separates user interaction (`MainMenuView`), request coordination (`MainController`), and domain/data layers (`model`, `service`, `dao`).

---

## 10. Main Functionalities
1. **Student Management & Enrollment (`Option 1`):**
    - Register new students with full personal and medical metadata.
    - List all active students alphabetically with formatted full names.
    - Enroll students in academic grades (`Preescolar` to `11°`) per academic year.
2. **Academic Management (`Option 2`):**
    - Register and update grades on the Colombian `0.0` to `5.0` scale.
    - View student grade history and calculate period averages using **Java Stream API**.
    - Record daily attendance (`Presente`, `Ausente`, `Tardanza`, `Excusa Médica`).
3. **Extracurricular Activities (`Option 3`):**
    - Browse active clubs and enroll students while enforcing maximum club capacity.
