# 🛒 Enterprise E-Commerce Platform

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-brightgreen.svg)
![Architecture](https://img.shields.io/badge/Architecture-Modular_Monolith-blue.svg)
![Design](https://img.shields.io/badge/Design-Clean_Architecture-blueviolet.svg)
![Database](https://img.shields.io/badge/Database-PostgreSQL-blue.svg)

A modern, scalable E-Commerce backend system built with **Java 21** and the latest **Spring Boot 4**. This project demonstrates a mature approach to software design by combining a **Modular Monolith** structure with **Clean Architecture** principles, ensuring the system is highly maintainable, testable, and adaptable to future business changes.

---

## 🎯 Project Overview

As systems grow, tightly coupled code becomes a liability. This project mitigates that risk by adopting a **Modular Monolithic** architecture combined with **Domain-Driven Design (DDD)**. The system is logically divided into strict, independent business modules. Inside each module, **Clean Architecture** is applied pragmatically to isolate core business logic from external frameworks, databases, and delivery mechanisms, allowing the application to scale sustainably without accumulating technical debt.

## ✨ Core Business Modules

### 🔐 1. Identity & Access Management (IAM)
* **Secure Authentication:** Implemented robust user authentication with modern JWT (JSON Web Tokens) strategies.
* **Two-Step Verification:** Integrated OTP-based registration utilizing **Redis** for fast, temporary data storage and expiration handling.
* **Advanced Authorization:** Built a custom Policy-Based Access Control (PBAC) / Attribute-Based Access Control (ABAC) engine to securely manage data ownership and granular permissions across roles (Customer, Seller, Admin).

### 📦 2. Catalog Management
* Comprehensive management of products, categories, and dynamic inventory attributes.
* Optimized database indexing for high-performance product retrieval using native JSONB and UUID types.

### 💳 3. Sales & Order Processing
* End-to-end management of the order lifecycle.
* Utilized strongly-typed identifiers (Value Objects) to ensure strict data integrity and prevent cross-domain data corruption during checkout.

### 🔔 4. Asynchronous Notifications
* Centralized notification system for automated user alerts.
* Implemented an **Event-Driven** flow where domain events trigger notifications asynchronously, ensuring the core checkout and registration processes remain lightning-fast.

---

## 🏗️ Architectural Highlights

* **Modular Monolith (Spring Modulith):** The application is deployed as a single unit but internally divided into strict, independent modules. This provides the boundary enforcement of microservices without the heavy operational complexity.
* **Clean Architecture:** Inside each module, the codebase is structured to separate concerns into distinct layers (Domain, Application, Infrastructure, and Presentation). Core business rules are independent of UI, databases, or external APIs.
* **Loose Coupling & Event-Driven:** Internal workflows are handled asynchronously via Domain Events, minimizing direct dependencies between different business contexts.
* **Resilience & Idempotency:** Built-in mechanisms (Event Publication Registry) ensure that background tasks are safely processed and retried if necessary, preventing data loss during network or external API failures.

---

## 🛠️ Tech Stack

* **Core Framework:** Java 21, Spring Boot 4
* **Architecture:** Modular Monolith (Spring Modulith), Clean Architecture, DDD, Event-Driven Architecture
* **Database:** PostgreSQL (utilizing advanced data types for optimal performance)
* **Caching & Session Management:** Redis
* **Security:** Spring Security, Custom PBAC/ABAC Policy Evaluator
* **Tooling:** Docker, Maven