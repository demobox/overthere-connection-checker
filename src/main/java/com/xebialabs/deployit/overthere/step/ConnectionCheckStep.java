/*
 * Copyright (c) 2008-2011 XebiaLabs B.V. All rights reserved.
 *
 * Your use of XebiaLabs Software and Documentation is subject to the Personal
 * License Agreement.
 *
 * http://www.xebialabs.com/deployit-personal-edition-license-agreement
 *
 * You are granted a personal license (i) to use the Software for your own
 * personal purposes which may be used in a production environment and/or (ii)
 * to use the Documentation to develop your own plugins to the Software.
 * "Documentation" means the how to's and instructions (instruction videos)
 * provided with the Software and/or available on the XebiaLabs website or other
 * websites as well as the provided API documentation, tutorial and access to
 * the source code of the XebiaLabs plugins. You agree not to (i) lease, rent
 * or sublicense the Software or Documentation to any third party, or otherwise
 * use it except as permitted in this agreement; (ii) reverse engineer,
 * decompile, disassemble, or otherwise attempt to determine source code or
 * protocols from the Software, and/or to (iii) copy the Software or
 * Documentation (which includes the source code of the XebiaLabs plugins). You
 * shall not create or attempt to create any derivative works from the Software
 * except and only to the extent permitted by law. You will preserve XebiaLabs'
 * copyright and legal notices on the Software and Documentation. XebiaLabs
 * retains all rights not expressly granted to You in the Personal License
 * Agreement.
 */

package com.xebialabs.deployit.overthere.step;

import static com.xebialabs.overthere.ConnectionOptions.ADDRESS;
import static com.xebialabs.overthere.ConnectionOptions.TEMPORARY_DIRECTORY_PATH;
import static com.xebialabs.overthere.OperatingSystemFamily.UNIX;
import static java.lang.String.format;

import com.xebialabs.overthere.CmdLine;
import com.xebialabs.overthere.ConnectionOptions;
import com.xebialabs.overthere.Overthere;
import com.xebialabs.overthere.OverthereConnection;
import com.xebialabs.overthere.OverthereFile;
import com.xebialabs.overthere.OverthereProcessOutputHandler;
import com.xebialabs.overthere.util.OverthereUtils;

public class ConnectionCheckStep {
    private final String protocol;
    private final ConnectionOptions options;

    public ConnectionCheckStep(String protocol, ConnectionOptions options) {
        this.protocol = protocol;
        this.options = options;
    }

    public boolean execute(OverthereProcessOutputHandler handler) {
        try {
            OverthereConnection connection = Overthere.getConnection(protocol, options);
            try {
                echoTmpDirContents(connection, handler);
                OverthereFile fileToUpload = uploadFile(connection);
                tryCanGetFilePermissions(connection, fileToUpload);
            } finally {
                connection.disconnect();
            }
        } catch (Exception exc) {
            handler.handleErrorLine(format("Connection test to host " + options.get(ADDRESS) + " failed. Please check address, username and password. Exception message is: %s", exc));
            return false;
        }
        handler.handleOutputLine("Connection test to host " + options.get(ADDRESS) + " succeeded");
        return true;
    }

    private void echoTmpDirContents(OverthereConnection connection,
            OverthereProcessOutputHandler handler) {
        String dircmd = (connection.getHostOperatingSystem() == UNIX) ? "ls" : "dir";
        CmdLine cmdLine = new CmdLine();
        cmdLine.addArgument(dircmd);
        cmdLine.addArgument((String) options.get(TEMPORARY_DIRECTORY_PATH));
        connection.execute(handler, cmdLine);
    }

    private OverthereFile uploadFile(OverthereConnection connection) {
        OverthereFile fileToUpload = connection.getTempFile("hostconnection-remote", ".txt");
        OverthereUtils.write("Contents of host connection test file", "UTF-8", fileToUpload);
        return fileToUpload;
    }

    private void tryCanGetFilePermissions(OverthereConnection connection,
            OverthereFile uploadedFile) {
        OverthereFile filesJustUploadedNotAsATempFile = connection.getFile(uploadedFile.getPath());
        if (!filesJustUploadedNotAsATempFile.canRead()) {
            throw new RuntimeException("Cannot check read permission for file just uploaded");
        }
    }
}
