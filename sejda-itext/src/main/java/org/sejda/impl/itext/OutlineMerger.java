/*
 * Created on 10/giu/2013
 * Copyright 2011 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.impl.itext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sejda.impl.itext.component.ITextOutlineSubsetProvider;
import org.sejda.model.exception.TaskException;
import org.sejda.model.input.PdfMergeInput;
import org.sejda.model.outline.OutlinePolicy;
import org.sejda.model.outline.OutlineSubsetProvider;
import org.sejda.model.pdf.page.PageRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.pdf.PdfReader;

/**
 * Helper class for the {@link MergeTask} creating the new document outline based on the selected {@link OutlinePolicy}
 * 
 * @author Andrea Vacondio
 * 
 */
class OutlineMerger {
    private static final Logger LOG = LoggerFactory.getLogger(OutlineMerger.class);

    private OutlinePolicy policy;
    private List<Map<String, Object>> outline = new ArrayList<Map<String, Object>>();

    OutlineMerger(OutlinePolicy policy) {
        this.policy = policy;
    }

    void updateOutline(PdfReader reader, PdfMergeInput input, int offset) throws TaskException {
        switch (policy) {
        case ONE_ENTRY_EACH_DOC:
            LOG.debug("One entry for each file policy not implemented");
            break;
        case RETAIN:
            updateRetainingOutline(reader, input, offset);
            break;
        default:
            LOG.debug("Discarding outline");
        }
    }

    public List<Map<String, Object>> getOutline() {
        return outline;
    }

    private void updateRetainingOutline(PdfReader reader, PdfMergeInput input, int offset) throws TaskException {
        LOG.debug("Retaining outline");
        OutlineSubsetProvider<Map<String, Object>> outlineProvider = new ITextOutlineSubsetProvider(reader);
        if (input.isAllPages()) {
            LOG.trace("Adding complete outline");
            outline.addAll(outlineProvider.getOutlineWithOffset(offset));
        } else {
            for (PageRange range : input.getPageSelection()) {
                outlineProvider.startPage(range.getStart());
                LOG.trace("Adding outline for {}", range);
                outline.addAll(outlineProvider.getOutlineUntillPageWithOffset(range.getEnd(), offset));
            }
        }
    }
}
