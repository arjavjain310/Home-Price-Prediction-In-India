# Interview Questions & Answers – Home Price Prediction Project

Use this as a reference for questions interviewers might ask about your Home Price Prediction project, with concise answers.

---

## 1. Project overview & motivation

**Q1. What is this project about?**  
A web application that estimates home prices in Indian Rupees (₹) from property details: square feet, number of bedrooms (BHK), bathrooms, and location. Users submit a form and get a predicted price. The model is a Linear Regression trained in Java on a CSV dataset; no Python or external ML libraries.

**Q2. Why did you build it?**  
To demonstrate end-to-end skills: Java/Spring Boot backend, in-house ML (Linear Regression), Thymeleaf frontend, validation, and deployment (Docker on Render). It also shows I can implement ML from scratch without Python.

**Q3. What is the live link?**  
https://home-price-prediction-in-india.onrender.com — anyone with the link can use it.

---

## 2. Architecture & design

**Q4. What architecture did you use?**  
MVC (Model–View–Controller).  
- **Model:** Domain objects (PropertyInput, PropertyRecord), Linear Regression, and CSV data.  
- **View:** Thymeleaf templates (index, result, error) and CSS.  
- **Controller:** HomeController handles GET /, POST /predict, GET /result; GlobalExceptionHandler handles errors.

**Q5. Why MVC?**  
Clear separation of concerns: controller handles HTTP and flow, service layer holds business/ML logic, view only renders. Easy to test and extend (e.g. add an API later without changing the model).

**Q6. How do the main components interact?**  
User hits the root URL → HomeController returns the form (with locations from ModelTrainer). On submit, controller validates input, calls PredictionService, which uses the trained LinearRegression from ModelTrainer and returns a price. Controller redirects to /result with the formatted price; result view displays it.

**Q7. Why Spring Boot?**  
Fast setup, embedded server, dependency injection, built-in validation and Thymeleaf support. Good fit for a single deployable JAR and cloud deployment (e.g. Render).

---

## 3. Data & dataset

**Q8. Where does the training data come from?**  
A CSV file inside the project: `src/main/resources/data/dataset.csv`. Columns: square_feet, bhk, bathrooms, location, price_inr.

**Q9. How do you load the CSV?**  
DatasetLoader reads it from the classpath using Spring’s ClassPathResource, parses the header to find column indices, then each row into a PropertyRecord (numeric fields + location encoded as an integer index).

**Q10. How is location (text) used in the model?**  
Location is categorical. Each distinct location is assigned an index (0, 1, 2, …) in the order it first appears in the CSV. That index is the feature the model uses. The same mapping is used for the dropdown and for prediction.

**Q11. Why not one-hot encoding?**  
One-hot would create many columns (one per location) and we have many states/UTs. Label encoding (index) keeps one feature per property and is simple for Linear Regression; for this project size it’s acceptable. For more advanced models, one-hot or embeddings could be better.

**Q12. How many locations / rows do you have?**  
The CSV includes major cities (e.g. Mumbai, Delhi, Bangalore) and all Indian states and union territories, with multiple rows per location so the model can learn different price ranges.

---

## 4. Machine learning – Linear Regression

**Q13. Why Linear Regression?**  
Interpretable, no extra ML libraries needed, and quick to implement in Java. For a portfolio project with limited features and a small dataset, it’s a good fit to show understanding of the math and code.

**Q14. Explain the math behind your Linear Regression.**  
We predict price as a linear combination of features:  
`y = w0 + w1×square_feet + w2×bhk + w3×bathrooms + w4×location_index`.  
We use the **normal equation** for OLS:  
`weights = (X'X)^(-1) X'y`,  
where X is the design matrix (each row = [1, sq_ft, bhk, baths, location_index]), and y is the vector of prices. The first column of 1s gives the intercept (w0).

**Q15. How did you implement it in Java?**  
I compute X'X (matrix multiply) and X'y (matrix–vector). Then I invert X'X using Gauss–Jordan elimination (no external linear algebra library). Finally I multiply the inverse by X'y to get the weight vector. Prediction is the dot product of weights and the feature vector.

**Q16. What is train/test split and why?**  
Data is split (e.g. 80% train, 20% test) after shuffling with a fixed seed. We fit the model only on the training set and evaluate on the test set to estimate how well the model generalizes and to report MAE/RMSE.

**Q17. What are MAE and RMSE?**  
- **MAE (Mean Absolute Error):** average of |actual − predicted|. Easy to interpret (same units as price).  
- **RMSE (Root Mean Squared Error):** square root of the mean of (actual − predicted)². Penalizes large errors more.  
Both are printed in the console after training for the test set.

**Q18. When is the model trained?**  
At application startup. ModelTrainer has a method annotated with @PostConstruct that loads the CSV, splits data, fits LinearRegression, and logs MAE/RMSE. So the model is ready before any HTTP request.

**Q19. What if the dataset is very large?**  
Loading the full CSV into memory and using the normal equation would become slow or infeasible. Alternatives: stochastic gradient descent (SGD), mini-batch training, or using a library (e.g. Apache Commons Math, or a separate Python service) for large-scale regression.

**Q20. How would you improve the model?**  
Use more features (e.g. age of building, amenities), try polynomial or interaction terms, regularisation (Ridge/Lasso), or tree-based models (Random Forest, XGBoost). Could also collect more data and do proper feature engineering and cross-validation.

---

## 5. Backend – Spring Boot & Java

**Q21. What does HomeController do?**  
It maps: GET / (and /) to the home form, POST /predict to handle form submit (validate, predict, redirect to result), and GET /result to show the predicted price. It uses ModelTrainer for locations and PredictionService for the prediction.

**Q22. How do you validate user input?**  
Bean Validation (Jakarta Validation) on PropertyInput: e.g. @NotNull, @Min/@Max for BHK and bathrooms, @DecimalMin/@DecimalMax for square feet, @NotBlank for location. In the controller we check BindingResult; if there are errors we return the same form with messages.

**Q23. What is the role of PredictionService?**  
It takes a validated PropertyInput, gets the trained model and location index from ModelTrainer, builds the feature vector [1, sq_ft, bhk, baths, location_index], calls model.predict(), then formats the result in Indian Rupee format (e.g. ₹ 1,00,00,000) and returns it.

**Q24. Why redirect after POST /predict?**  
To follow the Post-Redirect-Get (PRG) pattern. The result is shown on GET /result, so refreshing the page doesn’t resubmit the form and the URL is bookmarkable. We pass the formatted price via redirect flash attributes.

**Q25. How do you handle exceptions?**  
GlobalExceptionHandler (@ControllerAdvice) catches exceptions (e.g. IllegalStateException, IllegalArgumentException, generic Exception) and returns an error view with a user-friendly message instead of a stack trace.

**Q26. Why is the session cookie-only?**  
We set server.servlet.session.tracking-modes=cookie so the session ID is not appended to the URL (no ;jsessionid=...). Keeps URLs clean and avoids sharing session IDs in links.

---

## 6. Frontend

**Q27. Why Thymeleaf?**  
It integrates with Spring (model attributes, form binding, validation errors) and keeps the view in HTML with simple expressions. No separate front-end build step; good for a server-rendered form-based app.

**Q28. How do you show validation errors?**  
Thymeleaf’s #fields.hasErrors('fieldName') and th:errors. We add the binding result to the model when validation fails, and the template displays error messages next to each field.

**Q29. How did you support all Indian states and UTs in the dropdown?**  
The dropdown is filled from the same location list used for training (from the CSV). We expanded the CSV to include all states and union territories with sample rows so the model and dropdown stay in sync.

**Q30. Why dark theme and inline CSS?**  
For a clear, modern look and to avoid caching issues: critical dark-theme styles are inlined so the page looks correct even if the external stylesheet is cached. Full styles are in an external CSS file.

---

## 7. Deployment & DevOps

**Q31. How is the app deployed?**  
As a Docker container on Render. The Dockerfile does a multi-stage build: Maven compiles and packages the JAR, then a JRE image runs that JAR. Render builds from the GitHub repo and runs the container, exposing a public HTTPS URL.

**Q32. How does the app get the correct port on Render?**  
Render sets the PORT environment variable. The container runs something like `java -Dserver.port=$PORT -jar app.jar`, and application.properties has server.port=${PORT:8080} and server.address=0.0.0.0 so the app listens on the right port and accepts external connections.

**Q33. Why Docker?**  
Reproducible build and run: same Java version and dependencies everywhere. Render (and other platforms) can build from the Dockerfile and run the same image in any region.

**Q34. What about the free tier “spin down”?**  
On Render’s free tier, the instance sleeps after inactivity. The first request after that can take ~50 seconds (cold start). I mention this in the README so users know what to expect.

**Q35. Where is the code?**  
GitHub: https://github.com/arjavjain310/Home-Price-Prediction-In-India (or your actual repo). The repo has the full source, Dockerfile, and deployment notes.

---

## 8. Challenges & problem-solving

**Q36. What was the hardest part?**  
Examples: Implementing matrix inversion in Java for OLS; making sure the root URL (/) and / both work and that the session doesn’t put jsessionid in the URL; or getting the app to listen on 0.0.0.0 and use PORT correctly on Render so the public link works.

**Q37. Why did you see “Not Found” on Render initially?**  
The app was binding only to localhost. Setting server.address=0.0.0.0 made it listen on all interfaces so Render’s proxy could forward traffic. Also ensured PORT was passed into the container and used by the JVM.

**Q38. Why did the new UI (dark theme, house image) not show for you at first?**  
The running app was using an old built copy of the templates from target/classes. Doing a clean compile (e.g. mvn clean compile) and restarting the app refreshed the built templates so the new UI appeared.

**Q39. How do you ensure the same locations in the dropdown and in the model?**  
Locations and their indices come from one source: the CSV and DatasetLoader. ModelTrainer exposes this list for the controller and the location index for a given name. So the dropdown and the model always use the same encoding.

---

## 9. Testing & quality

**Q40. Did you write tests?**  
The project is set up for tests (spring-boot-starter-test). I could add unit tests for LinearRegression (fit/predict), DatasetLoader (parsing), and PredictionService (prediction and formatting), and integration tests for the controller and form flow.

**Q41. How would you test the model?**  
Unit tests: create small X and y, call fit(), then predict() and assert expected values (e.g. known weights for simple data). For integration, use a fixed CSV or mock DatasetLoader and check that predicted prices are in a reasonable range.

---

## 10. Security & best practices

**Q42. Any security considerations?**  
Input is validated (type and range) so we don’t process invalid or extreme values. No sensitive data is stored; no user accounts or API keys. For production, we’d add HTTPS (Render provides it), rate limiting, and keep dependencies updated.

**Q43. Why not store predictions or user data?**  
The project is a simple estimator: submit → get price → done. No database; no need to store inputs or results for this scope. Adding history or analytics would require a DB and privacy considerations.

---

## 11. Extensions & scalability

**Q44. How would you add a REST API?**  
Add a @RestController (e.g. /api/predict) that accepts JSON (squareFeet, bhk, bathrooms, location), calls PredictionService, and returns JSON with the predicted price. Same service layer; only the controller and response format change.

**Q45. How would you scale the model?**  
Options: cache the trained model in memory (already done); for very large data use SGD or external ML services; or schedule periodic retraining and reload the model without full app restart (e.g. load from file or DB).

**Q46. Would you use a database?**  
For this project, the CSV in the JAR is enough. For production with more data, user accounts, or prediction history, I’d add a DB (e.g. PostgreSQL), store training data and optionally predictions, and possibly run training as a batch job.

---

## 12. Quick one-liners (for rapid-fire questions)

- **What is the project?** A web app that predicts home prices in ₹ using Linear Regression in Java.  
- **Tech stack?** Java 17, Spring Boot 3.2, Maven, Thymeleaf, Docker, Render.  
- **Where is the model trained?** At startup, from a CSV in the project.  
- **How is the model implemented?** OLS Linear Regression in Java using the normal equation.  
- **Train/test split?** 80/20, shuffled with fixed seed; MAE and RMSE on the test set.  
- **How is location used?** Encoded as an integer index per location; same list for dropdown and model.  
- **Deployment?** Docker image built from GitHub, run on Render; public HTTPS URL.  
- **Live link?** https://home-price-prediction-in-india.onrender.com  

---

Use this document to rehearse answers in your own words and to align with how your code actually works. Good for interviews and for explaining the project on your resume or portfolio.
