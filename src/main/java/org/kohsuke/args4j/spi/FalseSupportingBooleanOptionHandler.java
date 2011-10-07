/*
 * @(#)FalseSupportingBooleanOptionHandler.java     7 Oct 2011
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
package org.kohsuke.args4j.spi;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;

import com.google.common.collect.ImmutableMap;

public class FalseSupportingBooleanOptionHandler extends OptionHandler<Boolean> {
    // same values as BooleanOptionHandler
    private static final Map<String, Boolean> ACCEPTABLE_VALUES = ImmutableMap.<String, Boolean>builder()
        .put("true", TRUE).put("on", TRUE).put("yes", TRUE).put("1", TRUE)
        .put("false", FALSE).put("off", FALSE).put("no", FALSE).put("0", FALSE)
        .build();

    public FalseSupportingBooleanOptionHandler(CmdLineParser parser,
            OptionDef option, Setter<? super Boolean> setter) {
        super(parser, option, setter);
    }

    @Override
    public int parseArguments(Parameters params) throws CmdLineException {
        // end of arg list or next arg is another option
        if ((params.size() == 0) || params.getParameter(0).startsWith("-")) {
            setter.addValue(TRUE);
            return 0;
        } else {
            setter.addValue(getBoolean(params.getParameter(0)));
            return 1;
        }
    }

    private Boolean getBoolean(String parameter) throws CmdLineException {
        String valueStr = parameter.toLowerCase();
        if (!ACCEPTABLE_VALUES.containsKey(valueStr)) {
            throw new CmdLineException(owner, Messages.ILLEGAL_BOOLEAN.format(valueStr));
        }
        return ACCEPTABLE_VALUES.get(valueStr);
    }

    @Override
    public String getDefaultMetaVariable() {
        return "[VAL]";
    }
}
