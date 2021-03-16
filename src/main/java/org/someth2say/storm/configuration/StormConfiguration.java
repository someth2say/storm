package org.someth2say.storm.configuration;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.someth2say.storm.category.CategorizerBuilder.CategorizerBuilderParams;
import org.someth2say.storm.stat.StatBuilder.StatBuilderParams;
import org.someth2say.storm.utils.SerializationUtils;

import io.quarkus.arc.config.ConfigProperties;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@ConfigProperties(prefix = "storm")
@CommandLine.Command(mixinStandardHelpOptions = true, version = "TO BE IMPORTED FROM POM")
public class StormConfiguration {

    @Positive
    @Option(names = {"-t", "--threads"}, description = "How many worker threads use")
    public Integer threads = null;

    @Positive
    @Option(names = {"-c", "--count"}, description = "How many times execute the requests")
    public Integer count = 10;

    @Positive
    @Option(names = {"-d", "--duration"}, description = "Milliseconds since the first request until the last accepted response.")
    public Integer duration = null;

    @Option(names = {"-o", "--order"}, description = "Strategy for picking the next URL from the list.")
    public Order order = Order.ROUNDROBIN;

    @Parameters(arity = "0..*", description = "Target URIs for the requests")
    public List<URI> urls = Collections.emptyList();

    @Option(names = {"--proxy"}, description = "Proxy for setting all connections.")
    public InetSocketAddress proxy = null;

    @Positive
    @Option(names = {"--connectTimeout"}, description = "Connection timeout in milliseconds.")
    public Integer connectTimeout = null;

    @Option(names = {"--requestTimeout"}, description = "Request timeout in milliseconds.")
    public Integer requestTimeout = null;

    @Option(names = {"--redirect"}, description = "Strategy for picking the next URL from the list.")
    public Redirect redirect = null;

    @Option(names = {"--httpVersion"}, description = "HTTP Version to use for clients.")
    public Version httpVersion = null;

    @Option(names = {"--cat", "--categorizers"}, description = "Categorizers to use to split response data.")
    public List<CategorizerBuilderParams> categorizerBuilderParams = Collections.emptyList();

    @Option(names = {"-s", "--stats"}, description = "Stats generated for each category bucket.")
    public List<StatBuilderParams> statBuilderParams = Collections.emptyList();

    @Positive
    @Option(names = {"--delay"}, description = "Delay time after a response")
    public Integer delay = null;

    @JsonIgnore
    @Option(names = {"--dumpConfig"}, description = "Prints the current configuration file and exits")
    public boolean dumpConfig = false;

    @JsonIgnore
    @Option(names = {"--configFile"}, description = "Extra configuration file to load.")
    public File configFile = null;

    @Override
    public String toString() {
        try {
            return SerializationUtils.toYAML(this);
        } catch (JsonProcessingException e) {
            return "<< non-serializable >>";
        }

    }
}
