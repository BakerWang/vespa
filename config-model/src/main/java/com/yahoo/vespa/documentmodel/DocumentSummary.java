// Copyright 2016 Yahoo Inc. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.vespa.documentmodel;

import com.yahoo.document.Field;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A document summary definition - a list of summary fields.
 *
 * @author bratseth
 */
public class DocumentSummary extends FieldView {

    /**
     * Creates a DocumentSummary with the given name.
     * 
     * @param name The name to use for this summary.
     */
    public DocumentSummary(String name) {
        super(name);
    }

    /**
     * The model is constrained to ensure that summary fields of the same name
     * in different classes have the same summary transform, because this is
     * what is supported by the backend currently.
     * 
     * @param summaryField The summaryfield to add
     */
    public void add(SummaryField summaryField) {
        summaryField.addDestination(getName());
        super.add(summaryField);
    }

    public SummaryField getSummaryField(String name) {
        return (SummaryField) get(name);
    }

    public Collection<SummaryField> getSummaryFields() {
        ArrayList<SummaryField> fields = new ArrayList<>(getFields().size());
        for(Field f : getFields()) {
            fields.add((SummaryField) f);
        }
        return fields;
    }

    /**
     * Removes implicit fields which shouldn't be included.
     * This is implicitly added fields which are sources for
     * other fields. We then assume they are not intended to be added
     * implicitly in addition.
     * This should be called when this summary is complete.
     */
    public void purgeImplicits() {
        List<SummaryField> falseImplicits = new ArrayList<>();
        for (SummaryField summaryField : getSummaryFields() ) {
            if (summaryField.isImplicit()) continue;
            for (Iterator<SummaryField.Source> j = summaryField.sourceIterator(); j.hasNext(); ) {
                String sourceName = j.next().getName();
                if (sourceName.equals(summaryField.getName())) continue;
                SummaryField sourceField=getSummaryField(sourceName);
                if (sourceField==null) continue;
                if (!sourceField.isImplicit()) continue;
                falseImplicits.add(sourceField);
            }
        }
        for (SummaryField field : falseImplicits) {
            remove(field.getName());
        }
    }

    public String toString() {
        return "document summary '" + getName() + "'";
    }

}
