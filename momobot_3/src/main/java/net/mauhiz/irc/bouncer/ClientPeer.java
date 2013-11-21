package net.mauhiz.irc.bouncer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

import net.mauhiz.irc.base.data.AbstractPeer;
import net.mauhiz.irc.base.data.IrcNetwork;
import net.mauhiz.irc.base.data.Target;

import org.apache.commons.lang.NotImplementedException;

public class ClientPeer extends AbstractPeer implements Target {
    private final BncServer server;

    public ClientPeer(AsynchronousSocketChannel socket, BncServer server) throws IOException {
        super((InetSocketAddress) socket.getRemoteAddress());
        this.server = server;
    }

    @Override
    public String getIrcForm() {
        throw new NotImplementedException("TODO");
    }

    @Override
    public IrcNetwork getNetwork() {
        return server;
    }
}
