package org.someth2say.storm.index;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class IndexEntryBuilderParams<INDEX extends Enum<INDEX>> {

    private static final Pattern pattern = Pattern.compile("([^(]+)(\\((.+)\\))?");

    public final INDEX indexEntry;
    public final String params;
    public final boolean paramsPresent;

    public IndexEntryBuilderParams(final INDEX index) {
        this(index,null, false);
    }

    public IndexEntryBuilderParams(final INDEX index, final String param) {
        this(index,param,true);
    }   

    private IndexEntryBuilderParams(final INDEX index, final String param, final boolean paramsPresent) {
        Objects.requireNonNull(index);
        this.indexEntry = index;
        this.params = param;
        this.paramsPresent=paramsPresent;
    }
    
    public IndexEntryBuilderParams(final Class<INDEX> indexClass, final String buildParams) {
        Objects.requireNonNull(buildParams);
        
        Matcher matcher = pattern.matcher(buildParams);
        if (!matcher.matches()){
            throw new IllegalArgumentException("Can not parse stat: "+buildParams);
        }

        this.params = matcher.groupCount()>2?matcher.group(3):null;
        this.paramsPresent = matcher.groupCount()>2 && !"~".equals(this.params);

        String indexName = matcher.group(1);
        indexEntry = Enum.valueOf(indexClass, indexName.toUpperCase());

    }

    @Override
    public String toString() {
        return indexEntry.toString();
    }
}
