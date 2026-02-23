package com.homeprice.model.domain;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Domain model for user property input used for price prediction.
 */
public class PropertyInput {

    @NotNull(message = "Square feet is required")
    @DecimalMin(value = "100", message = "Square feet must be at least 100")
    @DecimalMax(value = "10000", message = "Square feet must not exceed 10,000")
    private BigDecimal squareFeet;

    @NotNull(message = "Number of bedrooms (BHK) is required")
    @Min(value = 1, message = "BHK must be at least 1")
    @Max(value = 10, message = "BHK must not exceed 10")
    private Integer bhk;

    @NotNull(message = "Number of bathrooms is required")
    @Min(value = 1, message = "Bathrooms must be at least 1")
    @Max(value = 10, message = "Bathrooms must not exceed 10")
    private Integer bathrooms;

    @NotBlank(message = "Location is required")
    private String location;

    public PropertyInput() {
    }

    public PropertyInput(BigDecimal squareFeet, Integer bhk, Integer bathrooms, String location) {
        this.squareFeet = squareFeet;
        this.bhk = bhk;
        this.bathrooms = bathrooms;
        this.location = location;
    }

    public BigDecimal getSquareFeet() {
        return squareFeet;
    }

    public void setSquareFeet(BigDecimal squareFeet) {
        this.squareFeet = squareFeet;
    }

    public Integer getBhk() {
        return bhk;
    }

    public void setBhk(Integer bhk) {
        this.bhk = bhk;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location != null ? location.trim() : null;
    }
}
