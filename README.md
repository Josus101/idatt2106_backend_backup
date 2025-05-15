   # Krisefikser

## Overview

This application provides a RESTful API with various endpoints that handle user request from their interactions with the website Krisefisker.no.

## Technologies
- Java Version: 21
- Framework: Spring Boot 3.4.4
- Build Tool: Maven
- Database: JPA with H2 Database (Development)
- Security: Spring Security with JWT Authentication
- Documentation: OpenAPI (Swagger)
- Testing: JUnit, Mockito
- Code Quality: JaCoCo for test coverage
- Development Tools: Lombok


## Table of Contents
[Installation](#installation)

[Configuration](#configuration)

[Running the Application](#running-the-application)

[API Documentation](#api-documentation)

[Testing](#testing)

[Deployment](#deployment)


## Installation
### Requirements
- Java version 21 or later
- Maven
### Step-by-step installation
1. Cloning the Repository

   Clone with https:
   ```bash
   clone https://gitlab.stud.idi.ntnu.no/idatt2106-v25-01/backend.git
   ```
   
   Clone wit ssh:
   ```bash
   clone git@gitlab.stud.idi.ntnu.no:idatt2106-v25-01/backend.git
   ```
   
   Navigate to the project directory:
   ```bash
   cd path/to/backend
   ```

2. Installing Dependencies
   Make sure you have Java and Maven installed:

   ```bash
   java -version
   mvn -v
   ```
   
   If not, install them:
   - On Ubuntu:
   
   ```bash
   sudo apt update
   sudo apt install openjdk-17-jdk maven
   ```
   
   - On macOS (using Homebrew):
   ```bash
   brew install openjdk@21 maven
   ```
   - On Windows: Download from the Oracle website and Maven website.

   Install project dependencies:
   ```bash
   mvn clean install
   ```

## Configuration
Create an .env file in the root directory:
```bash
touch .env
```
Add your environment variables to the .env file:
```bash
EMAIL_USERNAME=your@gmail.com
EMAIL_PASSWORD=your_password
RECAPTCHA_SECRET=recaptcha_secret_key
```
in the application.properties file:
```bash
# Database
spring.datasource.url=jdbc:h2:file:./data/database/db;CIPHER=AES
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=your_db_name
spring.datasource.password=yout_db_name userpassword encryptionpassword(optional but recomended)
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect


jwt.secret=mysecretkey
recaptcha.secret=myrecaptcha
```

## Running the Application
run the folowing command in the terminal(in the root folder) to start the application
```bash
mvn spring-boot:run
```

## API Documentation
Se the Servertjenester on the Wiki for the documentation

## Testing
To run tests locally use:
```bash
mvn test
```
To generate a code coverage report, use:
```bash
mvn verify
```
The generated report can be found in:
```bash
target/site/jacoco/index.html
```

## Deployment
### Building the Application
First, package the application as a JAR file:

```bash
mvn clean package
```
The generated JAR will be located at:
```bash
target/yourproject-0.0.1-SNAPSHOT.jar
```

### Running the JAR File
Run the JAR file with environment variables:
```bash
java -jar -Dspring.profiles.active=prod target/yourproject-0.0.1-SNAPSHOT.jar
```
