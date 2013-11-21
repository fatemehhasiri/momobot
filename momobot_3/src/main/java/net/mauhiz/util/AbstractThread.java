package net.mauhiz.util;

public abstract class AbstractThread extends AbstractNamedRunnable implements IDaemon {

    /**
    * si le thread tourne.
    */
    private boolean running;

    public AbstractThread(String name) {
        super(name);
    }

    @Override
    public ExecutionType getExecutionType() {
        return ExecutionType.PARALLEL_CACHED;
    }

    protected void handleInterruption(InterruptedException ie) {
        tstop();
        ThreadUtils.handleInterruption(ie);
    }

    /**
     * @see IDaemon#isRunning()
     */
    @Override
    public boolean isRunning() {
        return running;
    }

    /**
     * @see IDaemon#pause(long)
     */
    @Override
    public void pause(long duree) {
        if (!ThreadUtils.safeSleep(duree)) {
            tstop();
        }
    }

    @Override
    public void run() {
        running = true;
        super.run();
    }

    @Override
    public void setName(String name) {
        Thread.currentThread().setName(name);
    }

    /**
     * @deprecated call launch()
     */
    @Override
    @Deprecated
    public void tstart() {
        new Thread(this).start();
    }

    @Override
    public void tstop() {
        running = false;
    }
}