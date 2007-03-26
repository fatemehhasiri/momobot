package ircbot.ident;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import utils.MyRunnable;

/**
 * A simple IdentServer (also know as "The Identification Protocol"). An ident
 * server provides a means to determine the identity of a user of a particular
 * TCP connection.
 * <p>
 * Most IRC servers attempt to contact the ident server on connecting hosts in
 * order to determine the user's identity. A few IRC servers will not allow you
 * to connect unless this information is provided.
 */
public class IdentServer extends MyRunnable {
    /**
     * logger.
     */
    private static final Logger LOG  = Logger.getLogger(IdentServer.class);
    /**
     * le port sur lequel on �coute.
     */
    private static final int    PORT = 113;
    /**
     * le login � faire passer.
     */
    private final String        login;
    /**
     * le serversocket.
     */
    private ServerSocket        ss   = null;

    /**
     * Constructs and starts an instance of an IdentServer that will respond to
     * a client with the provided login. Rather than calling this constructor
     * explicitly from your code, it is recommended that you use the
     * startIdentServer method in the PircBot class.
     * <p>
     * The ident server will wait for up to 60 seconds before shutting down.
     * Otherwise, it will shut down as soon as it has responded to an ident
     * request.
     * @param login1
     *            The login that the ident server will respond with.
     */
    public IdentServer(final String login1) {
        this.login = login1;
        try {
            this.ss = new ServerSocket(PORT);
            this.ss.setSoTimeout((int) TimeUnit.MILLISECONDS.convert(1,
                    TimeUnit.MINUTES));
        } catch (final Exception e) {
            LOG.error(e, e);
            return;
        }
    }

    /**
     * Waits for a client to connect to the ident server before making an
     * appropriate response. Note that this method is started by the class
     * constructor.
     */
    @Override
    public final void run() {
        setRunning(true);
        if (LOG.isInfoEnabled()) {
            LOG.info("server running on port " + PORT
                    + " for the next 60 seconds...");
        }
        try {
            final Socket socket = this.ss.accept();
            socket.setSoTimeout((int) TimeUnit.MILLISECONDS.convert(1,
                    TimeUnit.MINUTES));
            final PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream()), true);
            final String line = new BufferedReader(new InputStreamReader(socket
                    .getInputStream())).readLine();
            if (line != null) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("request received: " + line);
                }
                IOUtils.write(line + " : USERID : UNIX : " + this.login
                        + "\r\n", writer);
                if (LOG.isInfoEnabled()) {
                    LOG.info(">> " + line);
                }
            }
            IOUtils.closeQuietly(writer);
            this.ss.close();
        } catch (final IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error(e, e);
            }
        }
        setRunning(false);
        if (LOG.isInfoEnabled()) {
            LOG.info("server has been shut down.");
        }
    }
}
