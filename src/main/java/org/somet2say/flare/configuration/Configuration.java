package org.somet2say.flare.configuration;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.arc.config.ConfigProperties;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@ConfigProperties(prefix = "flare")
public interface Configuration {

    @Option(names = { "-t", "--threads" }, description = "How many worker threads use")
    public int getThreads();

    @Option(names = { "-r", "--repeat" }, description = "How many times execute the request")
    public int getRepeat();

    @Option(names = { "-o", "--order" }, description = "Strategy for picking the next URL from the list")
    @ConfigProperty(defaultValue = "random")
    public Order getOrder();

    @Parameters(index = "0", description = "Target URI for the requests")
    public List<URI> getUrls();

    @Option(names = { "-c", "--categorizers" }, description = "Categorizers to use to split response data.")
    @ConfigProperty(defaultValue = "")
    public List<String> getCategorizers();

    @Option(names = { "-s", "--stats" }, description = "Stats generated for each category bucket.")
    @ConfigProperty(defaultValue = "")
    public List<String> getStats();

}
