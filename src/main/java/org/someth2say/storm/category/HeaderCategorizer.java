package org.someth2say.storm.category;

import java.util.Optional;

import org.someth2say.storm.ResponseData;

public class HeaderCategorizer implements Categorizer {

    private final String headerKey;

    @Override
    public Optional<String> getCategoryFor(ResponseData<String> responseData) {
        if (responseData.response!=null)
           return responseData.response.headers().firstValue(headerKey);
        else
            return Optional.empty();
    }

    public HeaderCategorizer(String headerKey) {
        this.headerKey = headerKey;
    }

    @Override
    public String toString() {
        return headerKey;
    }
}
