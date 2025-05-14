[![](https://img.shields.io/discord/677642178083946580?color=%23768ACF&label=Discord)](https://discord.gg/U8NcPcHxW3) [![Twitch Status](https://img.shields.io/twitch/status/thiagorigonatti?label=Twitch)](https://twitch.tv/thiagorigonatti)
[![YouTube Channel Subscribers](https://img.shields.io/youtube/channel/subscribers/UCEDjQf5cEkH4320GevAitUA?label=Thiago%20Rigonatti)](https://www.youtube.com/thiagorigonatti)
[![](https://img.shields.io/badge/Linked-In-blue)](https://www.linkedin.com/in/thiagorigonatti/)
[![](https://img.shields.io/badge/Udemy-2%20Courses-blueviolet)](https://www.udemy.com/user/thiago-rigonatti-2/)

[![](https://img.shields.io/badge/GitHub-Repository-white)](https://github.com/thiagorigonatti/capital-gains/)
[![](https://img.shields.io/badge/Download-Jenkins-purple.svg)](https://jenkins.thecoders.com.br/job/capital-gains/)
[![](https://img.shields.io/badge/Javadoc-Overview-magenta)](https://thiagorigonatti.github.io/capital-gains/)
[![](https://img.shields.io/badge/License-AGPL3.0-darkgreen)](https://github.com/thiagorigonatti/capital-gains/blob/main/LICENSE)
[![](https://img.shields.io/badge/Docker-Image-aqua)](https://hub.docker.com/repository/docker/thiagorigonatti/capitalgains/tags)

📄 Read in another language: [Brazilian Portuguese](README.md)

# Capital Gains Tax Calculator
Calculates the capital gains tax from financial transactions involving the purchase and sale of stocks. The application processes operations sequentially, taking into account the profit obtained and applying a 20% rate on the gain, with the possibility of offsetting accumulated losses.

![Demo](demo.gif)

---
## 📐 Technical and Architectural Decisions
- **Programming Language**: The project was developed in **Java**.
- **Input and Output**: Input and output are in **JSON**, ensuring a modern interface compatible with external systems.
- **Modular Structure**: The project was divided into classes with clear responsibilities, making maintenance and testing easier.

---
## 📚 Justification for Library Usage
- **Jackson (`com.fasterxml.jackson.core:jackson-databind`)**: Chosen to simplify JSON parsing with a mature, well-documented API widely used in the industry. Alternatives include `org.json` and `com.google.code.gson`.
- **JUnit 5**: Used for automated testing, offering a modern API, support for parameterized tests, and being the natural evolution of the JUnit ecosystem.

---
## 🗳️ Requirements
- Java 21+
- Gradle
- Docker
- Unix-like, Windows 10 and newer, Windows Server 2016 and newer

---
## ⛴️ Instructions to Run with Docker
#### **The Docker image is included. Load the image into Docker with**:
```bash
docker load -i capitalgains.tar
```
#### **Run the following command to make the program wait for input on stdin**:
```bash
docker run --rm -it thiagorigonatti/capitalgains:latest
```
#### **Run the following command to provide input and execute the program**:
```bash
echo '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":5.00, "quantity": 5000},{"operation":"sell", "unit-cost":20.00, "quantity": 3000}]' | docker run --rm -i thiagorigonatti/capitalgains:latest
```
#### **Run the following command to provide a file with multiple input lines**:
```bash
docker run --rm -i thiagorigonatti/capitalgains:latest < input.txt
```

---
## 📦 How to Compile
> [!NOTE]
> The following gradle commands requires JDK installed and JAVA_HOME environment variable correctly set.
#### **To compile everything, run in the terminal**:
```bash
./gradlew buildAll
```
#### **To run the tests, use**:
```bash
./gradlew test
```
#### **To compile the application, use**:
```bash
./gradlew shadowJar
```
#### **To generate documentation, do**:
```bash
./gradlew javadoc
```

---
## 📗 Command-Line Argument Documentation

#### This program accepts the following command-line arguments related to buffer size. Sizes must be provided in the format `-bsi<num><k|m|g>` or `-bso<num><k|m|g>`, where:

- `<num>` is an integer.
- `<k|m|g>` represents the unit:
    - `k`: kilobytes (KB)
    - `m`: megabytes (MB)
    - `g`: gigabytes (GB)

### `-bsi<num><k|m|g>`

**Name:** Input Buffer  
**Format:** `-bsi1M`, `-bsi512k`, `-bsi2g`  
**Description:**  
_Default is 8,192 bytes or 8kb; a larger input buffer means more data can be stored in memory (heap), reducing I/O and execution time._

### `-bso<num><k|m|g>`

**Name:** Output Buffer  
**Format:** `-bso1M`, `-bso256k`, `-bso4g`  
**Description:**  
_Default is 8,192 bytes or 8kb; a larger output buffer reduces the need for stdout access and speeds up execution._

### `-pel`

**Name:** Preserve Spaces and Lines  
**Format:** `-pel`  
**Description:**  
_When specified, output buffer size is overridden to 8,192 bytes. Output is flushed on each line regardless of buffer size._

### `-t`

**Name:** Execution Time  
**Format:** `-t`  
**Description:**  
_When specified, prints the execution time upon completion._

---
## 🫙 Instructions to Run the .Jar

#### **Run to make the program wait for input on stdin**:
```bash
java -jar build/libs/CapitalGainsCalculator.jar -pel
```
#### **Run to provide input and execute the program**:
```bash
echo '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":5.00, "quantity": 5000},{"operation":"sell", "unit-cost":20.00, "quantity": 3000}]' | java -jar build/libs/CapitalGainsCalculator.jar
```
#### **Run with a file containing multiple input lines**:
```bash
java -jar build/libs/CapitalGainsCalculator.jar < input.txt -bsi32M -bso10M
```
#### **Run and redirect the result to a file**:
```bash
java -jar build/libs/CapitalGainsCalculator.jar > output.txt
```

---
## 🗒️ Additional Notes

#### A custom JRE was created using only required modules, reducing image size from ~820MB to ~75MB using linux/alpine.

#### For the `createCustomJRE` task to work, a Java distribution with JDK and jmods is required and JAVA_HOME must be set.

`sudo yum install java-21-amazon-corretto-devel`  
`sudo yum install java-21-amazon-corretto-jmods`

#### Tested environment: Amazon Linux 2023 (al2023-ami-2023.7.20250331.0-kernel-6.1-x86_64).

#### IDE
```
IntelliJ IDEA 2024.3.5 (Ultimate Edition)
Build #IU-243.26053.27, built on March 16, 2025
```
