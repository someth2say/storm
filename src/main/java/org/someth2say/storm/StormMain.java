package org.someth2say.storm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.someth2say.storm.category.CategorizerBuilder;
import org.someth2say.storm.configuration.StormConfiguration;
import org.someth2say.storm.configuration.FileConfigurationSource;
import org.someth2say.storm.configuration.PicocliConfigSource;
import org.someth2say.storm.stat.StatBuilder;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine;
import picocli.CommandLine.Model.OptionSpec;

@QuarkusMain
public class StormMain {
    public static void main(String[] args) {
        try {
            PicocliConfigSource.init(args, StormConfiguration.class);
            loadExtraConfig();
            argsSanityCheck();
            Quarkus.run(StormQuarkusApplication.class, StormMain::exitHandler, args);
        } catch (Exception e) {
            System.err.println("Error starting STORM: " + getDeepCause(e));
        }
    }

    private static void loadExtraConfig() throws IOException {
        OptionSpec matchedOption = PicocliConfigSource.parseResult.matchedOption("--configFile");
        if (matchedOption != null){
            File configFile = matchedOption.getValue();
            FileConfigurationSource.init(configFile, StormConfiguration.class);
        }
    }

    private static void argsSanityCheck(){
        final CommandLine commandLine = PicocliConfigSource.commandLine;
        if (commandLine.isUsageHelpRequested()) {
            printHelp(commandLine);
        } else if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
        } 
    }

    private static void printHelp(final CommandLine commandLine) {
        commandLine.usage(System.out);
        System.out.println();
        System.out.println("Available categorizers: " + List.of(CategorizerBuilder.values()));
        System.out.println("Available stats: " + List.of(StatBuilder.values()));
    }

    public static void exitHandler(Integer exitCode, Throwable exception) {
        if (exception!=null){
            System.err.printf("Unhandled exception (%s): %s",exception.getClass().getName() ,getDeepCause(exception));
            exception.printStackTrace();
        }
        System.exit(exitCode);
    }

    private static String getDeepCause(Throwable e) {
        String msg = e.getMessage();
        Throwable cause = e.getCause();
        while (cause!=null && e!=cause){
            msg=cause.getMessage();
            cause=cause.getCause();
        }
        return msg;
    }



}
