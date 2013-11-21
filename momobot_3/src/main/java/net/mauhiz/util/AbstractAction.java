package net.mauhiz.util;

import java.awt.event.ActionEvent;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;

/**
 * This class is an action for both SWT and AWT/Swing
 * @author mauhiz
 */
public abstract class AbstractAction extends AbstractRunnable implements IAction, ISwtRunnable {

    @Override
    public void actionPerformed(ActionEvent e) {
        launch();
    }

    /**
     * Use this method to launch without stop control.
     * @param display
     */
    @Override
    public void launch(Display display) {
        ThreadManager.launch(this, display);
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent arg0) {
        // nothing
    }

    @Override
    public void widgetSelected(SelectionEvent arg0) {
        launch(arg0.display);
    }
}
