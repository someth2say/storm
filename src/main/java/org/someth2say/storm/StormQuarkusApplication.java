package org.someth2say.storm;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.someth2say.storm.category.Category;
import org.someth2say.storm.configuration.Configuration;
import org.someth2say.storm.utils.SerializationUtils;

import io.quarkus.runtime.QuarkusApplication;

@Singleton
public class StormQuarkusApplication implements QuarkusApplication {

    public static final String RESET = "\033[0m"; // Text Reset

    @Inject
    Configuration configuration;

    @Override
    public int run(final String... args) throws Exception {

        Category rootCategory = Storm.main(configuration);
        if (rootCategory == null) {
            return -1;
        }
        System.out.println(rootCategory);

        return 0;
    }

}