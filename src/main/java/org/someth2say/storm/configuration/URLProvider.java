package org.someth2say.storm.configuration;

import java.net.URI;
import java.util.List;

@FunctionalInterface
public interface URLProvider {
	 URI getNextURI(final List<URI> urls, double repeat, int count) ;
}
