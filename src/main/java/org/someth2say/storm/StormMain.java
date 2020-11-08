package org.someth2say.storm;

import java.io.File;
import java.io.IOException;

import org.someth2say.storm.configuration.Configuration;
import org.someth2say.storm.configuration.FileConfigurationSource;
import org.someth2say.storm.configuration.PicocliConfigSource;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;
import picocli.CommandLine.Model.OptionSpec;

@QuarkusMain
public class StormMain {

    public static void main(String[] args) {
        try {
            PicocliConfigSource.init(args, Configuration.class);
            loadExtraConfig();
            Quarkus.run(Storm.class, StormMain::exitHandler, args);
        } catch (Exception e) {
            System.err.println("Error starting STORM: " + getDeepCause(e));
        }
    }

    private static void loadExtraConfig() throws IOException {
        OptionSpec matchedOption = PicocliConfigSource.parseResult.matchedOption("--configFile");
        if (matchedOption != null){
            File configFile = matchedOption.getValue();
            FileConfigurationSource.init(configFile, Configuration.class);
        }
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

    public static void exitHandler(Integer exitCode, Throwable exception) {
        if (exception!=null){
            System.err.println("Unhandled exception: " + getDeepCause(exception));
            //exception.printStackTrace();
        }
        System.exit(exitCode);
    }

}
