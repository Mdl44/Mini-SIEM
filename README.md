# SIEM Simulator

A console-based Security Information and Event Management (SIEM) simulator written in Java. This project puts the user in the shoes of a SOC (Security Operations Center) Analyst investigating network traffic, or a System Administrator managing the infrastructure. 

Currently in Stage 1, the application features a custom file-based data persistence system, dynamic traffic generation, and a fully functional progression loop.

## Key Features

### SOC Analyst Gameplay (Core Loop)
* **Authentication & Profiles:** Analysts log in with unique credentials. Profiles track experience (days survived), rank, and remaining lives (health points).
* **Daily Shifts:** Each shift generates a queue of 10 prioritized incidents based on the analyst's current rank level.
* **Incident Investigation:** Analysts review dossiers containing structured network and login events.
* **Decision Making:** Players must decide whether to `allow` or `block` traffic based on log patterns (e.g., time signatures, repeated failures, suspicious ports).
* **Desk Tools:** Access to internal systems lists, an IP Blacklist, and a limited "Ask Senior" lifeline for guaranteed correct classifications.
* **Shift Evaluation:** Decisions are graded at the end of the shift (True Positive, False Positive, etc.). Mistakes cost lives, while flawless shifts restore them. Surviving specific milestones grants veteran status.

### Admin Tools & Infrastructure Management
* **System Management:** Admins can view and register new monitored systems (servers, routers, laptops). 
* **Data Validation:** Includes strict validation for IPv4 formats, unique constraint checks for IPs, and auto-incrementing IDs to prevent data collisions.
* **Roster Management:** Admins can create new SOC Analyst accounts and review the performance (HP, Level, Days Survived) of the entire team.

### Dynamic Traffic Simulation
* **Procedural Incident Generation:** Traffic is generated dynamically. The difficulty and type of attacks scale with the player's level.
* **Diverse Scenarios:** Simulates various network scenarios including:
  * Normal employee logins and clumsy typos.
  * Brute Force attacks.
  * Port Scans.
  * Compromised Accounts & Insider Threats.
  * DDoS attacks.
  * Legitimate remote worker connections.
* **Realistic Time Deltas:** Events feature realistic timestamp intervals (e.g., nanosecond delays for DDoS, several seconds for human typing).

## Technical Architecture
* **Models:** Includes abstract base classes and inheritance trees for Users (`SOCAnalyst`, `Admin`) and Events (`LoginEvent`, `NetworkEvent`). Implements `Comparable` for priority-based incident sorting.
* **Repositories:** Handles file I/O operations, reading and writing objects to text/CSV files. Employs caching strategies (e.g., `HashSet` for the Blacklist) and on-demand reading.
* **Services:** Centralizes business logic. Includes a custom `TrafficSimulator` equipped with RNG-based probabilistic event generation.
* **Views:** A robust console menu (`GameMenu`) that manages the application state, handles user input validation, and prevents runtime crashes (e.g., catching dangling newlines and null pointers via `Optional`).

## Java Technologies Utilized
* Object-Oriented Principles: Encapsulation, Inheritance, Polymorphism, Abstraction.
* Java Collections Framework: `ArrayList`, `HashSet` (for unique IP lookups), `TreeSet` (for automatic priority sorting of critical incidents).
* `java.time` API for timestamp manipulation.
* `Optional<T>` for null-safety during database lookups.
* Regular Expressions (Regex) for input validation.
