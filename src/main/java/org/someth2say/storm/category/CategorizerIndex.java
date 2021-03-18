package org.someth2say.storm.category;

import org.someth2say.storm.index.IndexEntryBuilder;
import org.someth2say.storm.index.IndexEntryBuilderParams;

public enum CategorizerIndex {
    BODY(BodyCategorizer.class), 
    REGEXPMATCH(RegExpMatchCategorizer.class), 
    HTTPCODE(HttpCodeCategorizer.class),
    HEADER(HeaderCategorizer.class),
    URL(URLCategorizer.class),
    DURATIONHISTOGRAM( DurationHistogramCategorizer.class),
    TIMEHISTOGRAM(TimeHistogramCategorizer.class);

    private final Class<? extends Categorizer> targetClass;

    private CategorizerIndex(Class<? extends Categorizer> targetClass) {
        this.targetClass = targetClass;
    }

    public static Categorizer build(final CategorizerBuilderParams cbp)  {
        if (cbp.paramsPresent)
            return IndexEntryBuilder.build(cbp.indexEntry.targetClass, cbp.params);
        return IndexEntryBuilder.build(cbp.indexEntry.targetClass);
    } 

    public static class CategorizerBuilderParams extends IndexEntryBuilderParams<CategorizerIndex> {

        public CategorizerBuilderParams(final CategorizerIndex index) {
            super(index);
        }
    
        public CategorizerBuilderParams(final CategorizerIndex index, final String params) {
            super(index, params);
        }
    
        public CategorizerBuilderParams(final String params) {
            super(CategorizerIndex.class, params);
        }
    }
    
}
