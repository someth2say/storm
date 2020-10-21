package org.somet2say.flare.category;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum Categorizers {
    BODY(BodyCategorizer::new), 
    REGEXPMATCH(RegExpMatchCategorizer::new), 
    HTTPCODE(HttpCodeCategorizer::new),
    HEADER(HeaderCategorizer::new),
    URL(URLCategorizer::new),
    DURATIONHISTOGRAM(DurationHistogramCategorizer::new),
    TIMEHISTOGRAM(TimeHistogramCategorizer::new);

    private Supplier<Categorizer> supplier;
    private Function<String, Categorizer> function;
    private static final Pattern pattern = Pattern.compile("([^(]+)(\\((.+)\\))?");

    public Categorizer getInstance(String args) {
        return function != null ? function.apply(args) : supplier.get();
    }

    private Categorizers(Supplier<Categorizer> supplier) {
        this.supplier = supplier;
    }

    private Categorizers(Function<String, Categorizer> function) {
        this.function = function;

    }

    public static List<Categorizer> buildCategorizers(List<String> categorizers){
        return categorizers.stream()
            .map(Categorizers::buildCategorizer)
            .collect(Collectors.toList());
    }

    public static Categorizer buildCategorizer(final String categorizer){
        //1.- Separate categorizer name from config.
        Matcher matcher = pattern.matcher(categorizer);
        if (!matcher.matches()){
            //TODO
            System.out.println("Can not parse categorizer: "+categorizer);
           return null;
        }
        String categorizerName = matcher.group(1);
		String categorizerParams = matcher.groupCount()>2?matcher.group(3):null;

        //2.- Create categorizer
		return Categorizers.valueOf(categorizerName.toUpperCase()).getInstance(categorizerParams);
    }
}
