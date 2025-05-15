[![](https://img.shields.io/discord/677642178083946580?color=%23768ACF&label=Discord)](https://discord.gg/U8NcPcHxW3) [![Twitch Status](https://img.shields.io/twitch/status/thiagorigonatti?label=Twitch)](https://twitch.tv/thiagorigonatti)
[![YouTube Channel Subscribers](https://img.shields.io/youtube/channel/subscribers/UCEDjQf5cEkH4320GevAitUA?label=Thiago%20Rigonatti)](https://www.youtube.com/thiagorigonatti)
[![](https://img.shields.io/badge/Linked-In-blue)](https://www.linkedin.com/in/thiagorigonatti/)
[![](https://img.shields.io/badge/Udemy-2%20Courses-blueviolet)](https://www.udemy.com/user/thiago-rigonatti-2/)

[![](https://img.shields.io/badge/GitHub-Repository-white)](https://github.com/thiagorigonatti/capital-gains/)
[![](https://img.shields.io/badge/Download-Jenkins-purple.svg)](https://jenkins.thecoders.com.br/job/capital-gains/)
[![](https://img.shields.io/badge/Javadoc-Overview-magenta)](https://thiagorigonatti.github.io/capital-gains/)
[![](https://img.shields.io/badge/License-AGPL3.0-darkgreen)](https://github.com/thiagorigonatti/capital-gains/blob/main/LICENSE)
[![](https://img.shields.io/badge/Docker-Image-aqua)](https://hub.docker.com/repository/docker/thiagorigonatti/capitalgains/tags)

📄 Leia em outro idioma: [Inglês](README_EN.md)

# Calculadora de imposto sobre ganho de capital
Calcula o imposto sobre o ganho de capital de operações financeiras de compra e venda de ações. A aplicação processa as operações de forma sequencial, levando em conta o lucro obtido e aplicando a alíquota de 20% sobre o lucro, com possibilidade de compensar prejuízos acumulados.

![Demo](demo.gif)

---
## 📐 Decisões técnicas e arquiteturais
- **Linguagem de programação**: O projeto foi desenvolvido em **Java**.
- **Entrada e Saída**: A entrada e saída são em **JSON**, garantindo uma interface moderna e compatível com sistemas externos.
- **Estrutura Modular**: O projeto foi dividido em classes com responsabilidades claras, facilitando manutenção e testes.
---
## 📚 Justificativa para o uso das bibliotecas
- **Jackson (`com.fasterxml.jackson.core:jackson-databind`)**: Escolhido para facilitar o parsing de JSON com uma API madura, bem documentada e amplamente usada na indústria. Outras alternativas são `org.json` e `com.google.code.gson`
- **JUnit 5**: Utilizado para os testes automatizados, por oferecer uma API moderna, suporte a testes parametrizados, e ser a evolução natural do ecossistema JUnit.
---
## 🗳️ Requisitos
- Java 21+
- Gradle
- Docker
- Unix-like, Windows 10 and newer, Windows Server 2016 and newer
---
## ⛴️ Instruções para Executar com Docker
#### **Foi incluída a imagem docker. Carregue a imagem para o docker com**:
```bash
docker load -i capitalgains.tar
```
#### **Execute com o comando baixo se quiser que o programa fique aguardando uma lista na entrada padrão**:
```bash
docker run --rm -it thiagorigonatti/capitalgains:latest
```
#### **Execute com o comando baixo se quiser fornecer uma lista ao programa e rodá-lo**:
```bash
echo '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":5.00, "quantity": 5000},{"operation":"sell", "unit-cost":20.00, "quantity": 3000}]' | docker run --rm -i thiagorigonatti/capitalgains:latest
```
#### **Execute com o comando baixo se quiser fornecer um arquivo com várias listas, uma por linha**:
```bash
docker run --rm -i thiagorigonatti/capitalgains:latest < input.txt
```
---
## 📦 Como Compilar
> [!NOTE]
> Para usar os comandos gradle é necessário ter uma distribuição java instalada e a variável de ambiente JAVA_HOME corretamente definida.
#### **Par compilar tudo use no terminal**:
```bash
./gradlew buildAll
```
#### **Para executar os testes, use no terminal**:
```bash
./gradlew test
```
#### **Para compilar a aplicação, use no terminal**:
```bash
./gradlew shadowJar
```
#### **Para gerar a documentação, use no terminal**:
```bash
./gradlew javadoc
```
---
## 📗 Documentação dos Argumentos de Linha de Comando

#### Este programa aceita os seguintes argumentos de linha de comando relacionados ao tamanho de buffer. Os tamanhos devem ser fornecidos no formato `-bsi<num><k|m|g>` ou `-bso<num><k|m|g>`, onde:

- `<num>` é um número inteiro.
- `<k|m|g>` representa a unidade:
  - `k`: kilobytes (KB)
  - `m`: megabytes (MB)
  - `g`: gigabytes (GB)

### `-bsi<num><k|m|g>`

**Nome:** Buffer de entrada  
**Formato:** `-bsi1M`, `-bsi512k`, `-bsi2g`  
**Descrição:**  
_Por padrão o buffer de entrada tem o valor de 8.192 bytes ou 8kb; o buffer faz o dado ser armazenado em memória no heap, quanto maior o buffer mais dados podem ser processados sem ter de chamar a entrada padrão e consequentemente o programa levará menos tempo para terminar a tarefa._

### `-bso<num><k|m|g>`

**Nome:** Buffer de saída  
**Formato:** `-bso1M`, `-bso256k`, `-bso4g`  
**Descrição:**  
_Por padrão o buffer de saída tem o valor de 8.192 bytes ou 8kb; o buffer faz o dado ser armazenado em memória no heap, quanto maior o buffer mais dados podem ser processados sem ter de chamar a saída padrão e consequentemente o programa levará menos tempo para terminar a tarefa._

### `-pel`

**Nome:** Imprimir toda linha  
**Formato:** `-pel`  
**Descrição:**  
_Se especificado esse argumento, buffer size out será sobrescrito com 8.192 bytes, uma vez que -pel faz a saída ser chamada a cada nova linha, independentemente do tamanho do buffer size out, se especificado._

### `-t`

**Nome:** Imprimir tempo  
**Formato:** `-t`  
**Descrição:**  
_Se especificado esse argumento, ao término da tarefa será exibido o tempo que a tarefa levou pra ser completada._

---
## 🫙 Instruções para Executar o .Jar

#### **Execute com o comando baixo se quiser que o programa fique aguardando uma lista na entrada padrão**:
```bash
java -jar build/libs/CapitalGainsCalculator.jar -pel
```
#### **Execute com o comando baixo se quiser fornecer uma lista ao programa e rodá-lo**:
```bash
echo '[{"operation":"buy", "unit-cost":10.00, "quantity": 10000},{"operation":"sell", "unit-cost":5.00, "quantity": 5000},{"operation":"sell", "unit-cost":20.00, "quantity": 3000}]' | java -jar build/libs/CapitalGainsCalculator.jar
```
#### **Execute com o comando baixo se quiser fornecer um arquivo com várias listas, uma por linha**:
```bash
java -jar build/libs/CapitalGainsCalculator.jar < input.txt -bsi32M -bso10M
```
#### **Execute com o comando baixo se quiser fornecer um arquivo com várias listas, uma por linha e redirecionar o resultado para outra saída que não stdout**:
```bash
java -jar build/libs/CapitalGainsCalculator.jar > output.txt
```
---
## 🗒️ Notas Adicionais

#### Decidi de criar uma JRE personalizada, utilizando apenas os módulos de que a aplicação depende, isso diminui o tamanho da imagem, de ~820MB para ~75MB usando linux/alpine.

#### Para a task createCustomJRE funcionar, é necessário ter uma distribuição java contendo JDK, jmods e definir JAVA_HOME

`sudo yum install java-21-amazon-corretto-devel`  
`sudo yum install java-21-amazon-corretto-jmods`

#### Utilizei o ambiente de teste com Amazon Linux 2023 (al2023-ami-2023.7.20250331.0-kernel-6.1-x86_64).

#### IDE
```
IntelliJ IDEA 2024.3.5 (Ultimate Edition)
Build #IU-243.26053.27, built on March 16, 2025
```
---
