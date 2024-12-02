package org.sejda.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for {@link PageSize}.
 * <p>
 * This class tests the functionality of the `rotate` method in `PageSize` class, which swaps the width and height,
 * effectively changing the page's orientation between portrait and landscape.
 */
public class PageSizeTest {

    @Test
    public void testToPortraitFromLandscape() {
        var landscape = new PageSize(200, 100, "Landscape");
        var result = landscape.toPortrait();

        assertFalse(result.isLandscape());
        assertEquals(100, result.getWidth());
        assertEquals(200, result.getHeight());
        assertNotEquals(landscape, result);
    }

    @Test
    public void testToPortraitFromPortrait() {
        var portrait = new PageSize(100, 200, "Portrait");
        var result = portrait.toPortrait();

        assertFalse(result.isLandscape());
        assertEquals(100, result.getWidth());
        assertEquals(200, result.getHeight());
        assertEquals(portrait, result);
    }

    @Test
    public void testToLandscapeFromPortrait() {
        var portrait = new PageSize(100, 200, "Portrait");
        var result = portrait.toLandscape();

        assertTrue(result.isLandscape());
        assertEquals(200, result.getWidth());
        assertEquals(100, result.getHeight());
        assertNotEquals(portrait, result);
    }

    @Test
    public void testToLandscapeFromLandscape() {
        var landscape = new PageSize(200, 100, "Landscape");
        var result = landscape.toLandscape();

        assertTrue(result.isLandscape());
        assertEquals(200, result.getWidth());
        assertEquals(100, result.getHeight());
        assertEquals(landscape, result);
    }
}