package net.mauhiz.irc.base.data;

import java.net.InetSocketAddress;

public abstract class AbstractPeer implements IrcPeer {

    protected final InetSocketAddress hostPort;

    protected AbstractPeer(InetSocketAddress hostPort) {
        this.hostPort = hostPort;
    }

    /**
     * @see net.mauhiz.irc.base.data.IrcPeer#getAddress()
     */
    @Override
    public InetSocketAddress getAddress() {
        return hostPort;
    }

    @Override
    public String toString() {
        return hostPort.toString();
    }
}
