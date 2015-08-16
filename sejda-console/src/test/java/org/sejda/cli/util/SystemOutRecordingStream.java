/*
 * Created on Aug 29, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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