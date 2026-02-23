# Home Price Prediction

A Spring Boot web application that predicts home prices in Indian Rupees (₹) using a simple **Linear Regression** model trained in Java (no Python). Users enter property details (square feet, BHK, bathrooms, location) and get an estimated price.

## Live link (view by anyone)

**https://home-price-prediction-in-india.onrender.com**

Anyone with this link can open the app and get price estimates.  
*Note: On the free tier the app may take ~50 seconds to respond after a period of inactivity.*

## Features

- **MVC architecture**: Controller, Service, Domain models, Thymeleaf views
- **Linear Regression** implemented in Java (OLS via normal equation)
- **CSV dataset** under `src/main/resources/data/dataset.csv`; train/test split and training on startup
- **Evaluation metrics**: MAE and RMSE printed in the console after training
- **Input validation** (Bean Validation) and **global exception handling**
- **Clean UI**: HTML/CSS with Thymeleaf; price displayed in ₹ with Indian number formatting
- **Deployable** on Render or Railway with a public link

## Tech Stack

- Java 17, Spring Boot 3.2, Maven
- Thymeleaf, Spring Validation, Spring Actuator (health)

## Project Structure

```
├── pom.xml
├── Dockerfile
├── Procfile
├── render.yaml
├── DEPLOYMENT.md          # Deployment steps for Render/Railway
├── src/main/
│   ├── java/com/homeprice/
│   │   ├── HomePricePredictionApplication.java
│   │   ├── controller/HomeController.java
│   │   ├── exception/GlobalExceptionHandler.java
│   │   ├── model/
│   │   │   ├── domain/PropertyInput.java, PropertyRecord.java
│   │   │   └── ml/LinearRegression.java
│   │   └── service/DatasetLoader.java, ModelTrainer.java, PredictionService.java
│   └── resources/
│       ├── application.properties
│       ├── data/dataset.csv
│       ├── static/css/style.css
│       └── templates/index.html, result.html, error.html
└── README.md
```

## Run Locally

```bash
mvn clean package
mvn spring-boot:run
```

Open **http://localhost:8080**, fill the form, and click **Predict Price** to see the estimated price in ₹.

## Deploy Online

See **[DEPLOYMENT.md](DEPLOYMENT.md)** for:

- Running on **localhost**
- Deploying on **Render** (Docker or Blueprint)
- Deploying on **Railway** (Maven or Docker)

After deployment, the service gets a public URL that anyone can use to access the app.

## Dataset

`src/main/resources/data/dataset.csv` has columns: `square_feet`, `bhk`, `bathrooms`, `location`, `price_inr`. You can replace or extend this file; locations in the CSV are used to build the dropdown and the model’s location encoding.
