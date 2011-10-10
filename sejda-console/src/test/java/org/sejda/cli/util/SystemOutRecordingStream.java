/*
 * Created on Aug 29, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.cli.util;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Records the contents of System.out, leaving the original stream untouched
 * 
 * @author Eduard Weissmann
 * 
 */
public class SystemOutRecordingStream extends FilterOutputStream {
    private final ByteArrayOutputStream capturedSystemOut = new ByteArrayOutputStream();

    public SystemOutRecordingStream(OutputStream underlyingSystemOut) {
        super(underlyingSystemOut);
    }

    @Override
    public void write(byte b[]) throws IOException {
        super.write(b);
        capturedSystemOut.write(b);
    }

    @Override
    public void write(byte b[], int off, int len) throws IOException {
        super.write(b, off, len);
        capturedSystemOut.write(b, off, len);
    }

    /**
     * @return the capturedSystemOut
     */
    public String getCapturedSystemOut() {
        return capturedSystemOut.toString();
    }
}