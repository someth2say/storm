package org.someth2say.storm;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.someth2say.storm.category.Category;
import org.someth2say.storm.configuration.StormConfiguration;

import io.quarkus.runtime.QuarkusApplication;
@Singleton
public class StormQuarkusApplication implements QuarkusApplication {

    @Inject
    StormConfiguration configuration;

    @Override
    public int run(final String... args) throws Exception {

        Category rootCategory = main();
        if (rootCategory == null) {
            return -1;
        }
        System.out.println(rootCategory);

        return 0;
    }

    public Category main() throws Exception {
        return Storm.main(configuration);
    }
}
