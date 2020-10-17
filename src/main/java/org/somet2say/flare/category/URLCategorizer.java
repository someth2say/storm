package org.somet2say.flare.category;

import java.util.Optional;

import org.somet2say.flare.ResponseData;

public class URLCategorizer implements Categorizer {

    @Override
    public Optional<String> getCategoryFor(ResponseData<String> responseData) {
        if (responseData.response != null)
            return Optional.of(responseData.response.uri().toString());
        else
            return Optional.empty();
    }

    @Override
    public String toString() {
        return "url";
    }

}
