/*
 * Created on 09 set 2015
 * Copyright 2015 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * This file is part of Sejda.
 *
 * Sejda is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sejda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Sejda.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.impl.sambox.component;

import static org.sejda.sambox.cos.COSName.DA;
import static org.sejda.sambox.cos.COSName.DATAPREP;
import static org.sejda.sambox.cos.COSName.DS;
import static org.sejda.sambox.cos.COSName.DV;
import static org.sejda.sambox.cos.COSName.FF;
import static org.sejda.sambox.cos.COSName.FT;
import static org.sejda.sambox.cos.COSName.I;
import static org.sejda.sambox.cos.COSName.KIDS;
import static org.sejda.sambox.cos.COSName.LOCK;
import static org.sejda.sambox.cos.COSName.MAX_LEN;
import static org.sejda.sambox.cos.COSName.OPT;
import static org.sejda.sambox.cos.COSName.PARENT;
import static org.sejda.sambox.cos.COSName.Q;
import static org.sejda.sambox.cos.COSName.RV;
import static org.sejda.sambox.cos.COSName.SV;
import static org.sejda.sambox.cos.COSName.T;
import static org.sejda.sambox.cos.COSName.TI;
import static org.sejda.sambox.cos.COSName.TM;
import static org.sejda.sambox.cos.COSName.TU;
import static org.sejda.sambox.cos.COSName.V;

import java.util.Collection;

import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.PDPage;
import org.sejda.sambox.pdmodel.interactive.form.PDAcroForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Andrea Vacondio
 *
 */
public class AcroFormsMerger {
    private static final Logger LOG = LoggerFactory.getLogger(AcroFormsMerger.class);

    private static final COSName[] FIELD_KEYS = { FT, PARENT, KIDS, T, TU, TM, FF, V, DV, DA, Q, DS, RV, OPT, MAX_LEN,
            TI, I, LOCK, SV, DATAPREP };

    private AcroFormPolicy policy;
    private PDAcroForm form;

    public AcroFormsMerger(AcroFormPolicy policy, PDDocument destination) {
        this.policy = policy;
        this.form = new PDAcroForm(destination);
    }

    public void updateForm(PDAcroForm originalForm, String sourceName, Collection<PDPage> relevantPages) {
        if (originalForm != null && !relevantPages.isEmpty()) {
            switch (policy) {
            case MERGE_UNIQUE_NAMES:

                break;
            case MERGE:

                break;
            default:
                LOG.debug("Discarding acroform for {}", sourceName);
            }
        } else {
            LOG.debug("Skipped acroform merge, nothing to merge for {}", sourceName);
        }
    }

    public boolean hasForm() {
        return !form.getFields().isEmpty();
    }

    public PDAcroForm getForm() {
        return form;
    }
}
