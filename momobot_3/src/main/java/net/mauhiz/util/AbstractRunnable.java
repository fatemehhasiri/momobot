package net.mauhiz.util;

/**
 * @author mauhiz
 */
public abstract class AbstractRunnable implements IRunnable {

    @Override
    public String getName() {
        return getClass().getName();
    }

    /**
     * Use this method to launch without stop control.
     */
    @Override
    public void launch() {
        ThreadManager.launch(this);
    }

    @Override
    public void run() {
        // change worker thread name
        Thread currentThread = Thread.currentThread();
        String nameBak = currentThread.getName();
        currentThread.setName(getName());
        trun();
        currentThread.setName(nameBak);
    }

    protected abstract void trun();
}
