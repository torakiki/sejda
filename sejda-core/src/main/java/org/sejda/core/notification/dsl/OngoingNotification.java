/*
 * Created on 01/giu/2010
 *
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
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
package org.sejda.core.notification.dsl;

import java.math.BigDecimal;

/**
 * DSL interface to complete an ongoing "steps completed" notification.
 * 
 * @author Andrea Vacondio
 * 
 */
public interface OngoingNotification {

    /**
     * @param total
     *            the total number of steps
     */
    void outOf(int total);

    /**
     * @param total
     *            the total number of steps
     */
    void outOf(BigDecimal total);
}
