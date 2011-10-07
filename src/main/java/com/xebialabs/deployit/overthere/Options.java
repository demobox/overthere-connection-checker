package com.xebialabs.deployit.overthere;

import static com.xebialabs.deployit.overthere.Protocol.SSH_SUDO;
import static com.xebialabs.overthere.ConnectionOptions.ADDRESS;
import static com.xebialabs.overthere.ConnectionOptions.OPERATING_SYSTEM;
import static com.xebialabs.overthere.ConnectionOptions.TEMPORARY_DIRECTORY_PATH;
import static com.xebialabs.overthere.ConnectionOptions.USERNAME;
import static com.xebialabs.overthere.ssh.SshConnectionBuilder.ALLOCATE_DEFAULT_PTY;
import static com.xebialabs.overthere.ssh.SshConnectionBuilder.CONNECTION_TYPE;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.FalseSupportingBooleanOptionHandler;

import com.google.common.collect.Sets;
import com.xebialabs.overthere.ConnectionOptions;
import com.xebialabs.overthere.OperatingSystemFamily;
import com.xebialabs.overthere.ssh.SshConnectionBuilder;
import com.xebialabs.overthere.ssh.SshConnectionType;

public class Options {
    @Option(name = "-os-type", usage = "The target operating system type (default UNIX)")
    public OperatingSystemFamily osType = OperatingSystemFamily.UNIX;

    @Option(name = "-protocol", usage = "The protocol: only SSH_SCP, SSH_SFTP, SSH_SUDO, CIFS_TELNET, CIFS_WINRM supported", required = true)
    public Protocol protocol;

    @Option(name = "-address", usage = "The host name or IP address of the target machine", required = true)
    public String address;

    @Option(name = "-username", usage = "The user name to use to connect to the target machine", required = true)
    public String username;

    @Option(name = "-password", usage = "If not using public/private keys, the password to use to connect to the target machine")
    public String password;

    @Option(name = "-keyfile", usage = "If using public/private keys, the key file to use to connect to the target machine")
    public String keyfile;

    @Option(name = "-keypass", usage = "The password to use to connect to the target machine (if not using SSH keys)")
    public String keypass;

    @Option(name = "-sudouser", usage = "For connection type SSH_SUDO, the user to use for command execution (sudo -u <user> ...)")
    public String sudoUsername;

    @Option(name = "-sudo-requires-pass", handler = FalseSupportingBooleanOptionHandler.class, usage = "For connection type SSH_SUDO, specifies whether the invocation of 'sudo -u <user> ...' will prompt for a password that needs to be transmitted")
    public boolean sudoRequiresPassword = true;

    @Option(name = "-tmpdir", usage = "The temporary directory to use for session work files (default '/tmp' on UNIX, 'C:\\windows\\temp' on Windows)")
    public String temporaryDirectoryLocation;

    @Option(name = "-allocate-pty", handler = FalseSupportingBooleanOptionHandler.class, usage = "For SSH connections, whether to allocate a PTY when executing a command. Some sudo implementations require this even if no password is required")
    public boolean allocatePty = true;

    Set<String> getValidationErrors() {
        Set<String> errors = Sets.newHashSet();

        if (protocol.isSshConnection()) {
            if (StringUtils.isEmpty(password) && StringUtils.isEmpty(keyfile)) {
                errors.add("either '-password' or '-keyfile' is required for SSH* connections");
            }

            if (protocol == SSH_SUDO) {
                if (StringUtils.isEmpty(sudoUsername)) {
                    errors.add("'-sudouser' is required for SSH_SUDO connections");
                }

                if (sudoRequiresPassword && !allocatePty) {
                    errors.add("'-sudo-requires-pass' requires a PTY to be allocated using '-allocate-pty'");
                }
            }
        } else {
            // cifs_telnet or cifs_winrm
            if (StringUtils.isEmpty(password)) {
                errors.add("'-password' is required for CIFS_TELNET or CIFS_WINRM connections");
            }
        }
        return errors;
    }

    ConnectionOptions toConnectionOptions() {
        ConnectionOptions options = new ConnectionOptions();
        options.set(OPERATING_SYSTEM, osType);
        options.set(ADDRESS, address);
        options.set(USERNAME, username);
        
        if (protocol.isSshConnection()) {
            SshConnectionType sshType = protocol.getSshType();
            if (sshType.equals(SshConnectionType.SUDO) && sudoRequiresPassword) {
                sshType = SshConnectionType.INTERACTIVE_SUDO;
            }
            options.set(CONNECTION_TYPE, sshType);
            options.set(ALLOCATE_DEFAULT_PTY, allocatePty);
        }

        if (StringUtils.isNotEmpty(password)) {
            options.set(ConnectionOptions.PASSWORD, password);
        } else {
            options.set(SshConnectionBuilder.PRIVATE_KEY_FILE, keyfile);

            if (StringUtils.isNotEmpty(keypass)) {
                options.set(SshConnectionBuilder.PASSPHRASE, keypass);
            }
        }

        if (StringUtils.isNotEmpty(sudoUsername)) {
            options.set(SshConnectionBuilder.SUDO_USERNAME, sudoUsername);
        }

        options.set(TEMPORARY_DIRECTORY_PATH, 
                isNotEmpty(temporaryDirectoryLocation) 
                ? temporaryDirectoryLocation
                : osType.getDefaultTemporaryDirectoryPath());

        return options;
    }
}