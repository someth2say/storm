package org.someth2say.storm.category;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum CategorizerBuilder {
    BODY(BodyCategorizer::new), 
    REGEXPMATCH(RegExpMatchCategorizer::new), 
    HTTPCODE(HttpCodeCategorizer::new),
    HEADER(HeaderCategorizer::new),
    URL(URLCategorizer::new),
    DURATIONHISTOGRAM(DurationHistogramCategorizer::new),
    TIMEHISTOGRAM(TimeHistogramCategorizer::new);

    public static class CategorizerBuilderParams { 
        private static final Pattern pattern = Pattern.compile("([^(]+)(\\((.+)\\))?");

        public CategorizerBuilder categorizerBuilder;
        public String params;

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

    private Supplier<Categorizer> supplier;
    private Function<String, Categorizer> function;

    private CategorizerBuilder(Supplier<Categorizer> supplier) {
        this.supplier = supplier;
    }

    private CategorizerBuilder(Function<String, Categorizer> function) {
        this.function = function;
    }

    public static Categorizer build(final CategorizerBuilderParams cbp) {
        return cbp.categorizerBuilder.function != null ? cbp.categorizerBuilder.function.apply(cbp.params) : cbp.categorizerBuilder.supplier.get();
    }

    public static List<Categorizer> buildAll(final List<CategorizerBuilderParams> buildParams){
        return buildParams.stream()
            .map(CategorizerBuilder::build)
            .collect(Collectors.toList());
    }

/*     public static Categorizer buildCategorizer(final String buildParams){
        //1.- Separate categorizer name from config.
        Matcher matcher = pattern.matcher(buildParams);
        if (!matcher.matches()){
            //TODO
            System.out.println("Can not parse categorizer: "+buildParams);
           return null;
        }
        String categorizerName = matcher.group(1);
		String categorizerParams = matcher.groupCount()>2?matcher.group(3):null;

        //2.- Create categorizer
		CategorizerBuilder builder = CategorizerBuilder.valueOf(categorizerName.toUpperCase());
        return builder.build(categorizerParams);
    } */
}
