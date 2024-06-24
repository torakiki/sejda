package org.sejda.impl.sambox.component.pdfa;
/*
 * Created on 19/06/24
 * Copyright 2024 Sober Lemur S.r.l. and Sejda BV
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

import org.apache.commons.lang3.function.FailableConsumer;
import org.sejda.sambox.cos.COSDictionary;
import org.sejda.sambox.cos.COSStream;
import org.sejda.sambox.output.PreSaveCOSTransformer;

import java.io.IOException;

/**
 * @author Andrea Vacondio
 */
public class PDFA1BTransformer implements PreSaveCOSTransformer {

    private FailableConsumer<COSDictionary, IOException> dictionaryConsumer = t -> {
    };
    private FailableConsumer<COSStream, IOException> streamConsumer = t -> {
    };

    /**
     * Adds a consumer to the COSObjectsPreProcessor to be applied to a {@link COSDictionary}.
     */
    public void addDictionaryConsumer(FailableConsumer<COSDictionary, IOException> consumer) {
        dictionaryConsumer = dictionaryConsumer.andThen(consumer);
    }

    /**
     * Adds a consumer to the COSObjectsPreProcessor to be applied to a {@link COSStream}.
     */
    public void addStreamConsumer(FailableConsumer<COSStream, IOException> consumer) {
        streamConsumer = streamConsumer.andThen(consumer);
    }

    @Override
    public void visit(COSStream value) throws IOException {
        streamConsumer.accept(value);
    }

    @Override
    public void visit(COSDictionary value) throws IOException {
        dictionaryConsumer.accept(value);
    }
}
