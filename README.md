# Stock Management System

A high-performance stock management system implemented in **Java**. This project was created as a final assignment for a Data Structures and Algorithms course.

The system allows for real-time tracking of stock prices, historical updates, and efficient range queries.

## Technical Highlights
The core of this project is a **custom implementation of a Balanced Search Tree** (specifically a 2-3 Tree variant).
* **No external data structure libraries used:** The tree logic (`insert`, `delete`, `split`, `merge`) is implemented from scratch.
* **Time Complexity:** The system is designed to handle large datasets with **O(log n)** time complexity for insertion, deletion, and search operations.
* **Range Queries:** Supports efficient querying of stocks within a specific price range in **O(log n + k)** time (where k is the number of results).

## Features
* **Init:** Initialize the stock management system.
* **Add/Remove Stock:** Dynamically add or remove stocks from the system.
* **Update Price:** Update stock prices with timestamp tracking.
* **History Management:** Remove specific historical price updates (fixing errors).
* **Smart Queries:**
    * Get the current price of a specific stock.
    * Count how many stocks are within a price range `[min, max]`.
    * Retrieve a sorted list of stocks within a price range.

## Project Structure
* `StockManager.java`: The main API class that manages the stocks and price trees.
* `Tree.java`: Custom implementation of the balanced search tree.
* `Node.java`: Represents nodes in the tree (handling keys, values, and children).
* `Stock.java`: Represents a single stock entity with its own history tree.
* `Main.java`: **Provided by course staff.** Contains test cases and usage examples (unedited).

## Instructions
The full project requirements and instructions are available in the [PDF file](java_exercise.pdf) (Hebrew).
