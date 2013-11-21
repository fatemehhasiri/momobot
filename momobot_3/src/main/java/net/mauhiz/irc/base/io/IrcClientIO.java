package net.mauhiz.irc.base.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import net.mauhiz.irc.base.IrcClientControl;
import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcPeer;
import net.mauhiz.irc.base.io.input.IIrcInput;
import net.mauhiz.irc.base.io.input.IrcInput;
import net.mauhiz.irc.base.io.output.IrcOutput;
import net.mauhiz.irc.base.msg.Nick;
import net.mauhiz.irc.base.msg.User;
import net.mauhiz.util.ThreadUtils;

import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public class IrcClientIO extends AbstractIrcIO {
    class SocketConnected implements CompletionHandler<Void, Void> {

        @Override
        public void completed(Void result, Void attachment) {
            output = new IrcOutput(sclient);
            output.launch();
            input = new IrcInput(IrcClientIO.this, sclient);
            input.launch();
            IIrcServerPeer server = getServerPeer();
            sendMsg(new Nick(server).getIrcForm());
            sendMsg(new User(server).getIrcForm());
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            LOG.error("could not connect to " + peer, exc);
            status = IOStatus.DISCONNECTED;
        }

    }

    static final Logger LOG = Logger.getLogger(IrcClientIO.class);
    protected IIrcInput input;

    protected AsynchronousSocketChannel sclient;

    public IrcClientIO(IrcClientControl control, IrcPeer server) {
        super(control, server);
    }

    public void connect() {
        status = IOStatus.CONNECTING;
        InetSocketAddress address = peer.getAddress();
        try {
            sclient = AsynchronousSocketChannel.open();
            sclient.connect(address, null, new SocketConnected());
        } catch (IOException ioe) {
            LOG.error("could not create socket to " + address, ioe);
            status = IOStatus.DISCONNECTED;
        }
    }

    /**
     * @see org.apache.commons.net.SocketClient#disconnect()
     */
    @Override
    public void disconnect() {
        status = IOStatus.DISCONNECTING;
        if (output != null) {
            output.tstop();
        }
        if (input != null) {
            input.tstop();
        }
        ThreadUtils.safeSleep(2_000);
        try {
            sclient.close();
        } catch (IOException ioe) {
            LOG.error(ioe, ioe);
        }

        status = IOStatus.DISCONNECTED;
    }

    @Override
    public IIrcServerPeer getServerPeer() {
        return (IIrcServerPeer) peer; // server is my partner.
    }

    public void reconnect() {
        disconnect();
        connect();
    }

    public IOStatus waitForConnection() {
        while (true) {
            if (status == IOStatus.CONNECTED || status == IOStatus.DISCONNECTED) {
                return status;
            }
            ThreadUtils.safeSleep(100);
        }
    }
}
