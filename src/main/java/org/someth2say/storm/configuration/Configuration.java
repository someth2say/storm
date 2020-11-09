package org.someth2say.storm.configuration;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.util.List;

import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.arc.config.ConfigProperties;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@ConfigProperties(prefix="")
@CommandLine.Command(mixinStandardHelpOptions = true, version = "TO BE IMPORTED FROM POM")
public class Configuration {

    @Positive
    @Option(names = { "-t", "--threads" }, description = "How many worker threads use", defaultValue = "10")
    public Integer threads = null;

    @Positive
    @Option(names = { "-r", "--repeat" }, description = "How many times execute the request")
    public Integer repeat = null;

    @Option(names = { "-o", "--order" }, description = "Strategy for picking the next URL from the list.")
    public Order order = null;//

    @Parameters(arity = "0..*", description = "Target URIs for the requests")
    public List<URI> urls = null;

    @Option(names = {  "--proxy" }, description = "Proxy for setting all connections.")
    public InetSocketAddress proxy = null;

    @Positive
    @Option(names = {  "--connectTimeout" }, description = "Connection timeout in milliseconds.")
    public Integer connectTimeout = null;

    @Option(names = {  "--requestTimeout" }, description = "Request timeout in milliseconds.")
    public Integer requestTimeout = null;

    @Option(names = { "--redirect" }, description = "Strategy for picking the next URL from the list.")
    public Redirect redirect = null;

    @Option(names = { "--httpVersion" }, description = "HTTP Version to use for clients.")
    public Version httpVersion = null;

    @Option(names = { "-c", "--categorizers" }, description = "Categorizers to use to split response data.")
    public List<String> categorizers = null;

    @Option(names = { "-s", "--stats" }, description = "Stats generated for each category bucket.")
    public List<String> stats = null;

    @Positive
    @Option(names = { "-d", "--delay" }, description = "Delay time after a response")
    public Integer delay = null;

    @JsonIgnore
    @Option(names = { "--dumpConfig" }, description = "Prints the current configuration file and exits")
    public boolean dumpConfig=false;

    @JsonIgnore
    @Option(names={"--configFile"}, description = "Extra configuration file to load.")
    public File configFile = null;
}
