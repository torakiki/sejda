/*
 * Created on Aug 31, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * This file is part of the Sejda source code
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sejda.cli.util;

import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.internal.verification.checkers.MissingInvocationChecker;
import org.mockito.internal.verification.checkers.NumberOfInvocationsChecker;
import org.mockito.verification.VerificationMode;
import org.sejda.model.exception.SejdaRuntimeException;

/**
 * Custom Mockito verification mode that allows specifying a custom failure message
 * 
 * @author Eduard Weissmann
 * 
 */
public class OnceWithMessage implements VerificationMode {
    private final String failureDescribingMessage;
    private final int wantedCount = 1;

    public OnceWithMessage(String failureDescribingMessage) {
        this.failureDescribingMessage = failureDescribingMessage;
    }

    @Override
    public void verify(VerificationData data) {
        try {
            if (wantedCount > 0) {
                MissingInvocationChecker missingInvocation = new MissingInvocationChecker();
                missingInvocation.check(data.getAllInvocations(), data.getWanted());
            }
            NumberOfInvocationsChecker numberOfInvocations = new NumberOfInvocationsChecker();
            numberOfInvocations.check(data.getAllInvocations(), data.getWanted(), wantedCount);
        } catch (MockitoAssertionError mae) {
            throw new SejdaRuntimeException(failureDescribingMessage, mae);
        }
    }

}