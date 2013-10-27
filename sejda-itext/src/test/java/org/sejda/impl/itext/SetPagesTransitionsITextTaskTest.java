package org.sejda.impl.itext;

import org.sejda.core.service.SetPagesTransitionsTaskTest;
import org.sejda.model.parameter.SetPagesTransitionParameters;
import org.sejda.model.task.Task;

/**
 * 
 * @author Andrea Vacondio
 * 
 */
public class SetPagesTransitionsITextTaskTest extends SetPagesTransitionsTaskTest {

    public Task<SetPagesTransitionParameters> getTask() {
        return new SetPagesTransitionTask();
    }

}
