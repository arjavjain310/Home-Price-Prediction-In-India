# Deployment Instructions – Home Price Prediction

This document describes how to run the app locally (with Docker or Maven) and deploy it on **Render** with **Docker** so anyone with a public link can access it.

---

## Prerequisites

- **Java 17** (for Maven run) or **Docker** (for Docker run)
- **Maven 3.6+** (only if not using Docker)
- **Git** + GitHub (for deploying on Render)

---

## Run with Docker (same as Render)

1. **From the project root:**
   ```bash
   docker build -t home-price-prediction .
   docker run --rm -p 8080:8080 -e PORT=8080 home-price-prediction
   ```
   Or use the script:
   ```bash
   chmod +x run-docker.sh
   ./run-docker.sh
   ```

2. **Open:** http://localhost:8080 — use the form and click **Predict Price** to see the estimated price in ₹.

---

## Run on localhost (without Docker)

1. **Build the project**
   ```bash
   mvn clean package
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   Or run the JAR:
   ```bash
   java -jar target/home-price-prediction-1.0.0.jar
   ```

3. **Open in browser:** http://localhost:8080

4. **Console:** On startup, the app trains the model and prints **MAE** and **RMSE** on the test set.

---

## Deploy on Render with Docker

1. **Push the project to GitHub**
   ```bash
   cd "/Users/arjavjain/Home Price Prediction"
   git init
   git add .
   git commit -m "Home Price Prediction app"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
   git push -u origin main
   ```

2. **Create the service on Render**
   - Go to [Render](https://render.com) and sign in.
   - Click **New +** → **Web Service**.
   - Connect your **GitHub** account if needed, then select the repository that contains this project.
   - Click **Connect**.

3. **Configure for Docker**
   - **Name:** `home-price-prediction` (or any name).
   - **Region:** Choose one (e.g. Oregon).
   - **Runtime:** **Docker**.
   - **Dockerfile Path:** `./Dockerfile` (default; leave as is if the Dockerfile is in the repo root).
   - **Instance Type:** Free or paid.

4. **Environment (optional)**
   - Add variable: `JAVA_OPTS` = `-Xmx512m -Xms256m`.
   - Do **not** set `PORT` — Render sets it automatically.

5. **Deploy**
   - Click **Create Web Service**. Render will build the image from the Dockerfile and start the container.

6. **Public link**
   - When the deploy finishes, Render shows a URL like `https://home-price-prediction-xxxx.onrender.com`. Use this link to access the app; anyone with the link can open it and use **Predict Price**.

### Using Blueprint (render.yaml)

- In Render Dashboard: **Blueprint** → **New Blueprint Instance** → connect the repo.
- Render will create a web service from `render.yaml` (Docker runtime, `./Dockerfile`). No need to set Runtime or Dockerfile path manually.

---

## Deploy on Railway

1. Push this project to **GitHub**.

2. Go to [Railway](https://railway.app) and sign in. Create a **New Project**.

3. **Add a service:** Click **Add Service** → **GitHub Repo** and select this repository.

4. Railway will detect the app. Configure:
   - **Build:** Railway usually detects Maven/Java. If not, set **Build Command:** `mvn clean package -DskipTests`
   - **Start Command:** Either leave default (Railway often runs `java -jar target/*.jar`) or set:
     ```bash
     java -Dserver.port=$PORT -jar target/home-price-prediction-1.0.0.jar
     ```
   - Or use the **Procfile** in the repo: `web: java -Dserver.port=$PORT $JAVA_OPTS -jar target/home-price-prediction-1.0.0.jar`  
     (Railway sets `PORT` automatically.)

5. **Variables (optional):**
   - `PORT` is set by Railway.
   - You can set `JAVA_OPTS=-Xmx512m` if needed.

6. Under **Settings** → **Networking**, click **Generate Domain**. Railway will assign a public URL (e.g. `https://xxx.up.railway.app`).

7. Redeploy if needed. Anyone with the generated link can access the app and use **Predict Price**.

### Using Docker on Railway

If you prefer Docker on Railway:

1. Add a service from the same GitHub repo.
2. In **Settings**, set **Dockerfile Path** to `./Dockerfile` (or root).
3. Railway will build and run the container and set `PORT` automatically. Generate a public domain as above.

---

## Summary

| Step | Local | Render | Railway |
|------|--------|--------|---------|
| Build | `mvn clean package` | Automatic (Docker or Maven) | Automatic (Maven or Docker) |
| Run | `mvn spring-boot:run` or `java -jar target/...jar` | Container or Maven + Procfile | Same |
| Port | 8080 | `PORT` from Render | `PORT` from Railway |
| Public link | http://localhost:8080 | Given after deploy | Generated domain |

The app uses **Spring Boot**, reads **PORT** from the environment (default 8080), and serves a single web UI where users submit property details and see the estimated price in **Indian Rupees (₹)**. No Python is used; the model is trained in Java from the bundled CSV dataset.
