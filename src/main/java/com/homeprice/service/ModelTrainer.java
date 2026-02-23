package com.homeprice.service;

import com.homeprice.model.domain.PropertyRecord;
import com.homeprice.model.ml.LinearRegression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Loads dataset, splits into train/test, trains Linear Regression, and logs MAE/RMSE.
 */
@Service
public class ModelTrainer {

    private static final Logger log = LoggerFactory.getLogger(ModelTrainer.class);
    private static final double TRAIN_RATIO = 0.8;
    private static final long RANDOM_SEED = 42L;

    private final DatasetLoader datasetLoader;
    private final AtomicReference<LinearRegression> modelRef = new AtomicReference<>();
    private final AtomicReference<List<String>> locationOrderRef = new AtomicReference<>();

    public ModelTrainer(DatasetLoader datasetLoader) {
        this.datasetLoader = datasetLoader;
    }

    @PostConstruct
    public void trainModel() {
        try {
            List<PropertyRecord> records = datasetLoader.loadRecords();
            List<String> locations = datasetLoader.getLocationOrder();
            locationOrderRef.set(locations);

            if (records.size() < 5) {
                log.warn("Dataset too small ({} records). Model may not be reliable.", records.size());
            }

            List<PropertyRecord> shuffled = new ArrayList<>(records);
            Collections.shuffle(shuffled, new java.util.Random(RANDOM_SEED));
            int split = (int) (shuffled.size() * TRAIN_RATIO);
            List<PropertyRecord> trainList = shuffled.subList(0, split);
            List<PropertyRecord> testList = shuffled.subList(split, shuffled.size());

            double[][] XTrain = new double[trainList.size()][5];
            double[] yTrain = new double[trainList.size()];
            for (int i = 0; i < trainList.size(); i++) {
                PropertyRecord r = trainList.get(i);
                XTrain[i] = r.getFeatures();
                yTrain[i] = r.getPriceInr();
            }

            LinearRegression model = new LinearRegression();
            model.fit(XTrain, yTrain);
            modelRef.set(model);

            double[][] XTest = new double[testList.size()][5];
            double[] yTest = new double[testList.size()];
            for (int i = 0; i < testList.size(); i++) {
                PropertyRecord r = testList.get(i);
                XTest[i] = r.getFeatures();
                yTest[i] = r.getPriceInr();
            }
            double[] predictions = model.predict(XTest);
            double mae = computeMAE(yTest, predictions);
            double rmse = computeRMSE(yTest, predictions);
            log.info("=== Model evaluation (test set) ===");
            log.info("Test samples: {}", testList.size());
            log.info("MAE (Mean Absolute Error): ₹ {} ", String.format("%.2f", mae));
            log.info("RMSE (Root Mean Squared Error): ₹ {}", String.format("%.2f", rmse));
        } catch (IOException e) {
            log.error("Failed to load dataset or train model", e);
        }
    }

    public LinearRegression getModel() {
        LinearRegression m = modelRef.get();
        if (m == null) {
            throw new IllegalStateException("Model not yet trained or failed to load dataset");
        }
        return m;
    }

    public List<String> getLocationOrder() {
        List<String> locs = locationOrderRef.get();
        return locs != null ? new ArrayList<>(locs) : List.of();
    }

    public int getLocationIndex(String location) {
        List<String> order = locationOrderRef.get();
        if (order == null) return 0;
        int idx = order.indexOf(location);
        return idx >= 0 ? idx : 0;
    }

    private static double computeMAE(double[] actual, double[] predicted) {
        double sum = 0;
        for (int i = 0; i < actual.length; i++) {
            sum += Math.abs(actual[i] - predicted[i]);
        }
        return sum / actual.length;
    }

    private static double computeRMSE(double[] actual, double[] predicted) {
        double sum = 0;
        for (int i = 0; i < actual.length; i++) {
            double diff = actual[i] - predicted[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum / actual.length);
    }
}
