# Centralized Configuration using Spring Cloud Config

## Complete Beginner-Friendly Guide + Project Explanation

---

# PART 1: CENTRALIZED CONFIGURATION (THEORY)

---

## Step 1: What is Configuration?

### Simple Explanation

**Configuration** is the set of **settings** that tell your application **how to behave**.

Think of it like this: When you buy a new phone, you set it up with your language, Wi-Fi password, wallpaper, etc. These are your phone's **configuration**. Without them, the phone wouldn't know how to work the way YOU want.

Similarly, every application needs settings to run properly.

### Examples of Configuration

| Configuration          | What it does                                      | Example Value                             |
|------------------------|---------------------------------------------------|-------------------------------------------|
| `server.port`         | Which port the app runs on                        | `8080`                                    |
| `database URL`        | Where the database is located                     | `jdbc:mysql://localhost:3306/mydb`        |
| `API keys`            | Secret keys for third-party services              | `sk-abc123xyz`                            |
| `username / password` | Credentials for database or service connections   | `root` / `password123`                    |
| `logging level`       | How much detail the app logs                      | `DEBUG`, `INFO`, `ERROR`                  |

### Why Are Configs Important?

- **Without configuration**, your app doesn't know WHERE to connect, HOW to behave, or WHAT settings to use
- They let you **change behavior without changing code**
- You can have **different settings** for development, testing, and production

```
Example: In development, you use a local database.
         In production, you use a cloud database.
         The CODE is same. Only the CONFIG changes!
```

---

## Step 2: Problems Without Central Config

### The Problem in Microservices

In a microservices architecture, you have **many small services** (10, 50, or even 100+). Each service has its own configuration file.

```
Without Central Config:

    +-----------------+     +-----------------+     +-----------------+
    | Order Service   |     | Payment Service |     | User Service    |
    |                 |     |                 |     |                 |
    | application.    |     | application.    |     | application.    |
    | properties      |     | properties      |     | properties      |
    | (its own config)|     | (its own config)|     | (its own config)|
    +-----------------+     +-----------------+     +-----------------+

    Each service manages its OWN config file separately!
```

### What Goes Wrong?

| Problem                     | Explanation                                                                 |
|-----------------------------|-----------------------------------------------------------------------------|
| **Scattered configs**      | Configs are spread across 50+ services. Finding one setting is like finding a needle in a haystack |
| **Hard to update**         | If the database URL changes, you must update it in EVERY service manually   |
| **Inconsistency risk**     | One service might have the old URL, another has the new one. Things break!  |
| **No version control**     | You can't easily track WHO changed WHAT and WHEN                            |
| **Environment confusion**  | Mixing up dev and prod settings can cause disasters                         |
| **Restart required**       | To change even one setting, you must rebuild and redeploy the service       |

### Real-World Analogy

Imagine a **school with 50 classrooms**:

```
WITHOUT Central Config (Bad Way):
    
    Each teacher writes their OWN timetable on their OWN whiteboard.
    
    Problem: 
    - If the school changes lunch time from 12:00 to 12:30,
      someone has to go to ALL 50 classrooms and update each board.
    - Some teachers might forget to update.
    - Students in different classes get confused.
    - CHAOS!

WITH Central Config (Good Way):

    The school puts ONE digital display in the hallway.
    ALL teachers and students look at THAT ONE display.
    
    Benefit:
    - Change lunch time ONCE on the central display.
    - Everyone sees the update immediately.
    - No confusion. No inconsistency.
    - PEACE!
```

---

## Step 3: What is Centralized Configuration?

### Simple Definition

**Centralized Configuration** means storing ALL configuration for ALL services in **ONE central place**, instead of each service having its own separate config.

```
With Central Config:

                        +-------------------------+
                        |   CENTRAL CONFIG STORE   |
                        |   (One place for ALL     |
                        |    configurations)        |
                        +-------------------------+
                           /         |         \
                          /          |          \
                         v           v           v
              +-----------+  +-----------+  +-----------+
              |  Order    |  | Payment   |  |  User     |
              |  Service  |  | Service   |  |  Service  |
              +-----------+  +-----------+  +-----------+
              
              All services FETCH their config from the central store!
```

### How Does It Solve the Problems?

| Problem Before                  | Solution With Central Config                              |
|---------------------------------|-----------------------------------------------------------|
| Configs scattered everywhere    | All configs in ONE place                                  |
| Hard to update across services  | Update ONCE, all services get the change                  |
| Risk of inconsistency           | Single source of truth = no mismatch                      |
| No version tracking             | Git tracks every change with history                      |
| Environment confusion           | Separate files for dev, prod, etc. in one place           |

### Analogy: Google Drive

```
Before Central Config:
    Every team member has their OWN copy of a document on their laptop.
    Someone updates their copy, but others still have the OLD version.

After Central Config (like Google Drive):
    ONE document on Google Drive.
    Everyone reads from the SAME document.
    One person updates it -> Everyone sees the latest version!
```

---

## Step 4: What is Spring Cloud Config?

### Simple Definition

**Spring Cloud Config** is a **tool/framework** provided by Spring that helps you implement centralized configuration for your microservices.

It provides:
- A **Config Server** (the central place that stores and serves configs)
- A **Config Client** (built into each microservice to fetch configs from the server)

### Why Use Spring Cloud Config?

- It's part of the **Spring ecosystem** (works perfectly with Spring Boot)
- It uses **Git** as the config storage (version control for free!)
- It supports **multiple environments** (dev, test, prod)
- It's **easy to set up** (just a few annotations and properties)
- It's the **industry standard** for Spring-based microservices

### Key Benefits

```
+--------------------------------------------------+
|         Spring Cloud Config Benefits              |
+--------------------------------------------------+
|                                                    |
|  1. CENTRALIZED    -> All configs in one Git repo |
|  2. VERSIONED      -> Git tracks all changes      |
|  3. ENVIRONMENT    -> dev, prod, test configs      |
|  4. EASY UPDATE    -> Change once, apply to all   |
|  5. SECURE         -> Can encrypt sensitive data   |
|  6. SPRING NATIVE  -> Works seamlessly with Boot  |
|                                                    |
+--------------------------------------------------+
```

---

## Step 5: Core Components (VERY IMPORTANT)

Spring Cloud Config has **two main components**:

```
+-------------------+                    +-------------------+
|                   |   HTTP Request     |                   |
|   CONFIG CLIENT   | -----------------> |   CONFIG SERVER   |
|  (Microservice)   | <----------------- |   (Central Hub)   |
|                   |   Config Response  |                   |
+-------------------+                    +--------+----------+
                                                  |
                                                  | Reads from
                                                  v
                                         +-------------------+
                                         |    GIT REPO       |
                                         | (Config Storage)  |
                                         +-------------------+
```

---

### 1. Config Server

#### What Is It?

The Config Server is a **separate Spring Boot application** that acts as the **central hub** for all configurations. It is the "librarian" who knows where all the config "books" are stored.

#### What Does It Do?

- **Reads** configuration files from a **Git repository**
- **Serves** those configurations to any microservice that asks
- **Supports** multiple environments (dev, prod, test)
- Runs on a specific port (commonly **8888**)

#### Why Git?

```
Why Git for storing configs?

+----------------------------------------------+
| REASON              | EXPLANATION             |
+----------------------------------------------+
| Version History     | See WHO changed WHAT    |
|                     | and WHEN                |
+----------------------------------------------+
| Rollback            | Made a mistake? Go back |
|                     | to previous version     |
+----------------------------------------------+
| Branching           | Test config changes in  |
|                     | a branch first          |
+----------------------------------------------+
| Collaboration       | Multiple people can     |
|                     | manage configs          |
+----------------------------------------------+
| Already Familiar    | Most developers already |
|                     | know Git                |
+----------------------------------------------+
```

#### How to Create a Config Server?

Two things are needed:

**1. Add the annotation** `@EnableConfigServer` on the main class:

```java
@SpringBootApplication
@EnableConfigServer      // <-- This ONE annotation makes it a Config Server!
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

**2. Point it to a Git repo** in `application.properties`:

```properties
server.port=8888
spring.cloud.config.server.git.uri=https://github.com/your-repo/config-repo.git
```

That's it! Your Config Server is ready.

---

### 2. Config Client

#### What Is It?

The Config Client is any **microservice** that needs to **fetch its configuration** from the Config Server instead of using a local file.

#### What Does It Do?

- On startup, it **contacts the Config Server**
- **Downloads** its configuration
- **Uses** those configs just like local properties (via `@Value`, `@ConfigurationProperties`, etc.)

#### How It Works

```
Config Client Startup Process:
                                                    
    1. Client starts up
           |
    2. "I need my config!"
           |
    3. Contacts Config Server at http://localhost:8888
           |
    4. Says: "I am 'order-service' and my profile is 'dev'"
           |
    5. Server looks for: order-service-dev.properties in Git
           |
    6. Server returns the config values
           |
    7. Client uses those values (e.g., order.message = "Hello from Dev!")
```

#### How to Create a Config Client?

**1. Add the dependency** `spring-cloud-starter-config` in pom.xml

**2. Tell it where the Config Server is** in `application.properties`:

```properties
spring.application.name=order-service
spring.config.import=configserver:http://localhost:8888
spring.profiles.active=dev
```

**3. Use the config values** with `@Value`:

```java
@Value("${order.message}")
public String message;
```

---

## Step 6: Complete Flow Explanation (VERY IMPORTANT)

### Step-by-Step Flow

Here is exactly what happens when you run the system:

```
STEP-BY-STEP FLOW:

    +--------+         +---------+         +----------+
    | Client |         | Config  |         |   Git    |
    | (Order |         | Server  |         |   Repo   |
    | Service)|        | (:8888) |         |(GitHub)  |
    +---+----+         +----+----+         +----+-----+
        |                   |                   |
  1.    | App starts        |                   |
        |                   |                   |
  2.    | GET /order-       |                   |
        | service/dev       |                   |
        |------------------>|                   |
        |                   |                   |
  3.    |                   | Fetch config      |
        |                   | files from Git    |
        |                   |------------------>|
        |                   |                   |
  4.    |                   |<------------------|
        |                   | Return config     |
        |                   | file contents     |
        |                   |                   |
  5.    |<------------------|                   |
        | Return config     |                   |
        | as JSON           |                   |
        |                   |                   |
  6.    | Uses config       |                   |
        | values in app     |                   |
        | (e.g., @Value)    |                   |
        |                   |                   |
```

### Detailed Explanation of Each Step

| Step | What Happens | Details |
|------|-------------|---------|
| **1** | Config Client starts | Spring Boot application begins its startup process |
| **2** | Client requests config | Client sends HTTP GET to `http://localhost:8888/order-service/dev` |
| **3** | Server fetches from Git | Server clones/pulls from `https://github.com/.../config-repo.git` |
| **4** | Git returns files | Files like `order-service-dev.properties` are read |
| **5** | Server sends config to Client | Config values sent as JSON response |
| **6** | Client uses the config | Values are injected into beans via `@Value` or `@ConfigurationProperties` |

### What URL Does the Client Actually Call?

The Config Server follows this URL pattern:

```
http://localhost:8888/{application-name}/{profile}

Examples:
  http://localhost:8888/order-service/dev      -> order-service-dev.properties
  http://localhost:8888/order-service/prod     -> order-service-prod.properties
  http://localhost:8888/payment-service/dev    -> payment-service-dev.properties
```

The `application-name` comes from `spring.application.name` in the client.
The `profile` comes from `spring.profiles.active` in the client.

---

## Step 7: Environment-Based Configuration

### Why Do We Need Environments?

In real projects, you have **different environments**:

```
+-------------+-------------------------------------------+
| Environment | Purpose                                   |
+-------------+-------------------------------------------+
| DEV         | Developer's local machine. For testing.   |
| TEST/QA     | Quality Assurance team tests here.         |
| STAGING     | Pre-production. Almost like production.    |
| PROD        | Real users use this. Must be stable!       |
+-------------+-------------------------------------------+
```

Each environment has **different settings**:

```
DEV:
  database = localhost:3306/dev_db
  debug = true
  message = "Hello from Development!"

PROD:
  database = prod-server:3306/prod_db  
  debug = false
  message = "Welcome to Production!"
```

### How Spring Cloud Config Handles This

In the Git repo, you create **separate files** for each environment:

```
Git Config Repository Structure:

    config-repo/
    ├── order-service.properties            (default/common config)
    ├── order-service-dev.properties         (dev-specific config)
    ├── order-service-prod.properties        (prod-specific config)
    ├── payment-service.properties           (default for payment)
    ├── payment-service-dev.properties       (dev for payment)
    └── payment-service-prod.properties      (prod for payment)
```

### How It Works

```
If Client says: "I am order-service, profile = dev"

    Config Server will load:
    1. order-service.properties       (common/default values)
    2. order-service-dev.properties   (dev-specific values - OVERRIDES common)
    
    Dev-specific values WIN over common values!
```

### Example

**order-service.properties** (common):
```properties
order.timeout=30
order.currency=USD
```

**order-service-dev.properties**:
```properties
order.message=Hello from Development Environment!
order.debug=true
```

**order-service-prod.properties**:
```properties
order.message=Welcome to Production!
order.debug=false
```

When `profile = dev`, the app gets:
- `order.timeout=30` (from common)
- `order.currency=USD` (from common)
- `order.message=Hello from Development Environment!` (from dev)
- `order.debug=true` (from dev)

---

## Step 8: Dynamic Refresh

### The Problem

What if you change a config value in Git **AFTER** the service is already running?

```
Without Refresh:
    1. Service starts, fetches config (order.message = "Hello")
    2. You change config in Git (order.message = "Hi there!")
    3. Service STILL shows "Hello" (old value!)
    4. You must RESTART the service to get new value
    
    Restarting = DOWNTIME = BAD for production!
```

### The Solution: Spring Cloud Bus + @RefreshScope

```
With Dynamic Refresh:
    1. Service starts, fetches config (order.message = "Hello")
    2. You change config in Git (order.message = "Hi there!")
    3. Send a refresh request: POST /actuator/refresh
    4. Service picks up new value WITHOUT restart!
    5. Now shows "Hi there!" (new value!)
    
    No restart needed = ZERO DOWNTIME = GOOD!
```

### How to Enable It (Basic Idea)

1. Add `spring-boot-starter-actuator` dependency
2. Add `@RefreshScope` annotation on the bean that uses `@Value`
3. Call `POST http://localhost:port/actuator/refresh` after changing config

```java
@RestController
@RefreshScope    // <-- This enables dynamic refresh for this bean
public class OrderController {
    
    @Value("${order.message}")
    public String message;
    
    @GetMapping("/order")
    public String getOrderMessage() {
        return message;
    }
}
```

> **Note:** This is an advanced topic. The basic project works perfectly without it. Dynamic refresh is useful when you want to update configs without restarting services.

---
---

# PART 2: PROJECT EXPLANATION

---

## Project: Central Configuration Project

**GitHub Repository:** https://github.com/sarthakpawar0912/central-config-project.git

---

## Overview

This project demonstrates **Centralized Configuration using Spring Cloud Config** with two sub-projects:

```
central-config-project/
├── ConfigServerProject/    --> The Config SERVER (serves configs)
└── ConfigClientProject/    --> The Config CLIENT (consumes configs)
```

**In simple words:**
- The **ConfigServerProject** is like a **librarian** who stores and serves configuration
- The **ConfigClientProject** is like a **student** who asks the librarian for the configuration it needs
- The **Git repo** (config-repo) is like the **bookshelf** where configs are actually stored

```
    +--------------------+         +-------------------+         +------------------+
    | ConfigClientProject|  HTTP   | ConfigServerProject|  Git   | config-repo      |
    | (Order Service)    |-------->| (Port 8888)       |------->| (GitHub)         |
    | Port: default      |<--------|                   |<-------|                  |
    +--------------------+  Config +-------------------+  Files +------------------+
```

---

## ConfigServerProject (Detailed Explanation)

### Role

The Config Server is the **central hub** that:
- Connects to a **Git repository** containing config files
- Serves config to any microservice that asks for it
- Runs on port **8888** (the conventional port for Config Servers)

### Project Structure

```
ConfigServerProject/
├── pom.xml
└── src/main/
    ├── java/com/sarthak/configserverproject/
    │   └── ConfigServerProjectApplication.java
    └── resources/
        └── application.properties
```

### Main Application Class

```java
package com.sarthak.configserverproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer       // THIS annotation is the magic!
public class ConfigServerProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerProjectApplication.class, args);
    }
}
```

**Explanation:**

| Line | What It Does |
|------|-------------|
| `@SpringBootApplication` | Standard Spring Boot application marker |
| `@EnableConfigServer` | Turns this app into a Config Server. Without this, it's just a normal app. WITH this, it can serve config to other services! |
| `SpringApplication.run(...)` | Starts the application |

### Configuration (application.properties)

```properties
spring.application.name=ConfigServerProject
server.port=8888
spring.cloud.config.server.git.uri=https://github.com/sarthakpawar0912/config-repo.git
```

**Explanation:**

| Property | Value | Why |
|----------|-------|-----|
| `spring.application.name` | `ConfigServerProject` | Names the server application |
| `server.port` | `8888` | Config Server conventionally runs on port 8888 |
| `spring.cloud.config.server.git.uri` | `https://github.com/sarthakpawar0912/config-repo.git` | Tells the server WHERE to find the config files. This Git repo contains all the `.properties` files for all microservices |

### Key Dependency (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

This single dependency gives the application all the power to act as a Config Server.

### Tech Details

| Detail | Value |
|--------|-------|
| Spring Boot Version | 4.0.5 |
| Spring Cloud Version | 2025.1.1 |
| Java Version | 21 |

---

## ConfigClientProject (Detailed Explanation)

### Role

The Config Client is the **Order Service** microservice that:
- Fetches its configuration from the Config Server (instead of having its own local config)
- Exposes a REST API endpoint `/order` that returns a message from the centralized config
- Demonstrates how any microservice can use centralized configuration

### Project Structure

```
ConfigClientProject/
├── pom.xml
└── src/main/
    ├── java/com/sarthak/configclientproject/
    │   ├── ConfigClientProjectApplication.java
    │   └── OrderController.java
    └── resources/
        └── application.properties
```

### Main Application Class

```java
package com.sarthak.configclientproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConfigClientProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientProjectApplication.class, args);
    }
}
```

**Note:** No special annotation needed for the client! Just a normal Spring Boot app. The magic happens through the dependency and properties.

### OrderController (The REST Endpoint)

```java
package com.sarthak.configclientproject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Value("${order.message}")    // Injected from Config Server!
    public String message;

    @GetMapping("/order")
    public String getOrderMessage() {
        return message;
    }
}
```

**Line-by-Line Explanation:**

| Line | What It Does |
|------|-------------|
| `@RestController` | Makes this class a REST API controller |
| `@Value("${order.message}")` | Injects the value of `order.message` from the config. This value comes from the Config Server, NOT from a local file! |
| `public String message` | Stores the injected config value |
| `@GetMapping("/order")` | Creates a GET endpoint at `/order` |
| `return message` | Returns the config value as the HTTP response |

### Configuration (application.properties)

```properties
spring.application.name=order-service
spring.config.import=configserver:http://localhost:8888
spring.profiles.active=dev
```

**Explanation:**

| Property | Value | Why |
|----------|-------|-----|
| `spring.application.name` | `order-service` | This is the name the Config Server uses to find the right config file. Server will look for `order-service.properties` and `order-service-dev.properties` |
| `spring.config.import` | `configserver:http://localhost:8888` | Tells Spring Boot to import config from the Config Server at this URL. This is the modern way (Spring Boot 3+) to connect to a Config Server |
| `spring.profiles.active` | `dev` | Tells the server to also load `order-service-dev.properties` (dev environment config) |

### Key Dependencies (pom.xml)

```xml
<!-- To connect to Config Server -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

<!-- To create REST endpoints -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
```

---

## Project Flow (VERY IMPORTANT)

### Complete End-to-End Flow

```
COMPLETE PROJECT FLOW:

  +-----------+                +-----------+                +-----------+
  |           |                |           |                |           |
  |  Config   |                |  Config   |                |   Git     |
  |  Client   |                |  Server   |                |   Repo    |
  |  (Order   |                |  (Port    |                | (config-  |
  |  Service) |                |   8888)   |                |  repo)    |
  |           |                |           |                |           |
  +-----+-----+               +-----+-----+                +-----+-----+
        |                           |                             |
        |   STEP 1: Config Server starts on port 8888             |
        |                           |-------- connects to ------->|
        |                           |    Git repo on startup      |
        |                           |                             |
        |   STEP 2: Config Client starts                          |
        |                           |                             |
        |   STEP 3: Client asks     |                             |
        |   for config              |                             |
        |--- GET /order-service/dev |                             |
        |-------------------------->|                             |
        |                           |                             |
        |                    STEP 4:|                             |
        |                    Server fetches                       |
        |                    order-service-dev.properties          |
        |                           |---------------------------->|
        |                           |<----------------------------|
        |                           |  (order.message=Hello...)   |
        |                           |                             |
        |   STEP 5: Server sends    |                             |
        |   config back to client   |                             |
        |<--------------------------|                             |
        |  {order.message: "..."}   |                             |
        |                           |                             |
        |   STEP 6: Client injects  |                             |
        |   config into @Value      |                             |
        |   fields                  |                             |
        |                           |                             |
        |   STEP 7: User calls      |                             |
        |   GET /order              |                             |
        |   -> Returns the message  |                             |
        |   from centralized config!|                             |
```

### Step-by-Step Walkthrough

| Step | Action | What Happens |
|------|--------|-------------|
| **1** | Start Config Server | `ConfigServerProjectApplication` starts on port `8888`. It connects to `https://github.com/sarthakpawar0912/config-repo.git` |
| **2** | Start Config Client | `ConfigClientProjectApplication` starts. It sees `spring.config.import=configserver:http://localhost:8888` |
| **3** | Client requests config | Client automatically calls `http://localhost:8888/order-service/dev` (because `spring.application.name=order-service` and `spring.profiles.active=dev`) |
| **4** | Server fetches from Git | Server reads `order-service-dev.properties` from the Git repo |
| **5** | Server responds | Server sends back config values (including `order.message`) as a response |
| **6** | Client uses config | `@Value("${order.message}")` in `OrderController` gets populated with the value from the server |
| **7** | User tests the API | User opens browser: `http://localhost:8080/order` and sees the message from centralized config! |

---

## Git Config Repository

The Config Server points to: `https://github.com/sarthakpawar0912/config-repo.git`

This repo contains the actual configuration files:

```
config-repo/
├── order-service.properties          (common config)
├── order-service-dev.properties      (dev environment)
└── order-service-prod.properties     (prod environment - if exists)
```

**Example content of `order-service-dev.properties`:**
```properties
order.message=Hello from Dev Environment!
```

This is the value that gets injected into the `OrderController`'s `message` field!

---

## Tech Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 21 | Programming language |
| **Spring Boot** | 4.0.5 | Application framework |
| **Spring Cloud** | 2025.1.1 | Cloud-native tools (includes Config) |
| **Spring Cloud Config Server** | - | Centralized config server |
| **Spring Cloud Config Client** | - | Client library to fetch config |
| **Spring Web MVC** | - | REST API support |
| **Git (GitHub)** | - | Config file storage with version control |
| **Maven** | - | Build and dependency management |

---

## How to Run (Simple Steps)

### Prerequisites

- Java 21 installed
- Maven installed (or use the included `mvnw` wrapper)
- Internet connection (to access GitHub config repo)

### Step 1: Start the Config Server FIRST

```bash
cd ConfigServerProject
./mvnw spring-boot:run
```

Wait until you see:
```
Started ConfigServerProjectApplication on port 8888
```

### Step 2: Verify Config Server is Working

Open browser and go to:
```
http://localhost:8888/order-service/dev
```

You should see a JSON response with the config values from the Git repo.

### Step 3: Start the Config Client

Open a **new terminal**:

```bash
cd ConfigClientProject
./mvnw spring-boot:run
```

Wait until you see:
```
Started ConfigClientProjectApplication
```

### Step 4: Test the Application

Open browser and go to:
```
http://localhost:8080/order
```

You should see the message that was configured in the Git repo (e.g., "Hello from Dev Environment!")

### Important Notes

```
+---------------------------------------------------------------+
|  MUST START CONFIG SERVER BEFORE CONFIG CLIENT!                |
|                                                                |
|  Order:  1. Config Server (port 8888)                         |
|          2. Config Client (default port 8080)                 |
|                                                                |
|  If client starts before server, it will FAIL because it      |
|  can't fetch its configuration!                               |
+---------------------------------------------------------------+
```

---

## Summary Diagram

```
+================================================================+
|              CENTRALIZED CONFIGURATION ARCHITECTURE             |
+================================================================+
|                                                                  |
|   GITHUB (config-repo.git)                                      |
|   +----------------------------------------------------------+  |
|   | order-service.properties                                  |  |
|   | order-service-dev.properties                              |  |
|   | order-service-prod.properties                             |  |
|   +----------------------------+-----------------------------+  |
|                                |                                 |
|                          Git Clone/Pull                          |
|                                |                                 |
|                                v                                 |
|   CONFIG SERVER (Port 8888)                                     |
|   +----------------------------------------------------------+  |
|   | @EnableConfigServer                                       |  |
|   | Reads configs from Git                                    |  |
|   | Serves configs via REST API                               |  |
|   +----------------------------+-----------------------------+  |
|                                |                                 |
|                          HTTP Response                           |
|                          (Config JSON)                           |
|                                |                                 |
|                                v                                 |
|   CONFIG CLIENT (Order Service)                                  |
|   +----------------------------------------------------------+  |
|   | spring.config.import=configserver:http://localhost:8888   |  |
|   | @Value("${order.message}") -> Injected from server        |  |
|   | GET /order -> Returns the centralized config value        |  |
|   +----------------------------------------------------------+  |
|                                                                  |
+================================================================+
```

---

## Interview Quick Reference

### Common Interview Questions & Answers

**Q1: What is Spring Cloud Config?**
> Spring Cloud Config is a framework that provides server-side and client-side support for externalized configuration in a distributed system. The Config Server stores configs centrally (typically in Git), and Config Clients fetch their configs from it at startup.

**Q2: Why do we need centralized configuration?**
> In microservices, each service has its own config. Managing configs across 50+ services is hard, error-prone, and leads to inconsistencies. Centralized config stores everything in one place, making it easy to manage, update, and version-control.

**Q3: Why is Git used as the backend?**
> Git provides version history, rollback capability, branching, collaboration, and audit trails for free. It's already familiar to developers and integrates naturally with CI/CD pipelines.

**Q4: What annotation enables the Config Server?**
> `@EnableConfigServer` on the main application class.

**Q5: How does the client know which config to fetch?**
> The client uses `spring.application.name` (e.g., `order-service`) and `spring.profiles.active` (e.g., `dev`) to request the right config. The server maps this to `order-service-dev.properties` in the Git repo.

**Q6: What happens if the Config Server is down when the client starts?**
> The client will fail to start because it cannot fetch its configuration. That's why the Config Server must always start first and be highly available in production.

**Q7: What is `spring.config.import`?**
> It's the modern way (Spring Boot 3+) to tell the client where to import its config from. It replaces the older `bootstrap.properties` approach.

**Q8: Can configs be refreshed without restarting?**
> Yes, using `@RefreshScope` and Spring Boot Actuator's `/actuator/refresh` endpoint. For broadcasting refresh across all instances, Spring Cloud Bus can be used.

---

*Document generated for the Central Configuration Project by Sarthak Pawar*
*GitHub: https://github.com/sarthakpawar0912/central-config-project.git*
