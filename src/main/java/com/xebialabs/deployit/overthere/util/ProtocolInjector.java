/*
 * @(#)ProtocolInjector.java     15 Jul 2011
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
package com.xebialabs.deployit.overthere.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.overthere.Overthere;
import com.xebialabs.overthere.cifs.CifsTelnetConnection;
import com.xebialabs.overthere.spi.OverthereConnectionBuilder;
import com.xebialabs.overthere.ssh.SshConnectionBuilder;
import com.xebialabs.overthere.winrm.CifsWinRMConnectionBuilder;

/**
 * @author aphillips
 * @since 15 Jul 2011
 *
 */
public final class ProtocolInjector {
    private static final Map<String, Class<? extends OverthereConnectionBuilder>> protocols =
        ImmutableMap.of("ssh", SshConnectionBuilder.class, "cifs_telnet", CifsTelnetConnection.class,
                "cifs_winrm", CifsWinRMConnectionBuilder.class);
    
    public static void populateProtocolMap() {
        // also forces loading of Overthere
        if (getProtocols().get().isEmpty()) {
            // race condition here, but can't use compareAndSet because that uses reference equality
            getProtocols().set(protocols);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static AtomicReference<Map<String, Class<? extends OverthereConnectionBuilder>>> getProtocols() {
        try {
            Field protocolField = Overthere.class.getDeclaredField("protocols");
            protocolField.setAccessible(true);
            // static field
            return (AtomicReference<Map<String, Class<? extends OverthereConnectionBuilder>>>) protocolField.get(null);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
