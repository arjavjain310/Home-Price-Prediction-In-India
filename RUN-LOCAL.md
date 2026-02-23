# Run on your computer (any port)

Use this when you want to **modify the app and test** on your machine.

---

## 1. Pick a port

Examples: `3000`, `8080`, `5000`, `9090`. Default below is **8080**.

---

## 2. Run the app

**Option A – Maven (good for editing and rerunning)**

```bash
cd "/Users/arjavjain/Home Price Prediction"
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=3000"
```

Replace `3000` with any port you want.

**Option B – Script (Unix/Mac)**

```bash
chmod +x run-local.sh
./run-local.sh 3000
```

No argument = port **8080**.

**Option C – Environment variable**

```bash
PORT=3000 mvn spring-boot:run
```

**Option D – JAR (after building once)**

```bash
mvn clean package -DskipTests
java -jar target/home-price-prediction-1.0.0.jar --server.port=3000
```

---

## 3. Open in browser

Use the link for the port you chose:

| Port | Link |
|------|------|
| 3000 | **http://localhost:3000** |
| 8080 | **http://localhost:8080** |
| 5000 | **http://localhost:5000** |
| 9090 | **http://localhost:9090** |

---

## 4. Modify and check

1. Edit code (Java, HTML, CSS, `dataset.csv`, etc.).
2. Stop the app (Ctrl+C in the terminal).
3. Start again with the same command (e.g. `./run-local.sh 3000` or the `mvn` command above).
4. Reload the page in the browser to see changes.

For instant reload of Java changes during development, the project includes `spring-boot-devtools`; after recompiling, the app may restart automatically.

---

## Quick reference

- **Run on port 3000:**  
  `mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=3000"`
- **Link:**  
  **http://localhost:3000**
