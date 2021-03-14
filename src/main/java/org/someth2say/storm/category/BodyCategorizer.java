package org.someth2say.storm.category;

import java.util.Optional;

import org.someth2say.storm.ResponseData;

public class BodyCategorizer implements Categorizer {

    @Override
    public String toString() {
        return "body";
    }
    @Override
    public Optional<String> getCategoryKeyFor(ResponseData responseData) {
        if (responseData.response != null) {
            return Optional.of(responseData.response.body());
        } else {
            return Optional.empty();
        }
    }

}
