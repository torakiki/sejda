package org.sejda.core.manipulation.model.pdf.page;

import java.util.HashSet;
import java.util.Set;

/**
 * Represent a predefined set of pages like odd or even pages.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PredefinedSetOfPages implements PagesSelection {
    ALL_PAGES() {
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

    public abstract Set<Integer> getPages(int totalNumberOfPage);
}
