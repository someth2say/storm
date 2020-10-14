package org.somet2say.flare.category;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.somet2say.flare.ResponseData;

public class RegExpMatchCategorizer implements Categorizer {

    private final Pattern pattern;

    public RegExpMatchCategorizer(final String regex) {
        this.pattern = Pattern.compile(regex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    }

    @Override
    public Optional<String> getCategoryFor(final ResponseData<String> responseData) {
        Matcher matcher = pattern.matcher(responseData.response.body());
        if (matcher.matches()){
            return Optional.of(matcher.group(1));
        } 
        else return Optional.of("!"+pattern.toString());
    }
    
}
