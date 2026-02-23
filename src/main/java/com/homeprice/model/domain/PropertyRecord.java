package com.homeprice.model.domain;

/**
 * Represents a single row from the CSV dataset (internal use for training).
 */
public class PropertyRecord {

    private final double squareFeet;
    private final int bhk;
    private final int bathrooms;
    private final int locationIndex; // encoded location
    private final double priceInr;

    public PropertyRecord(double squareFeet, int bhk, int bathrooms, int locationIndex, double priceInr) {
        this.squareFeet = squareFeet;
        this.bhk = bhk;
        this.bathrooms = bathrooms;
        this.locationIndex = locationIndex;
        this.priceInr = priceInr;
    }

    public double getSquareFeet() {
        return squareFeet;
    }

    public int getBhk() {
        return bhk;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public int getLocationIndex() {
        return locationIndex;
    }

    public double getPriceInr() {
        return priceInr;
    }

    /** Feature vector: [1, squareFeet, bhk, bathrooms, locationIndex] for regression */
    public double[] getFeatures() {
        return new double[]{1.0, squareFeet, bhk, bathrooms, locationIndex};
    }
}
