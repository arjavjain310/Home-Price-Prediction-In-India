# See the new dark theme and house image

The app was using an **old built copy** of the page. The build has been refreshed. Do this:

## 1. Stop all instances
If the app is running on **any port** (8080, 3000, 5000, etc.), stop **every** instance: press **Ctrl+C** in each terminal where it’s running. Only the instance you start in step 2 will have the new UI.

## 2. Start the app on the port you want
From the project folder, pick **one** port and run:

**Port 8080 (default):**
```bash
cd "/Users/arjavjain/Home Price Prediction"
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"
```

**Or another port (e.g. 3000 or 5000):**
```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3000"
```

**Or use the script:**
```bash
./run-local.sh          # uses 8080
./run-local.sh 3000     # uses 3000
./run-local.sh 5000     # uses 5000
```

Wait until you see: `Started HomePricePredictionApplication`

## 3. Open in browser
Use the **same port** you started in step 2:

| Port  | URL                     |
|-------|-------------------------|
| 8080  | http://localhost:8080   |
| 3000  | http://localhost:3000   |
| 5000  | http://localhost:5000   |

Do a **hard refresh** (Cmd+Shift+R on Mac, Ctrl+Shift+R on Windows).

You should see: dark background, house image above the title, amber “Predict Price” button, and “Location (States & Union Territories of India)” with many options.
