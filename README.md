# Order-management-system
A database-backed Orders Management application designed to process client warehouse orders, structured with a robust layered architecture pattern.

## Project Structure

```
src/main/java/
├── model/
│   ├── Client.java
│   ├── Product.java
│   ├── Order.java
│   └── Address.java
├── dataAccess/
│   ├── AbstractDAO.java      # Generic DAO using reflection (create, edit, delete, find)
│   ├── ClientDAO.java
│   ├── ProductDAO.java
│   ├── OrderDAO.java
│   └── AddressDAO.java
├── businessLayer/
│   ├── ClientBLL.java
│   ├── ProductBLL.java
│   ├── OrderBLL.java
│   └── BusinessException.java
├── presentation/
│   ├── ClientView.java
│   ├── ClientController.java
│   ├── ClientFormDialog.java
│   ├── ProductView.java
│   ├── ProductFormDialog.java
│   └── OrderView.java
├── Connection/
│   └── ConnectionFactory.java
├── util/
│   └── TableUtil.java        # Reflection-based JTable population
└── Main.java
```

## Features

- **Client management** — add, edit, delete, view all clients in a JTable
- **Product management** — add, edit, delete, view all products in a JTable
- **Order management** — select a client and product, enter quantity; under-stock orders are rejected with an error message; product stock is decremented after a successful order
- **Generic DAO** — `AbstractDAO<T>` uses Java reflection to dynamically build SQL queries (INSERT, UPDATE, DELETE, SELECT) for any model class
- **Reflection-based table rendering** — `TableUtil` inspects object fields at runtime to auto-generate JTable headers and rows
- **Lambda expressions & streams** — used for list filtering and event handling throughout the presentation layer

## Technologies

- Java 23
- Java Swing (GUI)
- MySQL 8 + JDBC (`mysql-connector-java 8.0.33`)
- Maven (build tool)
- Java Reflection API

## Database Setup

The database schema and seed data are in `warehousedb.sql`. To set up:

```bash
mysql -u <username> -p < warehousedb.sql
```

This creates the `warehousedb` schema with the following tables: `address`, `client`, `product`, `orders`.

Then update the connection credentials in `Connection/ConnectionFactory.java` to match your local MySQL setup.

## How to Run

**Prerequisites:** Java 23+, Maven, MySQL 8

```bash
mvn compile
mvn exec:java -Dexec.mainClass="Main"
```

Or open in IntelliJ IDEA and run `Main.java` directly.

## JavaDoc

Pre-generated JavaDoc is available in the `JavaDoc/` folder. Open `JavaDoc/index.html` in a browser to browse the full API documentation.

