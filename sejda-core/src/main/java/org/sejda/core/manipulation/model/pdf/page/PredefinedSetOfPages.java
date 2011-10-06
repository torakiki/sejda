package org.sejda.core.manipulation.model.pdf.page;

import java.util.HashSet;
import java.util.Set;

import org.sejda.core.DisplayNamedEnum;

/**
 * Represent a predefined set of pages like odd or even pages.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PredefinedSetOfPages implements PagesSelection, DisplayNamedEnum {
    ALL_PAGES("all") {
        @Override
        public Set<Integer> getPages(int totalNumberOfPage) {
            Set<Integer> retSet = new HashSet<Integer>();
            for (int i = 1; i <= totalNumberOfPage; i++) {
                retSet.add(i);
            }
            return retSet;
        }
    },
    EVEN_PAGES("even") {
        @Override
        public Set<Integer> getPages(int totalNumberOfPage) {
            Set<Integer> retSet = new HashSet<Integer>();
            for (int i = 2; i <= totalNumberOfPage; i = i + 2) {
                retSet.add(i);
            }
            return retSet;
        }
    },
    ODD_PAGES("odd") {
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

    private String displayName;

    private PredefinedSetOfPages(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
