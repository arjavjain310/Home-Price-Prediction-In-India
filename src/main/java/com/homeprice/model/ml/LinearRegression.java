package com.homeprice.model.ml;

import java.util.Arrays;

/**
 * Simple Ordinary Least Squares (OLS) Linear Regression implementation in Java.
 * Fits: y = X * weights, where X includes intercept (first column of ones).
 */
public class LinearRegression {

    private double[] weights; // coefficients including intercept
    private boolean fitted;

    public LinearRegression() {
        this.fitted = false;
    }

    /**
     * Fit the model using normal equation: weights = (X'X)^(-1) X'y
     * X: design matrix (each row = one sample, first column = 1 for intercept)
     * y: target vector
     */
    public void fit(double[][] X, double[] y) {
        if (X == null || y == null || X.length != y.length || X.length == 0) {
            throw new IllegalArgumentException("X and y must be non-null, same length, and non-empty");
        }
        int n = X.length;
        int p = X[0].length;

        // X'X (p x p)
        double[][] XtX = new double[p][p];
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < p; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += X[k][i] * X[k][j];
                }
                XtX[i][j] = sum;
            }
        }

        // X'y (p x 1)
        double[] Xty = new double[p];
        for (int i = 0; i < p; i++) {
            double sum = 0;
            for (int k = 0; k < n; k++) {
                sum += X[k][i] * y[k];
            }
            Xty[i] = sum;
        }

        // Solve (X'X) weights = X'y via matrix inverse
        double[][] XtXInv = invert(XtX);
        weights = new double[p];
        for (int i = 0; i < p; i++) {
            double sum = 0;
            for (int j = 0; j < p; j++) {
                sum += XtXInv[i][j] * Xty[j];
            }
            weights[i] = sum;
        }
        fitted = true;
    }

    /**
     * Predict for a single feature vector (including intercept 1.0 as first element).
     */
    public double predict(double[] features) {
        if (!fitted || weights == null || features.length != weights.length) {
            throw new IllegalStateException("Model not fitted or feature dimension mismatch");
        }
        double sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * features[i];
        }
        return sum;
    }

    /**
     * Predict for multiple samples.
     */
    public double[] predict(double[][] X) {
        double[] result = new double[X.length];
        for (int i = 0; i < X.length; i++) {
            result[i] = predict(X[i]);
        }
        return result;
    }

    public double[] getWeights() {
        return weights == null ? null : Arrays.copyOf(weights, weights.length);
    }

    public boolean isFitted() {
        return fitted;
    }

    /**
     * Invert a square matrix using Gauss-Jordan elimination.
     */
    private static double[][] invert(double[][] A) {
        int n = A.length;
        double[][] a = new double[n][n * 2];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, a[i], 0, n);
            a[i][n + i] = 1.0;
        }
        for (int col = 0; col < n; col++) {
            int pivot = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(a[row][col]) > Math.abs(a[pivot][col])) {
                    pivot = row;
                }
            }
            double[] tmp = a[col];
            a[col] = a[pivot];
            a[pivot] = tmp;
            double div = a[col][col];
            if (Math.abs(div) < 1e-10) {
                throw new IllegalArgumentException("Matrix is singular, cannot invert");
            }
            for (int j = 0; j < n * 2; j++) {
                a[col][j] /= div;
            }
            for (int i = 0; i < n; i++) {
                if (i != col) {
                    double factor = a[i][col];
                    for (int j = 0; j < n * 2; j++) {
                        a[i][j] -= factor * a[col][j];
                    }
                }
            }
        }
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], n, inv[i], 0, n);
        }
        return inv;
    }
}
