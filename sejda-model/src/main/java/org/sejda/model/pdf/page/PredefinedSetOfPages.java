package org.sejda.model.pdf.page;

import java.util.SortedSet;
import java.util.TreeSet;

import org.sejda.model.FriendlyNamed;

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
            SortedSet<Integer> retSet = new TreeSet<>();
            for (int i = 1; i <= totalNumberOfPage; i++) {
                retSet.add(i);
            }
            return retSet;
        }

        @Override
        public boolean includes(int page) {
            return true;
        }
    },
    EVEN_PAGES("even") {
        @Override
        public SortedSet<Integer> getPages(int totalNumberOfPage) {
            SortedSet<Integer> retSet = new TreeSet<>();
            for (int i = 2; i <= totalNumberOfPage; i = i + 2) {
                retSet.add(i);
            }
            return retSet;
        }

        @Override
        public boolean includes(int page) {
            return page % 2 == 0;
        }
    },
    ODD_PAGES("odd") {
        @Override
        public SortedSet<Integer> getPages(int totalNumberOfPage) {
            SortedSet<Integer> retSet = new TreeSet<>();
            for (int i = 1; i <= totalNumberOfPage; i = i + 2) {
                retSet.add(i);
            }
            return retSet;
        }

        @Override
        public boolean includes(int page) {
            return page % 2 == 1;
        }

    },
    NONE("none") {
        @Override
        public SortedSet<Integer> getPages(int totalNumberOfPage) {
            return new TreeSet<>();
        }

        @Override
        public boolean includes(int page) {
            return false;
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

    public abstract boolean includes(int page);

    private final String displayName;

    PredefinedSetOfPages(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getFriendlyName() {
        return displayName;
    }
}
