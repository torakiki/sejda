/*
 * Created on 27 dic 2017
 * Copyright 2017 by Andrea Vacondio (andrea.vacondio@gmail.com)
 * and Edi Weissmann (edi.weissmann@gmail.com).
 *
 * This file is part of sejda-sdk-pro.
 *
 * You are not permitted to distribute it in any form unless explicit
 * consent is given by Andrea Vacondio or Eduard Weissman.
 * You are not permitted to modify it.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.sejda.impl.sambox.component.pdfa;

import org.sejda.sambox.contentstream.operator.OperatorProcessor;

/**
 * An operator aware of the conversion context
 *
 * @author Andrea Vacondio
 */
abstract class PdfAContentStreamOperator extends OperatorProcessor {

    private ConversionContext conversionContext;

    public ConversionContext conversionContext() {
        return conversionContext;
    }

    public void setConversionContext(ConversionContext conversionContext) {
        this.conversionContext = conversionContext;
    }

}
