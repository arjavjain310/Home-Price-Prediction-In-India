package com.homeprice.service;

import com.homeprice.model.domain.PropertyRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Loads and parses the CSV dataset and maps location names to indices.
 */
@Component
public class DatasetLoader {

    private static final String CSV_PATH = "data/dataset.csv";
    private final List<String> locationOrder = new ArrayList<>();

    /**
     * Load all records from CSV. First column header row defines locations encountered.
     * Location column is encoded by order of first appearance (or use predefined order).
     */
    public List<PropertyRecord> loadRecords() throws IOException {
        List<String> locations = new ArrayList<>();
        Map<String, Integer> locationToIndex = new HashMap<>();
        List<PropertyRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(CSV_PATH).getInputStream(), StandardCharsets.UTF_8))) {
            String header = reader.readLine();
            if (header == null) {
                return records;
            }
            String[] cols = parseCsvLine(header);
            int locIdx = -1;
            int sqIdx = -1, bhkIdx = -1, bathIdx = -1, priceIdx = -1;
            for (int i = 0; i < cols.length; i++) {
                String c = cols[i].trim().toLowerCase();
                if (c.equals("location")) locIdx = i;
                else if (c.equals("square_feet")) sqIdx = i;
                else if (c.equals("bhk")) bhkIdx = i;
                else if (c.equals("bathrooms")) bathIdx = i;
                else if (c.equals("price_inr")) priceIdx = i;
            }
            if (locIdx < 0 || sqIdx < 0 || bhkIdx < 0 || bathIdx < 0 || priceIdx < 0) {
                throw new IOException("CSV must contain columns: square_feet, bhk, bathrooms, location, price_inr");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = parseCsvLine(line);
                if (parts.length <= Math.max(Math.max(locIdx, sqIdx), Math.max(bhkIdx, Math.max(bathIdx, priceIdx)))) {
                    continue;
                }
                double sq = Double.parseDouble(parts[sqIdx].trim());
                int bhk = Integer.parseInt(parts[bhkIdx].trim());
                int bath = Integer.parseInt(parts[bathIdx].trim());
                String loc = parts[locIdx].trim();
                double price = Double.parseDouble(parts[priceIdx].trim());
                int locIndex = locationToIndex.computeIfAbsent(loc, k -> {
                    locations.add(loc);
                    return locations.size() - 1;
                });
                records.add(new PropertyRecord(sq, bhk, bath, locIndex, price));
            }
            locationOrder.clear();
            locationOrder.addAll(locations);
        }
        return records;
    }

    /**
     * Return ordered list of location names (same order as encoding).
     */
    public List<String> getLocationOrder() {
        return new ArrayList<>(locationOrder);
    }

    private static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if ((c == ',' && !inQuotes)) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        result.add(current.toString());
        return result.toArray(new String[0]);
    }
}
