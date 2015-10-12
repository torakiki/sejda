package org.sejda.impl.sambox.component;

public class OutlineItem implements Comparable<OutlineItem> {
    public final String title;
    public final int page;
    public final int level;
    // Sometimes when you click an outline item it goes to the beginning of the page,
    // some other times it goes to a specific page location (eg: 3rd paragraph title)
    public final boolean xyzDestination;

    public OutlineItem(String title, int page, int level, boolean xyzDestination) {
        this.title = title;
        this.page = page;
        this.level = level;
        this.xyzDestination = xyzDestination;
    }

    @Override
    public int compareTo(OutlineItem other) {
        return Integer.compare(this.page, other.page);
    }
}
