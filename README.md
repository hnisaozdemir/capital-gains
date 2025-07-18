# Capital Gains Tax Calculator 🧮📈

![Version](https://img.shields.io/badge/version-1.0.0-blue)
![License](https://img.shields.io/badge/license-MIT-green)
![Build Status](https://img.shields.io/badge/build-passing-brightgreen)

Welcome to the **Capital Gains** repository! This project is designed to help you calculate taxes on buying and selling shares. It is a command-line interface (CLI) tool created as part of the NuBank challenge.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Examples](#examples)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Introduction

Calculating capital gains can be complex, especially when it comes to buying and selling shares. This CLI tool simplifies the process, making it easy for users to determine their tax obligations. The tool focuses on providing accurate calculations based on user input, ensuring clarity and efficiency.

## Features

- **User-Friendly Interface**: Simple commands to enter buy and sell transactions.
- **Accurate Calculations**: Calculates capital gains and taxes owed based on user data.
- **Multiple Transactions**: Supports multiple buy and sell entries for comprehensive analysis.
- **Export Results**: Option to export results for record-keeping or further analysis.
- **Lightweight**: Minimal dependencies ensure quick installation and execution.

## Installation

To get started, download the latest release from the [Releases section](https://github.com/hnisaozdemir/capital-gains/releases). Follow these steps:

1. Visit the link above to find the latest version.
2. Download the executable file suitable for your operating system.
3. Extract the files if necessary.
4. Follow the instructions in the downloaded package to install the tool.

## Usage

After installation, you can start using the Capital Gains calculator. Open your terminal and run the executable. Here’s a quick overview of the commands:

- **Add Buy Transaction**: 
  ```
  ./capital-gains buy <share-name> <number-of-shares> <purchase-price>
  ```
- **Add Sell Transaction**: 
  ```
  ./capital-gains sell <share-name> <number-of-shares> <selling-price>
  ```
- **Calculate Gains**: 
  ```
  ./capital-gains calculate
  ```
- **Export Results**: 
  ```
  ./capital-gains export <filename>
  ```

For detailed instructions, please refer to the help command:
```
./capital-gains help
```

## Examples

### Adding Transactions

To add a buy transaction for 10 shares of "ABC Corp" at $50 each:
```
./capital-gains buy ABC 10 50
```

To add a sell transaction for 5 shares of "ABC Corp" at $70 each:
```
./capital-gains sell ABC 5 70
```

### Calculating Gains

Once you have entered your transactions, run the following command to calculate your capital gains:
```
./capital-gains calculate
```

The tool will provide a summary of your transactions, including total gains and taxes owed.

### Exporting Results

If you want to keep a record of your calculations, use the export command:
```
./capital-gains export my_results.txt
```

This will save your results in a text file for future reference.

## Contributing

We welcome contributions! If you would like to improve the Capital Gains calculator, please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -m 'Add new feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Open a pull request.

Please ensure your code follows the existing style and includes appropriate tests.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

For any questions or suggestions, please reach out to the project maintainer:

- **Name**: H. Nisa Ozdemir
- **Email**: hnisa@example.com

Thank you for checking out the Capital Gains calculator! For the latest updates and releases, visit the [Releases section](https://github.com/hnisaozdemir/capital-gains/releases).