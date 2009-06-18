package net.mauhiz.irc.base;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.mauhiz.util.AbstractRunnable;
import net.mauhiz.util.FileUtil;

import org.apache.commons.lang.StringUtils;

/**
 * @author mauhiz
 */
public class IrcOutput extends AbstractRunnable implements IIrcOutput {
    /**
     * antiflood en ms
     */
    private static final long DELAY = 100;
    private static final int MAX_SIZE = 50;
    private static final int MAXLEN = 255;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>(MAX_SIZE);
    private final PrintWriter writer;
    
    /**
     * @param socket
     * @throws IOException
     */
    IrcOutput(Socket socket) throws IOException {
        super();
        writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), FileUtil.ISO8859_15), true);
    }
    /**
     * @see net.mauhiz.irc.base.IIrcOutput#isReady()
     */
    public boolean isReady() {
        return isRunning() && queue.size() <= MAX_SIZE;
    }
    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while (isRunning()) {
            String toWrite = queue.poll();
            pause(DELAY);
            if (toWrite == null) {
                continue;
            }
            LOG.info(">> " + toWrite);
            writer.println(toWrite);
        }
        writer.close();
    }
    /**
     * @see net.mauhiz.irc.base.IIrcOutput#sendRawMsg(java.lang.String)
     */
    public void sendRawMsg(String raw) {
        if (raw == null) {
            return;
        } else if (StringUtils.isBlank(raw)) {
            LOG.warn("Tried to send empty msg", new IllegalArgumentException());
        }
        
        String trimmedRaw = raw.length() > MAXLEN ? raw.substring(0, MAXLEN) : raw;
        
        try {
            queue.put(trimmedRaw);
        } catch (InterruptedException e) {
            stop();
            AbstractRunnable.handleInterruption(e);
        }
    }
    
    /**
     * @see net.mauhiz.irc.base.IIrcOutput#start()
     */
    @Override
    public void start() {
        startAs("Output Thread");
    }
}
