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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.sejda.impl.sambox.component.SignatureClipper.clipSignature;
import static org.sejda.sambox.cos.COSName.A;
import static org.sejda.sambox.cos.COSName.AF;
import static org.sejda.sambox.cos.COSName.AP;
import static org.sejda.sambox.cos.COSName.AS;
import static org.sejda.sambox.cos.COSName.BM;
import static org.sejda.sambox.cos.COSName.BORDER;
import static org.sejda.sambox.cos.COSName.BS;
import static org.sejda.sambox.cos.COSName.C;
import static org.sejda.sambox.cos.COSName.CONTENTS;
import static org.sejda.sambox.cos.COSName.DA;
import static org.sejda.sambox.cos.COSName.DATAPREP;
import static org.sejda.sambox.cos.COSName.DS;
import static org.sejda.sambox.cos.COSName.DV;
import static org.sejda.sambox.cos.COSName.F;
import static org.sejda.sambox.cos.COSName.FF;
import static org.sejda.sambox.cos.COSName.FT;
import static org.sejda.sambox.cos.COSName.H;
import static org.sejda.sambox.cos.COSName.I;
import static org.sejda.sambox.cos.COSName.KIDS;
import static org.sejda.sambox.cos.COSName.LOCK;
import static org.sejda.sambox.cos.COSName.M;
import static org.sejda.sambox.cos.COSName.MAX_LEN;
import static org.sejda.sambox.cos.COSName.MK;
import static org.sejda.sambox.cos.COSName.NM;
import static org.sejda.sambox.cos.COSName.OC;
import static org.sejda.sambox.cos.COSName.OPT;
import static org.sejda.sambox.cos.COSName.P;
import static org.sejda.sambox.cos.COSName.PARENT;
import static org.sejda.sambox.cos.COSName.PMD;
import static org.sejda.sambox.cos.COSName.Q;
import static org.sejda.sambox.cos.COSName.RECT;
import static org.sejda.sambox.cos.COSName.RV;
import static org.sejda.sambox.cos.COSName.STRUCT_PARENT;
import static org.sejda.sambox.cos.COSName.SUBTYPE;
import static org.sejda.sambox.cos.COSName.SV;
import static org.sejda.sambox.cos.COSName.T;
import static org.sejda.sambox.cos.COSName.TI;
import static org.sejda.sambox.cos.COSName.TM;
import static org.sejda.sambox.cos.COSName.TU;
import static org.sejda.sambox.cos.COSName.TYPE;
import static org.sejda.sambox.cos.COSName.V;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.sejda.common.LookupTable;
import org.sejda.impl.sambox.util.AcroFormUtils;
import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.sejda.sambox.pdmodel.interactive.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component providing methods to merge multiple acroforms together using different strategies.
 * 
 * @author Andrea Vacondio
 */
public class AcroFormsMerger {
    private static final Logger LOG = LoggerFactory.getLogger(AcroFormsMerger.class);

    private static final COSName[] FIELD_KEYS = { FT, PARENT, KIDS, T, TU, TM, FF, V, DV, DA, Q, DS, RV, OPT, MAX_LEN,
            TI, I, LOCK, SV, DATAPREP };

    private static final COSName[] WIDGET_KEYS = { TYPE, SUBTYPE, RECT, CONTENTS, P, NM, M, F, AP, AS, BORDER, C,
            STRUCT_PARENT, OC, AF, BM, H, MK, A, BS, PMD };

    private AcroFormPolicy policy;
    private PDAcroForm form;
    private String random = Long.toString(UUID.randomUUID().getMostSignificantBits(), 36);
    private Long counter = 0L;

    private final BiFunction<PDTerminalField, LookupTable<PDField>, PDTerminalField> createOrReuseTerminalField = (
            PDTerminalField existing, LookupTable<PDField> fieldsLookup) -> {
        PDField previouslyCreated = ofNullable(getMergedField(existing.getFullyQualifiedName()))
                .orElseGet(() -> fieldsLookup.lookup(existing));
        if (isNull(previouslyCreated)) {
            previouslyCreated = PDFieldFactory.createFieldAddingChildToParent(this.form,
                    existing.getCOSObject().duplicate(),
                    (PDNonTerminalField) fieldsLookup.lookup(existing.getParent()));
            previouslyCreated.getCOSObject().removeItem(COSName.KIDS);
            fieldsLookup.addLookupEntry(existing, previouslyCreated);
        }
        if (!previouslyCreated.isTerminal()) {
            LOG.warn("Cannot merge terminal field because a non terminal field with the same name already exists: {}",
                    existing.getFullyQualifiedName());
            return null;
        }
        return (PDTerminalField) previouslyCreated;
    };

    private final BiFunction<PDTerminalField, LookupTable<PDField>, PDTerminalField> createRenamingTerminalField = (
            PDTerminalField existing, LookupTable<PDField> fieldsLookup) -> {
        PDTerminalField newField = (PDTerminalField) PDFieldFactory.createFieldAddingChildToParent(this.form,
                existing.getCOSObject().duplicate(), (PDNonTerminalField) fieldsLookup.lookup(existing.getParent()));
        if (nonNull(getMergedField(existing.getFullyQualifiedName())) || fieldsLookup.hasLookupFor(existing)) {
            newField.setPartialName(String.format("%s%s%d", existing.getPartialName(), random, ++counter));
            LOG.info("Existing terminal field renamed from {} to {}", existing.getPartialName(),
                    newField.getPartialName());
        }
        newField.getCOSObject().removeItem(COSName.KIDS);
        fieldsLookup.addLookupEntry(existing, newField);
        return newField;
    };

    private final BiConsumer<PDField, LookupTable<PDField>> createOrReuseNonTerminalField = (PDField field,
            LookupTable<PDField> fieldsLookup) -> {
        if (getMergedField(field.getFullyQualifiedName()) == null && !fieldsLookup.hasLookupFor(field)) {
            PDField newField = PDFieldFactory.createFieldAddingChildToParent(this.form,
                    field.getCOSObject().duplicate(), (PDNonTerminalField) fieldsLookup.lookup(field.getParent()));
            newField.getCOSObject().removeItem(COSName.KIDS);
            fieldsLookup.addLookupEntry(field, newField);
        }
    };

    private PDField getMergedField(String fullyQualifiedName) {
        return ofNullable(fullyQualifiedName).map(form::getField).orElse(null);
    }

    private final BiConsumer<PDField, LookupTable<PDField>> createRenamingNonTerminalField = (PDField field,
            LookupTable<PDField> fieldsLookup) -> {
        PDField newField = PDFieldFactory.createFieldAddingChildToParent(this.form, field.getCOSObject().duplicate(),
                (PDNonTerminalField) fieldsLookup.lookup(field.getParent()));
        if (getMergedField(field.getFullyQualifiedName()) != null || fieldsLookup.hasLookupFor(field)) {
            newField.setPartialName(String.format("%s%s%d", field.getPartialName(), random, ++counter));
            LOG.info("Existing non terminal field renamed from {} to {}", field.getPartialName(),
                    newField.getPartialName());
        }
        newField.getCOSObject().removeItem(COSName.KIDS);
        fieldsLookup.addLookupEntry(field, newField);
    };

    public AcroFormsMerger(AcroFormPolicy policy, PDDocument destination) {
        this.policy = policy;
        this.form = new PDAcroForm(destination);
    }

    /**
     * Merge the original form to the current one, considering only fields whose widgets are available in the given lookup table.
     * 
     * @param originalForm
     *            the form to merge
     * @param annotationsLookup
     *            lookup for relevant annotations
     */
    public void mergeForm(PDAcroForm originalForm, LookupTable<PDAnnotation> annotationsLookup) {
        if (nonNull(originalForm)) {
            if (originalForm.hasXFA()) {
                LOG.warn("The AcroForm has XFA resurces which will be ignored");
            }
            LOG.debug("Merging acroforms with policy {}", policy);
            switch (policy) {
            case MERGE_RENAMING_EXISTING_FIELDS:
                updateForm(originalForm, annotationsLookup, createRenamingTerminalField,
                        createRenamingNonTerminalField);
                break;
            case MERGE:
                updateForm(originalForm, annotationsLookup, createOrReuseTerminalField, createOrReuseNonTerminalField);
                break;
            case FLATTEN:
                updateForm(originalForm, annotationsLookup, createRenamingTerminalField,
                        createRenamingNonTerminalField);
                flatten();
                break;
            default:
                LOG.debug("Discarding acroform");
            }
        } else {
            LOG.debug("Skipped acroform merge, nothing to merge");
        }
    }

    /**
     * For each new widget annotation in the lookup table removes all the Field keys.
     * 
     * @param annotations
     */
    private void removeFieldKeysFromWidgets(Collection<PDAnnotationWidget> annotations) {
        annotations.stream().map(PDAnnotation::getCOSObject).forEach(a -> a.removeItems(FIELD_KEYS));
        LOG.trace("Removed fields keys from widget annotations");
    }

    private void updateForm(PDAcroForm originalForm, LookupTable<PDAnnotation> annotationsLookup,
            BiFunction<PDTerminalField, LookupTable<PDField>, PDTerminalField> getTerminalField,
            BiConsumer<PDField, LookupTable<PDField>> createNonTerminalField) {
        AcroFormUtils.mergeDefaults(originalForm, form);
        LookupTable<PDField> fieldsLookup = new LookupTable<>();
        // it must be a pre order visit because we have to process non terminal first otherwise terminal ones won't get a parent
        originalForm.getFieldTree().stream().forEach(field -> {
            if (!field.isTerminal()) {
                createNonTerminalField.accept(field, fieldsLookup);
            } else {
                List<PDAnnotationWidget> relevantWidgets = findMappedWidgetsFor((PDTerminalField) field,
                        annotationsLookup);
                if (!relevantWidgets.isEmpty()) {
                    PDTerminalField terminalField = getTerminalField.apply((PDTerminalField) field, fieldsLookup);
                    if (nonNull(terminalField)) {
                        removeFieldKeysFromWidgets(relevantWidgets);
                        for (PDAnnotationWidget widget : relevantWidgets) {
                            terminalField.addWidgetIfMissing(widget);
                        }
                        terminalField.getCOSObject().removeItems(WIDGET_KEYS);
                    }
                } else {
                    LOG.debug("Discarded not relevant field {}", field.getFullyQualifiedName());
                }
            }
        });

        this.form.addFields(originalForm.getFields().stream().map(fieldsLookup::lookup).filter(Objects::nonNull)
                .collect(Collectors.toList()));
        // let's process those annotations containing merged widget/fields dictionaries and somehow not referenced by originalForm acroform (ex. empty fields array)
        annotationsLookup.values().stream().filter(a -> a instanceof PDAnnotationWidget)
                .filter(a -> a.getCOSObject().containsKey(COSName.T)).map(a -> (PDAnnotationWidget) a).collect(toList())
                .forEach(w -> {
                    PDField orphanField = PDFieldFactory.createField(originalForm, w.getCOSObject(), null);
                    if (orphanField instanceof PDTerminalField) {
                        PDTerminalField newOrphanField = getTerminalField.apply((PDTerminalField) orphanField,
                                fieldsLookup);
                        if (nonNull(newOrphanField)) {
                            w.getCOSObject().removeItems(FIELD_KEYS);
                            newOrphanField.addWidgetIfMissing(w);
                            newOrphanField.getCOSObject().removeItems(WIDGET_KEYS);
                            if (isNull(getMergedField(newOrphanField.getFullyQualifiedName()))) {
                                this.form.addFields(Arrays.asList(newOrphanField));
                            }
                        }
                    }
                });
    }

    /**
     * @param field
     * @param annotationsLookup
     * @return the list of relevant widgets for the given field.
     */
    private List<PDAnnotationWidget> findMappedWidgetsFor(PDTerminalField field,
            LookupTable<PDAnnotation> annotationsLookup) {
        return field.getWidgets().stream().map(annotationsLookup::lookup).filter(w -> w instanceof PDAnnotationWidget)
                .map(w -> (PDAnnotationWidget) w).collect(toList());

    }

    private void flatten() {
        try {
            List<PDField> fields = new ArrayList<>();
            for (PDField field : form.getFieldTree()) {
                fields.add(field);

                if(field instanceof PDVariableText) {
                    ensureValueCanBeDisplayed((PDVariableText) field);
                }
            }
            form.flatten(fields, true);
        } catch (IOException | UnsupportedOperationException ex) {
            LOG.warn("Failed to flatten form", ex);
        }
    }

    /**
     * Makes sure the string can be displayed using appearances font
     */
    private void ensureValueCanBeDisplayed(PDVariableText field) {
        String value = field.getValueAsString();
        if(!FontUtils.canDisplay(value, field.getAppearanceFont())) {
            PDFont fallbackFont = FontUtils.findFontFor(form.getDocument(), value);
            field.setAppearanceOverrideFont(fallbackFont);
            LOG.debug("Form field can't render (in appearances) it's value '%', will use font % for better support", value, fallbackFont);
        }
    }

    /**
     * Performs some cleanup task on the resulting {@link PDAcroForm} and then returns it.
     * 
     * @return
     */
    public PDAcroForm getForm() {
        for (PDField current : form.getFieldTree()) {
            if (!current.isTerminal() && !((PDNonTerminalField) current).hasChildren()) {
                LOG.info("Removing non terminal field with no child {}", current.getFullyQualifiedName());
                if (nonNull(current.getParent())) {
                    current.getParent().removeChild(current);
                } else {
                    // it's a root field
                    form.removeField(current);
                }
            } else if (clipSignature(current)) {
                form.setSignaturesExist(true);
            }
        }
        if (isBlank(form.getDefaultAppearance())) {
            form.setDefaultAppearance("/Helv 0 Tf 0 g ");
        }
        return form;
    }
}
