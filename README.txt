Description
===========

A connection checker to verify Overthere connection settings for remote machines. Debug output is written to a connection-check.log file with settings controlled by a logback.xml file in the JAR, which can be overridden by placing a different logback.xml in CHECKER_HOME/conf. 

When testing 'sudo' execution while connecting with SSH keys, sudo has to be set up as NOPASSWD or the password option (which isn't mandatory if you're using -keyfile) needs to be set. There's a warning to that effect.

Usage
=====

Run checker.[sh|cmd] from CHECKER_HOME/bin

checker.[sh|cmd] arguments...
 -address VAL                           : The host name or IP address of the
                                          target machine
 -allocate-pty [VAL]                    : For SSH connections, whether to
                                          allocate a PTY when executing a
                                          command. Some sudo implementations
                                          require this even if no password is
                                          required
 -keyfile VAL                           : If using public/private keys, the key
                                          file to use to connect to the target
                                          machine
 -keypass VAL                           : The password to use to connect to the
                                          target machine (if not using SSH keys)
 -os-type [WINDOWS | UNIX]              : The target operating system type
                                          (default UNIX)
 -password VAL                          : If not using public/private keys, the
                                          password to use to connect to the
                                          target machine
 -protocol [CIFS_TELNET | CIFS_WINRM |  : The protocol: only SSH_SCP, SSH_SFTP,
 CIFS_WINRMS |SSH_SCP | SSH_SFTP |      : SSH_SUDO, CIFS_TELNET, CIFS_WINRM,
 SSH_SUDO]                              : CIFS_WINRMS supported
 -sudo-requires-pass [VAL]              : For connection type SSH_SUDO,
                                          specifies whether the invocation of
                                          'sudo -u <user> ...' will prompt for
                                          a password that needs to be transmitted
 -sudouser VAL                          : For connection type SSH_SUDO, the
                                          user to use for command execution
                                          (sudo -u <user> ...)
 -tmpdir VAL                            : The temporary directory to use for
                                          session work files (default '/tmp' on
                                          UNIX, 'C:\windows\temp' on Windows)
 -username VAL                          : The user name to use to connect to
                                          the target machine

Examples
========

*	checker.[sh|cmd] -address apache-22 -protocol SSH_SCP -username deployit -keyfile C:/Users/aphillips/.ssh/id_rsa -keypass foo

*	checker.[sh|cmd] -address apache-22 -protocol SSH_SUDO -username deployit -password deployit

NOTE: The following two examples will only work with NOPASSWD!

*	checker.[sh|cmd] -address apache-22 -protocol SSH_SUDO -username deployit -keyfile C:/Users/aphillips/.ssh/id_rsa -keypass foo -sudouser groovy -sudo-requires-pass false

*	checker.[sh|cmd] -address apache-22 -protocol SSH_SUDO -username deployit -keyfile C:/Users/aphillips/.ssh/id_rsa -keypass foo -sudouser groovy -sudo-requires-pass false -allocate-pty false

NOTE: In the following example, '-keyfile' will be used for SSH authentication, '-password' for sudo authentication
*	checker.[sh|cmd] -address apache-22 -protocol SSH_SUDO -username deployit -keyfile C:/Users/aphillips/.ssh/id_rsa -keypass foo -sudouser groovy -password deployit