package com.xebialabs.deployit.overthere;

import static com.xebialabs.overthere.ConnectionOptions.TEMPORARY_DIRECTORY_PATH;
import static com.xebialabs.overthere.ssh.SshConnectionBuilder.ALLOCATE_DEFAULT_PTY;
import static com.xebialabs.overthere.ssh.SshConnectionBuilder.CONNECTION_TYPE;
import static com.xebialabs.overthere.ssh.SshConnectionBuilder.PASSPHRASE;
import static com.xebialabs.overthere.ssh.SshConnectionBuilder.PRIVATE_KEY_FILE;
import static com.xebialabs.overthere.ssh.SshConnectionType.INTERACTIVE_SUDO;
import static com.xebialabs.overthere.ssh.SshConnectionType.SUDO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.xebialabs.overthere.OperatingSystemFamily;

public class OptionsTest {

    @Test
    public void passwordIsRequiredForCifsTelnet() {
        Options options = new Options();
        options.protocol = Protocol.CIFS_TELNET;

        assertEquals(1, options.getValidationErrors().size());
    }

    @Test
    public void passwordIsRequiredForCifsWinrm() {
        Options options = new Options();
        options.protocol = Protocol.CIFS_WINRM;

        assertEquals(1, options.getValidationErrors().size());
    }
    
    @Test
    public void eitherKeyfileOrPasswordIsRequiredForSshScp() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SCP;

        assertEquals(1, options.getValidationErrors().size());
    }

    @Test
    public void eitherKeyfileOrPasswordIsRequiredForSshSftp() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SFTP;

        assertEquals(1, options.getValidationErrors().size());
    }

    @Test
    public void eitherKeyfileOrPasswordIsRequiredForSshSudo() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SUDO;
        // required for SSH_SUDO
        options.sudoUsername = "jbond";
        // otherwise results in an additional error
        options.sudoRequiresPassword = false;

        assertEquals(1, options.getValidationErrors().size());
    }

    @Test
    public void sudoUserIsRequiredForSshSudo() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SUDO;
        options.username = "jbond";
        options.password = "007";

        assertEquals(1, options.getValidationErrors().size());
    }

    @Test
    public void keyfileArgIsHonoured() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SFTP;
        options.username = "jbond";
        String keyfile = "/mi6/secret.key";
        options.keyfile = keyfile;

        assertEquals(keyfile, options.toConnectionOptions().get(PRIVATE_KEY_FILE));
    }

    @Test
    public void keypassArgIsHonoured() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SFTP;
        options.username = "jbond";
        options.keyfile = "/mi6/secret.key";
        String keypass = "secret";
        options.keypass = keypass;

        assertEquals(keypass, options.toConnectionOptions().get(PASSPHRASE));
    }

    @Test
    public void tmpdirArgIsHonoured() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SCP;
        options.username = "jbond";
        options.password = "secret";
        String tmpdir = "/tmp/secret";
        options.temporaryDirectoryLocation = tmpdir;

        assertEquals(tmpdir, options.toConnectionOptions().get(TEMPORARY_DIRECTORY_PATH));
    }

    @Test
    public void usesSudoIfSudoDoesNotRequirePass() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SUDO;
        options.username = "jbond";
        options.password = "secret";
        options.sudoUsername = "M";
        options.sudoRequiresPassword = false;

        assertEquals(SUDO, options.toConnectionOptions().get(CONNECTION_TYPE));
    }

    @Test
    public void usesInteractiveSudoIfSudoRequiresPass() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SUDO;
        options.username = "jbond";
        options.password = "secret";
        options.sudoUsername = "M";
        options.sudoRequiresPassword = true;

        assertEquals(INTERACTIVE_SUDO, options.toConnectionOptions().get(CONNECTION_TYPE));
    }

    @Test
    public void passwordIsRequiredIfSudoRequiresPass() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SUDO;
        options.username = "jbond";
        options.keyfile = "/mi6/secret.key";
        options.sudoUsername = "M";
        options.sudoRequiresPassword = true;

        assertEquals(1, options.getValidationErrors().size());
    }

    @Test
    public void allocatePtyArgIsHonoured() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SCP;
        options.username = "jbond";
        options.password = "secret";
        options.allocatePty = false;

        assertFalse(options.toConnectionOptions().<Boolean>get(ALLOCATE_DEFAULT_PTY));
    }

    @Test
    public void allocatePtyIsRequiredIfSudoRequiresPass() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SUDO;
        options.username = "jbond";
        options.password = "secret";
        options.sudoUsername = "M";
        options.sudoRequiresPassword = true;
        options.allocatePty = false;

        assertEquals(1, options.getValidationErrors().size());
    }

    @Test
    public void defaultTempDirIsSetForMissingTmpdirArgOnWindows() {
        Options options = new Options();
        options.protocol = Protocol.CIFS_TELNET;
        options.osType = OperatingSystemFamily.WINDOWS;
        options.username = "jbond";
        options.password = "secret";

        assertEquals(OperatingSystemFamily.WINDOWS.getDefaultTemporaryDirectoryPath(),
                options.toConnectionOptions().get(TEMPORARY_DIRECTORY_PATH));
    }

    @Test
    public void defaultTempDirIsSetForMissingTmpdirArgOnUnix() {
        Options options = new Options();
        options.protocol = Protocol.SSH_SFTP;
        options.osType = OperatingSystemFamily.UNIX;
        options.username = "jbond";
        options.password = "secret";

        assertEquals(OperatingSystemFamily.UNIX.getDefaultTemporaryDirectoryPath(),
                options.toConnectionOptions().get(TEMPORARY_DIRECTORY_PATH));
    }

}
