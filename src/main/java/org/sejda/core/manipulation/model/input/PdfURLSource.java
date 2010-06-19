/*
 * Created on 29/mag/2010
 * Copyright (C) 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.sejda.core.manipulation.model.input;

import java.net.URL;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * {@link PdfSource} from a {@link URL}
 * 
 * @author Andrea Vacondio
 * 
 */
public class PdfURLSource extends PdfSource {

    private static final long serialVersionUID = 3076969028015862056L;

    private URL url;

    public PdfURLSource(URL url, String name) {
        this(url, name, null);
    }

    public PdfURLSource(URL url, String name, String password) {
        super(name, password);
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public PdfSourceType getSourceType() {
        return PdfSourceType.URL_SOURCE;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).appendSuper(super.toString()).append(url).toString();
    }

}
