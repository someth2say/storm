package org.someth2say.storm.configuration;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;
import org.someth2say.storm.category.CategorizerIndex.CategorizerBuilderParams;
import org.someth2say.storm.stat.StatIndex.StatBuilderParams;

import io.quarkus.arc.config.ConfigProperties;
import picocli.CommandLine;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParseResult;

public class PicocliConfigSource implements ConfigSource {

    private static final Logger LOG = Logger.getLogger(PicocliConfigSource.class);

    private static String positionalArgsName;
    private static String configPrefix = null;

    private static String[] args;
    private static Map<String, String> properties;

    public static CommandLine commandLine;
    public static ParseResult parseResult;

    public static void init(final String[] args, final Class<?> commandClass) throws Exception {
        LOG.debugf("Initializing Picocli with args %s", Arrays.toString(args));
        init(args, commandClass, getConfigPrefixFromConfigClass(commandClass),
                getPositionalArgsNameFromCommandClass(commandClass));
    }

    public static void init(final String[] args, final Class<?> commandClass, final String positionalArgsName)
            throws Exception {
        init(args, commandClass, getConfigPrefixFromConfigClass(commandClass), positionalArgsName);
    }

    public static void init(final String[] args, final Class<?> commandClass, final String configPrefix,
            final String positionalArgsName) {

        PicocliConfigSource.args = args;
        PicocliConfigSource.configPrefix = configPrefix;
        PicocliConfigSource.positionalArgsName = positionalArgsName;
        commandLine = new CommandLine(commandClass)
            .registerConverter(CategorizerBuilderParams.class, s->new CategorizerBuilderParams(s))
            .registerConverter(StatBuilderParams.class, s->new StatBuilderParams(s));

        buildMapFromArgs();
    }

    private static String getConfigPrefixFromConfigClass(final Class<?> commandClass) {
        ConfigProperties configAnnotation = commandClass.getAnnotation(ConfigProperties.class);
        if (configAnnotation != null) {
            String prefix = configAnnotation.prefix();
            if (prefix.equals("<< unset >>"))
                prefix = "";
            LOG.debug("Detected config prefix as '" + prefix + "'");
            return prefix;
        }
        return "";
    }

    private static String getPositionalArgsNameFromCommandClass(Class<?> commandClass) {
        Field[] fields = commandClass.getFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Parameters.class)) {
                LOG.debug("Detected positional alguments name as '" + field.getName() + "'");
                return field.getName();
            }
        }
        return "";
    }

    @Override
    public int getOrdinal() {
        return 500;
    }

    @Override
    public Map<String, String> getProperties() {
        if (properties == null) {
            return Collections.emptyMap();
        }
        return properties;
    }

    private static void buildMapFromArgs() {
        parseResult = checkCommandLineParseResults(commandLine, args);
        if (parseResult != null) {
            properties = new HashMap<>();
            for (ArgSpec arg : parseResult.matchedArgs()) {
                String name = buildConfigNameForArg(arg);
                String value = buildConfigValueForArg(arg);
                LOG.debugf("Adding argument %s as %s=%s ", arg.paramLabel(), name, value);
                properties.put(name, value);
            }
        }
    }

    private static String buildConfigValueForArg(ArgSpec arg) {
        return arg.originalStringValues().stream().collect(Collectors.joining(","));
    }

    private static String buildConfigNameForArg(ArgSpec arg) {
        String argName = "";
        if (arg.isPositional()) {
            argName = positionalArgsName;
        } else {
            argName = ((OptionSpec) arg).longestName();
            while (argName.startsWith("-")) {
                argName = argName.substring(1);
            }
            argName = kebabCase(argName);
        }

        return configPrefix + "." + argName;
    }

    private static String kebabCase(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1-$2").toLowerCase();
    }

    @Override
    public String getValue(String propertyName) {

        if (args != null)
            return getProperties().get(propertyName);

        return null;
    }

    @Override
    public String getName() {
        return "picocli-config-source";
    }

    private static ParseResult checkCommandLineParseResults(CommandLine commandLine, final String... args) {
        ParseResult parseResults = commandLine.parseArgs(args);
        if (!parseResults.errors().isEmpty()) {
            parseResults.errors().forEach(e -> LOG.error(e));
            return null;
        }
        return parseResults;
    }

}
