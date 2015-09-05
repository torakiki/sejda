package org.sejda.model.pdf.page;

import java.util.SortedSet;
import java.util.TreeSet;

import org.sejda.common.FriendlyNamed;

/**
 * Represent a predefined set of pages like odd or even pages.
 * 
 * @author Andrea Vacondio
 * 
 */
public enum PredefinedSetOfPages implements PagesSelection, FriendlyNamed {
    ALL_PAGES("all") {
        @Override
        public SortedSet<Integer> getPages(int totalNumberOfPage) {
            SortedSet<Integer> retSet = new TreeSet<Integer>();
            for (int i = 1; i <= totalNumberOfPage; i++) {
                retSet.add(i);
            }
            return retSet;
        }
    },
    EVEN_PAGES("even") {
        @Override
        public SortedSet<Integer> getPages(int totalNumberOfPage) {
            SortedSet<Integer> retSet = new TreeSet<Integer>();
            for (int i = 2; i <= totalNumberOfPage; i = i + 2) {
                retSet.add(i);
            }
            return retSet;
        }
    },
    ODD_PAGES("odd") {
        @Override
        public SortedSet<Integer> getPages(int totalNumberOfPage) {
            SortedSet<Integer> retSet = new TreeSet<Integer>();
            for (int i = 1; i <= totalNumberOfPage; i = i + 2) {
                retSet.add(i);
            }
            return retSet;
        }

    },
    NONE("none") {
        @Override
        public SortedSet<Integer> getPages(int totalNumberOfPage) {
            return new TreeSet<Integer>();
        }
    };

    /**
     * @param totalNumberOfPage
     *            the number of pages of the document (upper limit).
     * @return the selected set of pages ordered using their natural ordering.
     * @see PagesSelection#getPages(int)
     */
    @Override
    public abstract SortedSet<Integer> getPages(int totalNumberOfPage);

    private String displayName;

    private PredefinedSetOfPages(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}
