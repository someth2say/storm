package org.someth2say.storm.category;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.someth2say.storm.ResponseData;

public class RegExpMatchCategorizer implements Categorizer {

    private final Pattern pattern;

    public RegExpMatchCategorizer(final String regex) {
        this.pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    }

    @Override
    public Optional<String> getCategoryFor(final ResponseData<String> responseData) {
        if (responseData.response != null) {
            Matcher matcher = pattern.matcher(responseData.response.body());
            if (matcher.matches()) {
                return Optional.of("true");
            } else
                return Optional.of("false");
        } else {
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return pattern.toString();
    }

}
