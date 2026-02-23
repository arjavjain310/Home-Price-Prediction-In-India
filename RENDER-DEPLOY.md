# Deploy on Render

This repo is ready for Render with **Docker**.

## Steps

1. Go to [Render](https://render.com) and sign in.
2. Click **New +** → **Web Service**.
3. Connect **GitHub** and select the repo: **arjavjain310/Home-Price-Prediction-In-India**.
4. Configure:
   - **Name:** `home-price-prediction` (or any name).
   - **Region:** Choose one (e.g. Oregon).
   - **Runtime:** **Docker**.
   - **Dockerfile Path:** `./Dockerfile` (default).
5. Click **Create Web Service**.
6. Wait for the build to finish. Render will give you a URL like:
   **https://home-price-prediction-xxxx.onrender.com**

That URL is your public link. Anyone can use it to get home price estimates in ₹.

## Optional: Blueprint

In Render Dashboard → **Blueprint** → **New Blueprint Instance** → connect this repo.  
Render will use `render.yaml` and create the Docker web service automatically.
