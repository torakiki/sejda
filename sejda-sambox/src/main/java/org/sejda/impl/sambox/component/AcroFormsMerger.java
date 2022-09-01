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
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
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
import static org.sejda.sambox.cos.COSName.CA;
import static org.sejda.sambox.cos.COSName.CA_NS;
import static org.sejda.sambox.cos.COSName.CONTENTS;
import static org.sejda.sambox.cos.COSName.DATAPREP;
import static org.sejda.sambox.cos.COSName.DS;
import static org.sejda.sambox.cos.COSName.DV;
import static org.sejda.sambox.cos.COSName.F;
import static org.sejda.sambox.cos.COSName.FF;
import static org.sejda.sambox.cos.COSName.FT;
import static org.sejda.sambox.cos.COSName.H;
import static org.sejda.sambox.cos.COSName.I;
import static org.sejda.sambox.cos.COSName.KIDS;
import static org.sejda.sambox.cos.COSName.LANG;
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
import static org.sejda.sambox.pdmodel.interactive.form.PDFieldFactory.createField;

import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.sejda.commons.LookupTable;
import org.sejda.impl.sambox.util.AcroFormUtils;
import org.sejda.impl.sambox.util.FontUtils;
import org.sejda.model.pdf.form.AcroFormPolicy;
import org.sejda.sambox.cos.COSArray;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSName;
import org.sejda.sambox.pdmodel.PDDocument;
import org.sejda.sambox.pdmodel.font.PDFont;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotation;
import org.sejda.sambox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.sejda.sambox.pdmodel.interactive.form.PDAcroForm;
import org.sejda.sambox.pdmodel.interactive.form.PDField;
import org.sejda.sambox.pdmodel.interactive.form.PDFieldFactory;
import org.sejda.sambox.pdmodel.interactive.form.PDNonTerminalField;
import org.sejda.sambox.pdmodel.interactive.form.PDTerminalField;
import org.sejda.sambox.pdmodel.interactive.form.PDVariableText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component providing methods to merge multiple acroforms together using different strategies.
 * 
 * @author Andrea Vacondio
 */
public class AcroFormsMerger {
    private static final Logger LOG = LoggerFactory.getLogger(AcroFormsMerger.class);

    // when we separate field and widget items, we keep /AA in both, Acrobat Reader seems to work correctly only if that's the case
    private static final COSName[] FIELD_KEYS = { FT, PARENT, KIDS, T, TU, TM, FF, V, DV, Q, DS, RV, OPT, MAX_LEN, TI,
            I, LOCK, SV, DATAPREP };

    private static final COSName[] WIDGET_KEYS = { TYPE, SUBTYPE, RECT, CONTENTS, P, NM, M, F, AP, AS, BORDER, C,
            STRUCT_PARENT, OC, AF, CA, CA_NS, LANG, BM, H, MK, A, BS, PMD };

    private AcroFormPolicy policy;
    private PDAcroForm form;
    private String random = Long.toString(UUID.randomUUID().getMostSignificantBits(), 36);
    private Long counter = 0L;

    private final BiFunction<PDTerminalField, LookupTable<PDField>, PDTerminalField> createOrReuseTerminalField = (
            PDTerminalField existing, LookupTable<PDField> fieldsLookup) -> {
        PDField previouslyCreated = ofNullable(getMergedField(existing.getFullyQualifiedName()))
                .orElseGet(() -> fieldsLookup.lookup(existing));
        
        boolean shouldCreateNew = isNull(previouslyCreated);
        boolean shouldCreateNewAndRename = previouslyCreated != null &&
                (        
                    // different types (eg: checkbox vs text)
                    !previouslyCreated.getClass().equals(existing.getClass()) ||
                    // different values (eg: john vs jack)
                    !previouslyCreated.getValueAsString().equals(existing.getValueAsString())
                ); 

        if (shouldCreateNew || shouldCreateNewAndRename) {
            previouslyCreated = PDFieldFactory.createFieldAddingChildToParent(this.form,
                    existing.getCOSObject().duplicate(),
                    (PDNonTerminalField) fieldsLookup.lookup(existing.getParent()));
            
            if(shouldCreateNewAndRename) {
                LOG.warn("Cannot merge terminal field because another field with the same name but different value already exists: {}",
                        existing.getFullyQualifiedName());

                previouslyCreated.setPartialName(String.format("%s%s%d", removeDotsIfAny(existing.getPartialName()),
                        random, ++counter));
            }

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
            String partialName = removeDotsIfAny(existing.getPartialName());
            newField.setPartialName(String.format("%s%s%d", partialName, random, ++counter));
            LOG.info("Existing terminal field renamed from {} to {}", partialName, newField.getPartialName());
        }
        newField.getCOSObject().removeItem(COSName.KIDS);
        fieldsLookup.addLookupEntry(existing, newField);
        return newField;
    };

    // dots are not allowed in the partial name, but still broken documents may exist
    private static String removeDotsIfAny(String s) {
        if(s == null) {
            return null;
        }
        return s.replace(".", "");
    }

    private final BiConsumer<PDField, LookupTable<PDField>> createOrReuseNonTerminalField = (PDField existing,
            LookupTable<PDField> fieldsLookup) -> {
                // do we aready have a lookup for this?
                if (!fieldsLookup.hasLookupFor(existing)) {
            PDField mergedField = getMergedField(existing.getFullyQualifiedName());
            // we don't have a lookup but do we have a merged field with the same name (from a previous document)?
            if (isNull(mergedField)) {
                mergedField = PDFieldFactory.createFieldAddingChildToParent(this.form,
                        existing.getCOSObject().duplicate(),
                        (PDNonTerminalField) fieldsLookup.lookup(existing.getParent()));
                mergedField.getCOSObject().removeItem(COSName.KIDS);
            } else if (mergedField.isTerminal()) {
                mergedField = PDFieldFactory.createFieldAddingChildToParent(this.form,
                        existing.getCOSObject().duplicate(),
                        (PDNonTerminalField) fieldsLookup.lookup(existing.getParent()));
                mergedField.getCOSObject().removeItem(COSName.KIDS);
                mergedField.setPartialName(String.format("%s%s%d", removeDotsIfAny(existing.getPartialName()), 
                        random, ++counter));
                LOG.warn("Cannot reuse merged terminal field {} as a non terminal field, renaming it to {}",
                        existing.getPartialName(), mergedField.getPartialName());
            }
            fieldsLookup.addLookupEntry(existing, mergedField);
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
            newField.setPartialName(String.format("%s%s%d", removeDotsIfAny(field.getPartialName()), random, ++counter));
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
        annotations.stream().map(PDAnnotation::getCOSObject).forEach(a -> {
            a.removeItems(FIELD_KEYS);
            // if multiple kids we preserve their /DA even if Widget shouldn't have DA according to specs because if there is one Acrobat honors it.
            // if only one kid then /DA is in the field. see for a live example PDFBOX-3687
            if (annotations.size() == 1) {
                a.removeItem(COSName.DA);
            }
        });
        LOG.trace("Removed fields keys from widget annotations");
    }

    private void updateForm(PDAcroForm originalForm, LookupTable<PDAnnotation> annotationsLookup,
            BiFunction<PDTerminalField, LookupTable<PDField>, PDTerminalField> getTerminalField,
            BiConsumer<PDField, LookupTable<PDField>> createNonTerminalField) {
        AcroFormUtils.mergeDefaults(originalForm, form);
        LookupTable<PDField> fieldsLookup = new LookupTable<>();
        Set<PDAnnotationWidget> allRelevantWidgets = annotationsLookup.keys().stream()
                .filter(a -> a instanceof PDAnnotationWidget).map(a -> (PDAnnotationWidget) a).collect(toSet());
        Set<PDField> rootFields = new HashSet<>();

        // it must be a pre order visit because we have to process non terminal first otherwise terminal ones won't get a parent
        // every widget we meet is removed from the allRelevantWidgets so we can identify widgets not referenced by the originalForm
        originalForm.getFieldTree().stream().forEach(
                f -> mergeField(f, annotationsLookup, getTerminalField, createNonTerminalField, fieldsLookup,
                        of(allRelevantWidgets::remove)));
        // keep track of the root fields
        originalForm.getFields().stream().map(fieldsLookup::lookup).filter(Objects::nonNull).forEach(rootFields::add);

        if (!allRelevantWidgets.isEmpty()) {
            LOG.info("Found relevant widget annotations ({}) not linked to the form", allRelevantWidgets.size());
            // we process those widget annotations not referenced by the originalForm acroform (ex. empty fields array)
            PDAcroForm dummy = new PDAcroForm(null);
            allRelevantWidgets.forEach(w -> {
                COSDictionary currentField = w.getCOSObject();
                // if there's a hierarchy we walk up to find the root
                while (nonNull(currentField.getDictionaryObject(COSName.PARENT, COSDictionary.class))) {
                    currentField = currentField.getDictionaryObject(COSName.PARENT, COSDictionary.class);
                }
                // we add it as root to a dummy form so we can reuse its tree visit logic
                dummy.addFields(List.of(createField(originalForm, currentField, null)));
            });

            dummy.getFieldTree().stream().forEach(f -> mergeField(f, annotationsLookup, getTerminalField,
                    createNonTerminalField, fieldsLookup, empty()));
            // keep track of the root fields
            dummy.getFields().stream().map(fieldsLookup::lookup).filter(Objects::nonNull).forEach(rootFields::add);
        }
        List<PDField> currentRoots = this.form.getFields();
        // add only if not there already
        this.form.addFields(rootFields.stream().filter(f -> !currentRoots.contains(f)).collect(toList()));
        mergeCalculationOrder(originalForm, fieldsLookup);
    }

    private void mergeField(PDField field, LookupTable<PDAnnotation> annotationsLookup,
            BiFunction<PDTerminalField, LookupTable<PDField>, PDTerminalField> getTerminalField,
            BiConsumer<PDField, LookupTable<PDField>> createNonTerminalField, LookupTable<PDField> fieldsLookup,
            Optional<Consumer<PDAnnotationWidget>> onProcessedWidget) {
        if (!field.isTerminal()) {
            createNonTerminalField.accept(field, fieldsLookup);
        } else {
            List<PDAnnotationWidget> relevantWidgets = findMappedWidgetsFor((PDTerminalField) field, annotationsLookup);
            if (!relevantWidgets.isEmpty()) {
                PDTerminalField terminalField = getTerminalField.apply((PDTerminalField) field, fieldsLookup);
                if (nonNull(terminalField)) {
                    removeFieldKeysFromWidgets(relevantWidgets);
                    for (PDAnnotationWidget widget : relevantWidgets) {
                        terminalField.addWidgetIfMissing(widget);
                        onProcessedWidget.ifPresent(c -> field.getWidgets().forEach(c));
                    }
                    terminalField.getCOSObject().removeItems(WIDGET_KEYS);
                }
            } else {
                LOG.debug("Discarded not relevant field {}", field.getFullyQualifiedName());
            }
        }
    }

    private void mergeCalculationOrder(PDAcroForm originalForm, LookupTable<PDField> fieldsLookup) {
        List<PDField> co = originalForm.getCalculationOrder().stream().map(fieldsLookup::lookup)
                .filter(Objects::nonNull).toList();
        if (co.size() > 0) {
            COSArray formCo = ofNullable(
                    this.form.getCOSObject().getDictionaryObject(COSName.CO, COSArray.class)).orElseGet(COSArray::new);
            for (PDField field : co) {
                formCo.add(field);
            }
            this.form.setCalculationOrder(formCo);
        }
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

                if (field instanceof PDVariableText) {
                    ensureValueCanBeDisplayed((PDVariableText) field);
                }
            }
            form.flatten(fields, form.isNeedAppearances());
        } catch (IOException | UnsupportedOperationException ex) {
            LOG.warn("Failed to flatten form", ex);
        }
    }

    /**
     * Makes sure the string can be displayed using appearances font
     * 
     * @throws IOException
     */
    private void ensureValueCanBeDisplayed(PDVariableText field) throws IOException {
        String value = field.getValueAsString();
        if (!FontUtils.canDisplay(value, field.getAppearanceFont())) {
            PDFont fallbackFont = FontUtils.findFontFor(form.getDocument(), value);
            field.setAppearanceOverrideFont(fallbackFont);
            // we updated the field, let's generate a new appearance
            field.applyChange();
            LOG.debug("Form field can't render (in appearances) it's value '{}', will use font {} for better support",
                    value, fallbackFont);
        }
    }

    /**
     * 
     * @return Performs some cleanup task on the resulting {@link PDAcroForm} and then returns it
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
