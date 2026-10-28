# Java Banking System

A console-based banking application developed in Java to demonstrate object-oriented programming, account management, and basic banking operations.

The application supports customer registration, login, savings and current accounts, deposits, withdrawals, fund transfers, and local data persistence.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Installation and Setup](#installation-and-setup)
- [How to Run](#how-to-run)
- [How to Use](#how-to-use)
- [Data Persistence](#data-persistence)
- [Limitations and Security](#limitations-and-security)
- [Future Improvements](#future-improvements)

## Overview

The Java Banking System is a command-line application that simulates common banking operations. It uses object-oriented design to organize customers, bank accounts, transactions, and banking services.

The project is intended for learning and demonstration. It is not connected to a real bank, payment gateway, or external database.

## Features

### Customer Management
- Register new customers.
- Generate a unique customer ID.
- Log in using a customer ID and PIN.

### Account Management
- Create savings accounts.
- Create current accounts.
- View customer accounts and balances.
- Generate account numbers.

### Banking Operations
- Deposit money into an account.
- Withdraw money according to account rules.
- Transfer funds between accounts.
- Maintain transaction records for supported account operations.

### Additional Features
- Savings account interest calculation.
- Overdraft support for current accounts.
- Local data persistence using Java object serialization.
- Separate classes for banking operations, customers, accounts, and transactions.

## Technology Stack

- **Language:** Java
- **User Interface:** Command-line interface (CLI)
- **Data Storage:** Java object serialization
- **Collections:** Java `Map` and `List`
- **Date and Time:** `LocalDateTime`
- **Core Concepts:** Object-oriented programming, inheritance, abstraction, and encapsulation

No Spring Boot, external database, Maven, or Gradle build configuration is included in the supplied project.

## Project Structure

```text
Banking project/
├── Main.java
├── Bank.java
├── BankService.java
├── Customer.java
├── Account.java
├── SavingsAccount.java
├── CurrentAccount.java
├── Transaction.java
├── TransactionType.java
└── DataStore.java
```

### Class Responsibilities

| Class | Responsibility |
|---|---|
| `Main` | Runs the command-line menu and accepts user input. |
| `Bank` | Manages customers, customer IDs, authentication, and account searches. |
| `BankService` | Coordinates customer registration and banking operations. |
| `Customer` | Stores customer details and associated accounts. |
| `Account` | Base class for account balances, deposits, withdrawals, and transaction history. |
| `SavingsAccount` | Implements savings account behavior and interest calculation. |
| `CurrentAccount` | Implements current account behavior and overdraft rules. |
| `Transaction` | Represents a transaction amount, type, and timestamp. |
| `TransactionType` | Defines transaction types such as deposits and withdrawals. |
| `DataStore` | Saves and loads bank data from a local file. |

## Architecture

The application uses a simple layered design:

```text
        User
         |
         v
   Main (CLI Menu)
         |
         v
     BankService
         |
         v
        Bank
         |
         v
      Customer
         |
         v
       Account
         |
         v
    Transaction

Bank Data
    |
    v
 DataStore
    |
    v
  bank.dat
```

### How It Works

1. The user selects an option from the command-line menu.
2. `BankService` processes the requested operation.
3. `Bank` manages customer records and account lookups.
4. Each customer can own multiple accounts.
5. Account classes implement deposit and withdrawal behavior.
6. Transactions record supported account operations.
7. `DataStore` persists the bank object locally.

## Prerequisites

Install a Java Development Kit (JDK) on your computer.

Verify your installation:

```bash
java -version
javac -version
```

A terminal or command prompt is also required.

## Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Dheerajpatel55/payment-processing-engine.git
```

Navigate into the directory containing the Java source files.

> If this repository contains a different project or the source files are in a subdirectory, adjust the directory accordingly.

### 2. Open the Project

Open the folder containing `Main.java` in VS Code or your preferred Java IDE.

### 3. Compile the Source Files

From the directory containing the Java files, run:

```bash
javac *.java
```

If compilation succeeds, Java class files will be generated.

## How to Run

Run the application using:

```bash
java Main
```

The application displays a command-line menu. Follow the prompts to register a customer or log in.

## How to Use

### Main Menu

The application provides options to:

1. Create a customer.
2. Log in.
3. Exit.

When registering, note the generated customer ID because it is needed for login.

### Customer Menu

After logging in, users can:

1. Create an account.
2. Deposit money.
3. Withdraw money.
4. Transfer funds.
5. View accounts.
6. Log out.

Select the appropriate menu option and follow the prompts.

### Account Types

**Savings Account**
- Supports standard account operations.
- Includes an interest calculation method using a configured rate of 4%.
- Interest calculation is not currently exposed as a customer menu option.

**Current Account**
- Supports deposits and withdrawals.
- Includes a configured overdraft limit of 5,000 units.

## Data Persistence

The `DataStore` class saves and loads bank data using Java object serialization.

The application uses a local file named:

```text
bank.dat
```

The file is stored relative to the application's working directory.

**Important:** The current implementation saves data at specific points, including customer creation and logout. Some later account changes may not be saved immediately. Closing the application without saving can cause data loss.

The serialized file should be treated as local application data and should not be committed if it contains personal or sensitive information.

## Limitations and Security

This project is a learning application and is **not suitable for real banking or payment processing**.

- Customer PINs are stored without secure password hashing.
- Monetary values use `double`, which can introduce rounding errors.
- Input validation needs improvement, including checks for zero and negative amounts.
- Transfers are not implemented as atomic database transactions.
- Some account operations may not be recorded consistently in transaction history.
- Data is stored in a serialized file rather than a database.
- Persistence, concurrency control, and recovery mechanisms are limited.
- Authentication and account ownership checks need further security review.

Use fictional customer details and test data only.

## Future Improvements

Potential enhancements include:

- Replace `double` with `BigDecimal` for monetary calculations.
- Add stronger input validation and meaningful error messages.
- Securely hash customer PINs.
- Improve transaction history and transfer records.
- Save changes reliably after every successful operation.
- Introduce a relational database for persistent storage.
- Implement atomic transfers and concurrency safeguards.
- Add unit and integration tests.
- Improve exception handling and application logging.
- Add a build system such as Maven or Gradle.
- Add licensing information and contribution guidelines.

---

**Project:** payment processing engine  
**Maintainer:** Dheeraj Patel

This project is intended for educational purposes and can be extended as a foundation for learning backend development and banking application design.
