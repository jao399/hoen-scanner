package com.skyscanner;

import com.fasterxml.jackson.core.type.TypeReference;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HoenScannerApplication extends Application<HoenScannerConfiguration> {
    private static final TypeReference<List<SearchResult>> SEARCH_RESULTS_TYPE = new TypeReference<>() { };

    public static void main(final String[] args) throws Exception {
        new HoenScannerApplication().run(args);
    }

    @Override
    public String getName() {
        return "hoen-scanner";
    }

    @Override
    public void initialize(final Bootstrap<HoenScannerConfiguration> bootstrap) {

    }

    @Override
    public void run(final HoenScannerConfiguration configuration, final Environment environment) throws IOException {
        final List<SearchResult> searchResults = new ArrayList<>();
        searchResults.addAll(loadSearchResults(environment, "rental_cars.json", "rental_car"));
        searchResults.addAll(loadSearchResults(environment, "hotels.json", "hotel"));
        environment.jersey().register(new SearchResource(searchResults));
    }

    private List<SearchResult> loadSearchResults(
            final Environment environment,
            final String resourceName,
            final String kind
    ) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IOException("Missing resource: " + resourceName);
            }

            final List<SearchResult> results =
                    environment.getObjectMapper().readValue(inputStream, SEARCH_RESULTS_TYPE);
            results.forEach(result -> result.setKind(kind));
            return results;
        }
    }
}
