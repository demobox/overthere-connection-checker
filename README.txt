Description
===========

A connection checker to verify Overthere connection settings for remote machines. Debug output is written to a connection-check.log file with settings controlled by a logback.xml file in the JAR, which can be overridden by placing a different logback.xml in CHECKER_HOME/conf. 

When testing 'sudo' execution while connecting with SSH keys, sudo has to be set up as NOPASSWD or the password option (which isn't mandatory if you're using -keyfile) needs to be set. There's a warning to that effect.

Usage
=====

checker.[sh|cmd] arguments...
 -address VAL                           : The host name or IP address of the
                                          target machine
 -keyfile VAL                           : If using public/private keys, the key
                                          file to use to connect to the target
                                          machine
 -keypass VAL                           : The password to use to connect to the
                                          target machine (if not using SSH keys)
 -osType [WINDOWS | UNIX]               : The target operating system type
                                          (default UNIX)
 -password VAL                          : If not using public/private keys, the
                                          password to use to connect to the
                                          target machine
 -protocol [CIFS_TELNET | CIFS_WINRM |  : The protocol: only SSH_SCP, SSH_SFTP,
 SSH_SCP | SSH_SFTP | SSH_SUDO]         : SSH_SUDO, CIFS_TELNET, CIFS_WINRM
                                          supported
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

*	checker.[sh|cmd] -address apache-22 -protocol SSH_SUDO -username deployit -keyfile C:/Users/aphillips/.ssh/id_rsa -keypass foo -sudouser groovy 

NOTE: The above will only work with NOPASSWD!

*	checker.[sh|cmd] -address apache-22 -protocol SSH_SUDO -username deployit -keyfile C:/Users/aphillips/.ssh/id_rsa -keypass foo -sudouser groovy -password deployit