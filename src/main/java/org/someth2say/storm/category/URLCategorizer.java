package org.someth2say.storm.category;

import java.util.Optional;

import org.someth2say.storm.ResponseData;

public class URLCategorizer implements Categorizer {

    @Override
    public Optional<String> getCategoryFor(ResponseData<String> responseData) {
        return Optional.of(responseData.request.uri().toString());
    }

    @Override
    public String toString() {
        return "url";
    }

}
