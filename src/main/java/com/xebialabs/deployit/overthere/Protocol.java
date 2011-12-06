/*
 * @(#)ConnectionType.java     15 Jul 2011
 *
 * Copyright © 2010 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package com.xebialabs.deployit.overthere;

import com.xebialabs.overthere.cifs.CifsConnectionType;
import com.xebialabs.overthere.ssh.SshConnectionType;

/**
 * @author aphillips
 * @since 15 Jul 2011
 *
 */
public enum Protocol {
    CIFS_TELNET("cifs", false, null, CifsConnectionType.TELNET),
    CIFS_WINRM("cifs", false, null, CifsConnectionType.WINRM_HTTP),
    CIFS_WINRMS("cifs", false, null, CifsConnectionType.WINRM_HTTPS),
    SSH_SCP("ssh", true, SshConnectionType.SCP, null),
    SSH_SFTP("ssh", true, SshConnectionType.SFTP, null),
    SSH_SUDO("ssh", true, SshConnectionType.SUDO, null);
    
    private String protocolType;
    private final boolean sshConnection;
    private final SshConnectionType sshType;
    private final CifsConnectionType cifsType;
    
    private Protocol(String protocolType, boolean sshConnection, SshConnectionType sshType,
            CifsConnectionType cifsType) {
        this.protocolType = protocolType;
        this.sshConnection = sshConnection;
        this.sshType = sshType;
        this.cifsType = cifsType;
    }

    String getProtocolType() {
        return protocolType;
    }
    
    boolean isSshConnection() {
        return sshConnection;
    }
    
    boolean isCifsConnection() {
        return !sshConnection;
    }

    SshConnectionType getSshType() {
        if (!isSshConnection()) {
            throw new IllegalStateException("not an SSH connection type");
        }
        
        return sshType;
    }

    CifsConnectionType getCifsType() {
        if (!isCifsConnection()) {
            throw new IllegalStateException("not a CIFS connection type");
        }

        return cifsType;
    }
}
