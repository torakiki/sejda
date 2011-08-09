package org.sejda.core.manipulation.model.parameter;

import java.util.HashSet;
import java.util.Set;

/**
 * Different types of simple split.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum SimpleSplitType {
    BURST() {
        @Override
        public Set<Integer> getPages(int totalNumberOfPage) {
            Set<Integer> retSet = new HashSet<Integer>();
            for (int i = 1; i <= totalNumberOfPage; i++) {
                retSet.add(i);
            }
            return retSet;
        }
    },
    EVEN_PAGES() {
        @Override
        public Set<Integer> getPages(int totalNumberOfPage) {
            Set<Integer> retSet = new HashSet<Integer>();
            for (int i = 2; i <= totalNumberOfPage; i = i + 2) {
                retSet.add(i);
            }
            return retSet;
        }
    },
    ODD_PAGES() {
        @Override
        public Set<Integer> getPages(int totalNumberOfPage) {
            Set<Integer> retSet = new HashSet<Integer>();
            for (int i = 1; i <= totalNumberOfPage; i = i + 2) {
                retSet.add(i);
            }
            return retSet;
        }
    };

    /**
     * 
     * @param totalNumberOfPage
     *            the number of pages of the document.
     * @return the set of pages to split at for this split type.
     */
    public abstract Set<Integer> getPages(int totalNumberOfPage);
}
