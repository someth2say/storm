package org.somet2say.flare.configuration;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.somet2say.flare.stats.Stats;

import io.quarkus.arc.config.ConfigProperties;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@ConfigProperties(prefix = "storm")
@CommandLine.Command(mixinStandardHelpOptions = true, version = "TO BE IMPORTED FROM POM")
public class Configuration {

    @Positive
    @Option(names = { "-t", "--threads" }, description = "How many worker threads use")
    public int threads = 10;

    @Positive
    @Option(names = { "-r", "--repeat" }, description = "How many times execute the request")
    public int repeat = 10;

    @Option(names = { "-o", "--order" }, description = "Strategy for picking the next URL from the list.")
    public Order order = Order.random;

    // @NotEmpty 
    // TODO: Does not work, as validation occurs before enriching with command line.
    @Parameters(index = "0..*", arity = "0..*", description = "Target URIs for the requests")
    public List<URI> urls;

    @Option(names = { "-c", "--categorizers" }, description = "Categorizers to use to split response data.")
    public List<String> categorizers = Collections.emptyList();

    @Option(names = { "-s", "--stats" }, description = "Stats generated for each category bucket.")
    public List<String> stats = List.of(Stats.COUNT.name());

    @Min(0)
    @Option(names = { "-d", "--delay" }, description = "Delay time after a response")
    public int delay = 0;

    @JsonIgnore
    @Option(names = { "--dumpConfig" }, description = "Prints the current configuration file and exits")
    public boolean dumpConfig=false;

}
