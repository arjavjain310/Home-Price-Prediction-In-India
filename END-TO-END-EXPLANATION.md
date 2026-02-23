# End-to-End Explanation: Home Price Prediction

This document explains how the **Home Price Prediction** project works from start to finish: data, model, backend, frontend, and deployment.

---

## 1. Project Overview

**What it does:** A web app where users enter property details (square feet, bedrooms, bathrooms, location) and get an **estimated price in Indian Rupees (₹)**. The estimate is produced by a **Linear Regression** model trained entirely in **Java** (no Python).

**Live link:** https://home-price-prediction-in-india.onrender.com

**Tech stack:** Java 17, Spring Boot 3.2, Maven, Thymeleaf (HTML/CSS), Docker, Render.

---

## 2. High-Level Architecture (MVC)

The app follows **MVC (Model–View–Controller)**:

| Layer      | Role | In this project |
|-----------|------|------------------|
| **Model** | Data and business logic | `PropertyInput`, `PropertyRecord`, `LinearRegression`; CSV dataset |
| **View**  | What the user sees | Thymeleaf templates (`index.html`, `result.html`, `error.html`) + CSS |
| **Controller** | Handles HTTP, calls services | `HomeController` (form, predict, result); `GlobalExceptionHandler` |

**Flow in one line:** User opens the form (View) → submits (Controller) → Controller validates input (Model), calls service to predict (Model) → result is shown (View).

---

## 3. Data: The CSV Dataset

**Where:** `src/main/resources/data/dataset.csv`

**Columns:**
- `square_feet` – area of the property  
- `bhk` – number of bedrooms  
- `bathrooms` – number of bathrooms  
- `location` – city/state/UT (e.g. Mumbai, Delhi, Andhra Pradesh)  
- `price_inr` – price in Indian Rupees (target to predict)

**Purpose:** This CSV is the only “training data.” The app loads it at startup, trains a Linear Regression model on it, and uses that model to predict price for new inputs. Locations in the CSV define the **dropdown options** (all Indian states and union territories + major cities).

**Encoding location:** Location is text, but the model needs numbers. Each location is assigned an **index** (0, 1, 2, …) in the order it first appears in the CSV. That index is the “location” feature for the model.

---

## 4. What Happens at Startup (Training the Model)

When the Spring Boot app starts:

1. **DatasetLoader** reads `data/dataset.csv` from the classpath.
2. Each row is parsed into a **PropertyRecord** (square feet, bhk, bathrooms, location index, price_inr).
3. **ModelTrainer** (runs automatically via `@PostConstruct`):
   - Takes all records, **shuffles** them (fixed seed 42).
   - **Splits** 80% training / 20% test.
   - Builds the **feature matrix X** and **target vector y**:
     - One row per record.
     - Features per row: `[1, square_feet, bhk, bathrooms, location_index]` (the leading 1 is the intercept).
     - Target: `price_inr`.
   - Creates a **LinearRegression** instance and calls **fit(X, y)**.
   - Predicts on the **test set**, computes **MAE** and **RMSE**, and **logs them to the console**.

So by the time the first HTTP request arrives, the model is already trained and ready to predict.

---

## 5. The Linear Regression Model (Java, No Python)

**Class:** `com.homeprice.model.ml.LinearRegression`

**Idea:** Predict price as a linear combination of features:

`price ≈ w0×1 + w1×square_feet + w2×bhk + w3×bathrooms + w4×location_index`

The **weights** (w0, w1, …, w4) are learned from the training data.

**How it’s done (Ordinary Least Squares – OLS):**

1. **Normal equation:**  
   `weights = (X'X)^(-1) X'y`  
   - X = design matrix (each row = one property, columns = 1, square_feet, bhk, bathrooms, location_index)  
   - y = vector of prices in the training set  
   - X' = transpose of X  

2. **Implementation:**  
   - Compute **X'X** (matrix) and **X'y** (vector).  
   - Invert **X'X** (Gauss–Jordan elimination in Java).  
   - Multiply inverse by **X'y** to get the weight vector.

3. **Prediction:**  
   For a new property, build the feature row `[1, sq_ft, bhk, baths, location_index]`, then:  
   `predicted_price = weights[0]*1 + weights[1]*sq_ft + … + weights[4]*location_index`

All of this is implemented in Java; no Python or external ML library is used.

---

## 6. Request Flow: From Browser to Price

**Step 1 – User opens the app**  
- Browser: `GET /` (or `GET /` with trailing slash).  
- **HomeController.home()** runs:  
  - Creates an empty **PropertyInput**.  
  - Gets the list of **locations** from **ModelTrainer** (from CSV).  
  - Adds both to the **Model** and returns view name **"index"**.  
- Thymeleaf renders **index.html** with the form (square feet, BHK, bathrooms, location dropdown).  
- User sees the dark-themed form with the house image and “Predict Price” button.

**Step 2 – User submits the form**  
- Browser: `POST /predict` with form data (squareFeet, bhk, bathrooms, location).  
- **HomeController.predict()** runs:  
  - Spring binds form data to **PropertyInput** and runs **validation** (e.g. min/max for square feet, BHK, bathrooms; non-blank location).  
  - If validation fails: same **index** view is returned with error messages.  
  - If validation passes:  
    - **PredictionService.predictPriceInr(propertyInput)** is called.  
    - Service gets the trained **LinearRegression** from **ModelTrainer**, gets **location index** for the selected location, builds the feature vector `[1, sq_ft, bhk, baths, location_index]`, calls **model.predict(features)**, rounds and clamps to get **price in INR**.  
    - **formatPriceInr(price)** turns it into a string like “₹ 1,00,00,000”.  
  - Controller puts **formattedPrice** (and related data) in **flash attributes** and redirects to **/result**.  
- Browser: **GET /result**.  
- **HomeController.result()** runs: if **formattedPrice** is in the model (from redirect), returns view **"result"**; otherwise redirects back to **"/"**.  
- Thymeleaf renders **result.html** showing the estimated price in ₹.  
- User can click “Predict Another” to go back to **/**.

**Step 3 – Errors**  
- Any unhandled exception is caught by **GlobalExceptionHandler**, which returns an **error** view with a message.  
- Validation errors are shown on the same form (index) with field-level messages.

So the “end-to-end” path is: **CSV → train model at startup → user fills form → POST /predict → validate → predict with LinearRegression → format price → redirect to /result → show price in ₹.**

---

## 7. Frontend (View Layer)

- **Templates:** Thymeleaf HTML under `src/main/resources/templates/`:  
  - **index.html** – form (square feet, BHK, bathrooms, location dropdown), dark theme, inline critical CSS, house SVG.  
  - **result.html** – shows the estimated price in ₹ and “Predict Another.”  
  - **error.html** – generic error message and “Back to Home.”

- **Static assets:** `src/main/resources/static/css/style.css` – full dark theme, layout, buttons, form styling.

- **Validation:**  
  - Client-side: HTML5 (required, min, max).  
  - Server-side: Bean Validation on **PropertyInput** (e.g. `@NotNull`, `@Min`, `@Max`, `@NotBlank`).  
  - Invalid input re-displays the form with error messages.

- **Session:** Cookie-only session (`server.servlet.session.tracking-modes=cookie`) so the URL stays clean (no `;jsessionid=...`).

---

## 8. Backend (Controller and Services)

- **HomeController**  
  - `GET /`, `GET /` with `/` → **home()** → index form.  
  - `POST /predict` → **predict()** → validate → call **PredictionService** → redirect to **/result** with flash data.  
  - `GET /result` → **result()** → show result or redirect to **/**.

- **PredictionService**  
  - Uses **ModelTrainer** to get the trained **LinearRegression** and location list.  
  - Converts **PropertyInput** to a feature vector, calls **model.predict()**, formats and returns price in ₹.

- **ModelTrainer**  
  - Loads data via **DatasetLoader**, splits, trains **LinearRegression**, stores model and location order, exposes them for prediction and for the dropdown.

- **DatasetLoader**  
  - Reads CSV, parses rows into **PropertyRecord**, assigns location indices, returns records and ordered location list.

- **GlobalExceptionHandler**  
  - Handles exceptions and returns the **error** view with a user-friendly message.

---

## 9. Deployment (Docker on Render)

- **Build:**  
  - **Dockerfile** has a multi-stage build: first stage uses Maven to compile and package the app into a JAR; second stage uses a JRE image and only copies that JAR.  
  - The JAR includes the CSV and all resources; no external database.

- **Run:**  
  - Container runs: `java -Dserver.port=${PORT} -jar app.jar`.  
  - **PORT** is set by Render so the app listens on the correct port.  
  - **server.address=0.0.0.0** in `application.properties` so the server accepts connections from the internet.

- **Render:**  
  - Render builds the Docker image from the GitHub repo, runs the container, and assigns a public URL (e.g. **https://home-price-prediction-in-india.onrender.com**).  
  - Anyone with that link can use the app. On the free tier, the service may spin down when idle; the first request after that can take ~50 seconds.

---

## 10. How to Run and Use

**Locally:**  
- `./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"`  
- Open **http://localhost:8080**.  
- Fill the form and click **Predict Price** to see the estimated price in ₹.  
- In the console you’ll see the **MAE** and **RMSE** from the test set (printed at startup).

**On the web:**  
- Open **https://home-price-prediction-in-india.onrender.com**.  
- Use the form the same way; the same model and flow apply.

---

## 11. Summary Diagram

```
[User] → Browser (GET /) → HomeController.home() → ModelTrainer.getLocationOrder()
       → Thymeleaf index.html → Form with locations from CSV

[User] → Submit form (POST /predict) → HomeController.predict()
       → Validate PropertyInput → PredictionService.predictPriceInr()
       → ModelTrainer.getModel() → LinearRegression.predict([1, sqft, bhk, baths, locIdx])
       → Format price → Redirect to /result with flash data

[User] → GET /result → HomeController.result() → result.html → "Estimated price: ₹ ..."
```

**Data flow:** CSV → DatasetLoader → PropertyRecords → ModelTrainer (split, fit LinearRegression) → at runtime: PropertyInput → feature vector → LinearRegression.predict() → formatted ₹ string → result page.

This is the complete end-to-end explanation of the project: from the CSV and model training at startup, through the MVC layers and request/response flow, to the user seeing the predicted price in Indian Rupees and how the app is deployed on Render.
