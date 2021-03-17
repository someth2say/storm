package org.someth2say.storm.category;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategorizerBuilderParams { 
    private static final Pattern pattern = Pattern.compile("([^(]+)(\\((.+)\\))?");

    public CategorizerBuilder categorizerBuilder;
    public String params;

    public CategorizerBuilderParams(){ }

    public CategorizerBuilderParams(final CategorizerBuilder categorizerBuilder, final String params){ 
        Objects.requireNonNull(categorizerBuilder);
        this.categorizerBuilder=categorizerBuilder;
        this.params=params;
    }

    public CategorizerBuilderParams(final String buildParams){
        Matcher matcher = pattern.matcher(buildParams);
        if (!matcher.matches()){
            throw new IllegalArgumentException("Can not parse categorizer: "+buildParams);
        }

        this.params = matcher.groupCount()>2?matcher.group(3):null;
        this.categorizerBuilder = CategorizerBuilder.valueOf(matcher.group(1).toUpperCase());
    }
}