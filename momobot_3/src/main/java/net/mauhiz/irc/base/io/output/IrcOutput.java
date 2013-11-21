package net.mauhiz.irc.base.io.output;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.mauhiz.util.AbstractDaemon;
import net.mauhiz.util.FileUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public class IrcOutput extends AbstractDaemon implements IIrcOutput, Closeable {
    /**
     * antiflood en ms
     */
    private static final long DELAY = 100;
    private static final Logger LOG = Logger.getLogger(IrcOutput.class);
    private static final int MAX_SIZE = 50;
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>(MAX_SIZE);
    private final AsynchronousSocketChannel socket;

    /**
     * @param socket
     */
    public IrcOutput(AsynchronousSocketChannel socket) {
        super("IRC Output");
        this.socket = socket;
    }

    @Override
    public void close() throws IOException {
        try (AsynchronousSocketChannel toClose = socket) {
        }
    }

    /**
     * @see net.mauhiz.irc.base.io.output.IIrcOutput#isReady()
     */
    @Override
    public boolean isReady() {
        return isRunning() && queue.size() <= MAX_SIZE;
    }

    /**
     * @see net.mauhiz.irc.base.io.output.IIrcOutput#sendRawMsg(java.lang.String)
     */
    @Override
    public void sendRawMsg(String raw) {
        if (raw == null) {
            return;
        } else if (StringUtils.isBlank(raw)) {
            LOG.warn("Tried to send empty msg", new IllegalArgumentException());
        }
        try {
            queue.put(raw);
        } catch (InterruptedException ie) {
            handleInterruption(ie);
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void trun() {
        while (isRunning()) {
            try {
                String toWrite = queue.take();

                if (toWrite != null) {
                    write(toWrite);
                    pause(DELAY);
                }
            } catch (InterruptedException ie) {
                handleInterruption(ie);
            }
        }
        tstop();
    }
    
    @Override
    public void tstop() {
        super.tstop();
        try {
            close();
        } catch (IOException ioe) {
            LOG.warn(ioe, ioe);
        }
    }

    private void write(String toWrite) {
        LOG.debug(">> " + toWrite);
        ByteBuffer bytesToWrite = FileUtil.ISO8859_15.encode(CharBuffer.wrap(toWrite + "\r\n"));
        socket.write(bytesToWrite, null, new BytesWritten(bytesToWrite.limit(), this));
    }
}
