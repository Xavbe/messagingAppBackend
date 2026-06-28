# Messaging App Backend

This application is the backend for the selfhosted messaging APP.
To use it completly you need to add a 256 bits key in the application properties.


# Prerequisites
- Java 21
- Docker
- Gradle

# Running Locally
1. Clone the reop
2. Create the Docker container for PostgreSQL docker compose up -d
3. Execute tests : ./gradlew test
4. Run the app:  ./gradlew bootRun 

# Installation
Make sure you install the hooks for PLantUML. In the PowerShell execute in the repo directory:
.\script\install-hooks.ps1