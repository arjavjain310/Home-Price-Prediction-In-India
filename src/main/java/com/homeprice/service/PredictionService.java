package com.homeprice.service;

import com.homeprice.model.domain.PropertyInput;
import com.homeprice.model.ml.LinearRegression;
import org.springframework.stereotype.Service;

/**
 * Service to predict home price from user input using the trained model.
 */
@Service
public class PredictionService {

    private final ModelTrainer modelTrainer;

    public PredictionService(ModelTrainer modelTrainer) {
        this.modelTrainer = modelTrainer;
    }

    /**
     * Predict price in INR for the given property input.
     * Returns a non-negative value rounded to nearest rupee.
     */
    public long predictPriceInr(PropertyInput input) {
        LinearRegression model = modelTrainer.getModel();
        int locationIndex = modelTrainer.getLocationIndex(input.getLocation());
        double sq = input.getSquareFeet().doubleValue();
        int bhk = input.getBhk();
        int bath = input.getBathrooms();
        double[] features = new double[]{1.0, sq, bhk, bath, locationIndex};
        double predicted = model.predict(features);
        long price = Math.round(Math.max(0, predicted));
        return price;
    }

    /**
     * Format price for display in Indian Rupees (e.g. 1,00,00,000).
     */
    public String formatPriceInr(long priceInr) {
        if (priceInr < 0) priceInr = 0;
        String s = String.valueOf(priceInr);
        int len = s.length();
        if (len <= 3) return "₹ " + s;
        StringBuilder sb = new StringBuilder();
        int firstGroup = len % 2 == 0 ? 2 : 1;
        sb.append(s, 0, firstGroup);
        for (int i = firstGroup; i < len; i += 2) {
            if (i > 0) sb.append(",");
            sb.append(s, i, Math.min(i + 2, len));
        }
        return "₹ " + sb.toString();
    }
}
