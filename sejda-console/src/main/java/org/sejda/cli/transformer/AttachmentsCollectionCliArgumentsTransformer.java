/*
 * Created on 25 gen 2016
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
package org.sejda.cli.transformer;

import org.sejda.cli.model.AttachmentsCollectionTaskCliArguments;
import org.sejda.conversion.FileSourceAdapter;
import org.sejda.model.parameter.AttachmentsCollectionParameters;

/**
 * a transformer for the attachment collection task
 * 
 * @author Andrea Vacondio
 *
 */
public class AttachmentsCollectionCliArgumentsTransformer extends BaseCliArgumentsTransformer implements
        CommandCliArgumentsTransformer<AttachmentsCollectionTaskCliArguments, AttachmentsCollectionParameters> {

    @Override
    public AttachmentsCollectionParameters toTaskParameters(AttachmentsCollectionTaskCliArguments taskCliArguments) {
        AttachmentsCollectionParameters parameters = new AttachmentsCollectionParameters();
        populateAbstractParameters(parameters, taskCliArguments);
        for (FileSourceAdapter eachAdapter : taskCliArguments.getFiles()) {
            parameters.addSource(eachAdapter.getSource());
        }
        populateOutputTaskParameters(parameters, taskCliArguments);
        parameters.setInitialView(taskCliArguments.getInitialView().getEnumValue());
        return parameters;
    }

}
