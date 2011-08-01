package com.xebialabs.deployit.overthere;

import static com.xebialabs.deployit.overthere.Protocol.SSH_SUDO;
import static com.xebialabs.overthere.ConnectionOptions.ADDRESS;
import static com.xebialabs.overthere.ConnectionOptions.OPERATING_SYSTEM;
import static com.xebialabs.overthere.ConnectionOptions.PASSWORD;
import static com.xebialabs.overthere.ssh.SshConnectionBuilder.CONNECTION_TYPE;
import static com.xebialabs.overthere.ssh.SshConnectionType.INTERACTIVE_SUDO;
import static com.xebialabs.overthere.util.LoggingOverthereProcessOutputHandler.loggingHandler;
import static org.apache.commons.lang.StringUtils.isEmpty;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.deployit.overthere.step.ConnectionCheckStep;
import com.xebialabs.overthere.ConnectionOptions;

public class ConnectionChecker implements Runnable {
    private static final String CHECKER_RUN_COMMAND = "java -jar overthere-connection-checker.jar [options...] arguments...";

    private final Logger logger = LoggerFactory.getLogger(ConnectionChecker.class);
    private final String protocol;
    private final ConnectionOptions options;

    private ConnectionChecker(String protocol, ConnectionOptions options) {
        this.protocol = protocol;
        this.options = options;
    }

    @Override
    public void run() {
        executeConnectionCheck();

        // also try interactive SUDO
        if (protocol.equals("ssh") && options.get(CONNECTION_TYPE).equals(SSH_SUDO)) {
            options.get(CONNECTION_TYPE).equals(INTERACTIVE_SUDO);

            if (isEmpty((String) options.get(PASSWORD))) {
                logger.warn("***** WARNING: Without a password, sudo connections will only work correctly if the connecting user has NOPASSWD rights. Should a 'Sorry, try again' prompt (or similar) be displayed, please abort the connection from the target machine. *****");
            }

            executeConnectionCheck();
        }

        logger.info("Completed connection check");
    }

    private void executeConnectionCheck() {
        logger.info(String.format("Checking connection to host '%s' (OS '%s') using connection type '%s'",
                options.get(ADDRESS), options.get(OPERATING_SYSTEM), options.get(CONNECTION_TYPE)));

        new ConnectionCheckStep(protocol, options).execute(loggingHandler(logger));
    }

    public static void main(String[] args) {
        final Options options = parseCommandLine(args);

        if (options == null) {
            return;
        }

        new ConnectionChecker(options.protocol.getProtocolType(), 
                options.toConnectionOptions()).run();
    }

    private static Options parseCommandLine(String[] args) {
        Options options = new Options();
        final CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println(CHECKER_RUN_COMMAND);
            parser.printUsage(System.err);
            return null;
        }

        Set<String> validationErrors = options.getValidationErrors();
        if (!validationErrors.isEmpty()) {
            System.err.println("Please correct the following problems:");
            System.err.println("- "
                    + StringUtils.join(validationErrors, "\n- ") + "\n");
            System.err.println(CHECKER_RUN_COMMAND);
            parser.printUsage(System.err);
            return null;
        }
        return options;
    }
}
