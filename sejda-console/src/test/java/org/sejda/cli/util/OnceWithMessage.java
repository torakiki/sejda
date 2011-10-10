/*
 * Created on Aug 31, 2011
 * Copyright 2010 by Eduard Weissmann (edi.weissmann@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.cli.util;

import org.mockito.exceptions.base.MockitoAssertionError;
import org.mockito.internal.verification.api.VerificationData;
import org.mockito.internal.verification.checkers.MissingInvocationChecker;
import org.mockito.internal.verification.checkers.NumberOfInvocationsChecker;
import org.mockito.verification.VerificationMode;
import org.sejda.core.exception.SejdaRuntimeException;

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