/*
 * Created on 18 JAN 2017
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
package org.sejda.impl.sambox.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.sejda.model.pdf.transition.PdfPageTransition;
import org.sejda.model.pdf.transition.PdfPageTransitionStyle;
import org.sejda.sambox.pdmodel.interactive.pagenavigation.PDTransition;

public class TransitionUtilsTest {

    @Test
    public void testInitTransitionMotionBoxInward() {
        PDTransition to = new PDTransition();
        TransitionUtils.initTransitionMotion(PdfPageTransition.newInstance(PdfPageTransitionStyle.BOX_INWARD, 2, 2),
                to);
        assertEquals("I", to.getMotion());
    }

    @Test
    public void testInitTransitionMotionSplitHorizontalInward() {
        PDTransition to = new PDTransition();
        TransitionUtils.initTransitionMotion(
                PdfPageTransition.newInstance(PdfPageTransitionStyle.SPLIT_HORIZONTAL_INWARD, 2, 2), to);
        assertEquals("I", to.getMotion());
    }

    @Test
    public void testInitTransitionMotionSplitVerticalInward() {
        PDTransition to = new PDTransition();
        TransitionUtils.initTransitionMotion(
                PdfPageTransition.newInstance(PdfPageTransitionStyle.SPLIT_VERTICAL_INWARD, 2, 2), to);
        assertEquals("I", to.getMotion());
    }

    @Test
    public void testInitTransitionMotionBoxOnward() {
        PDTransition to = new PDTransition();
        TransitionUtils.initTransitionMotion(PdfPageTransition.newInstance(PdfPageTransitionStyle.BOX_OUTWARD, 2, 2),
                to);
        assertEquals("O", to.getMotion());
    }

    @Test
    public void testInitTransitionMotionSplitHorizontalOnward() {
        PDTransition to = new PDTransition();
        TransitionUtils.initTransitionMotion(
                PdfPageTransition.newInstance(PdfPageTransitionStyle.SPLIT_HORIZONTAL_OUTWARD, 2, 2), to);
        assertEquals("O", to.getMotion());
    }

    @Test
    public void testInitTransitionMotionSplitVerticalOnward() {
        PDTransition to = new PDTransition();
        TransitionUtils.initTransitionMotion(
                PdfPageTransition.newInstance(PdfPageTransitionStyle.SPLIT_VERTICAL_OUTWARD, 2, 2), to);
        assertEquals("O", to.getMotion());
    }

    @Test
    public void testInitTransitionDefault() {
        PDTransition to = new PDTransition();
        TransitionUtils.initTransitionMotion(PdfPageTransition.newInstance(PdfPageTransitionStyle.FADE, 2, 2), to);
        assertEquals("I", to.getMotion());
    }

}
