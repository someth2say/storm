package org.someth2say.storm.category;

import java.util.Optional;

import org.someth2say.storm.ResponseData;

public class HttpCodeCategorizer implements Categorizer {

    @Override
    public Optional<String> getCategoryKeyFor(final ResponseData responseData) {
        if (responseData.response != null)
            return Optional.of(Integer.toString(responseData.response.statusCode()));
        else
            return Optional.empty();
    }


    @Override
    public String toString() {
        return "httpCode";
    }

}
