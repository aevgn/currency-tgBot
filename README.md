
# Currency Telegram Bot

A Telegram bot built with Java and Spring Boot that converts apartment rental prices from USD to BYN using the official exchange rate provided by the National Bank of the Republic of Belarus (NBRB).

## Features

- Convert rental prices from USD to BYN.
- Fetch the official USD exchange rate from the NBRB API.
- Automatically update and store exchange rates.
- Save rental price data in PostgreSQL.
- Handle user interactions through Telegram commands.

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL
- TelegramBots API
- Maven

## Getting Started

1. Clone the repository:

   ```bash
   git clone https://github.com/aevgn/currency-tgBot.git
   cd currency-tgBot
   ```

2. Create a PostgreSQL database.
3. Configure your database credentials and Telegram bot token in `application.properties`.
4. Run the application using your IDE or Maven.

## Author

[Eugene](https://github.com/aevgn)
