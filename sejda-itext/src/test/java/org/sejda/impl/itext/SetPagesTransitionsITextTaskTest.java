package org.sejda.impl.itext;

import org.sejda.core.manipulation.model.parameter.SetPagesTransitionParameters;
import org.sejda.core.manipulation.model.task.Task;
import org.sejda.core.manipulation.service.SetPagesTransitionsTaskTest;
import org.sejda.impl.itext.SetPagesTransitionTask;

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
