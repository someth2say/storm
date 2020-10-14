package org.somet2say.flare.category;

import java.util.Optional;

import org.somet2say.flare.ResponseData;

public class HttpCodeCategorizer implements Categorizer {

    @Override
    public Optional<String> getCategoryFor(final ResponseData<String> responseData) {
        return Optional.of(Integer.toString(responseData.response.statusCode()));
    }
    
}
