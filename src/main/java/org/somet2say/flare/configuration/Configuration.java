package org.somet2say.flare.configuration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.somet2say.flare.stats.Stats;

import io.quarkus.arc.config.ConfigProperties;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@ConfigProperties(prefix = "storm")
@CommandLine.Command(mixinStandardHelpOptions = true, version="TO BE IMPORTED FROM POM")
@ApplicationScoped
public class Configuration  { 

    @Option(names = { "-t", "--threads" }, description = "How many worker threads use")
    public int threads=10;

    @Option(names = { "-r", "--repeat" }, description = "How many times execute the request")
    public int repeat=10;

    @Option(names = { "-o", "--order" }, description = "Strategy for picking the next URL from the list.")
    public Order order = Order.random;

    @Parameters(index = "0..*", description = "Target URIs for the requests")
    public List<URI> urls;

    @Option(names = { "-c", "--categorizers" }, description = "Categorizers to use to split response data.")
    public List<String> categorizers = Collections.emptyList();

    @Option(names = { "-s", "--stats" }, description = "Stats generated for each category bucket.")
    public List<String> stats = List.of(Stats.COUNT.name());

}
