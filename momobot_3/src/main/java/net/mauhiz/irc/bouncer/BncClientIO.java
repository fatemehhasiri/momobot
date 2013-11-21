package net.mauhiz.irc.bouncer;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

import net.mauhiz.irc.base.ColorUtils;
import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcDecoder;
import net.mauhiz.irc.base.data.Target;
import net.mauhiz.irc.base.io.AbstractIrcIO;
import net.mauhiz.irc.base.io.IOStatus;
import net.mauhiz.irc.base.io.input.IIrcInput;
import net.mauhiz.irc.base.msg.IIrcMessage;
import net.mauhiz.irc.base.msg.Nick;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.msg.ServerMsg;
import net.mauhiz.util.ThreadUtils;

import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public class BncClientIO extends AbstractIrcIO {
    private static final Logger LOG = Logger.getLogger(BncClientIO.class);
    private Account account;
    private String nick;
    private final BncServerConnection server;
    private final AsynchronousSocketChannel socket;

    protected BncClientIO(BncClientControl ircControl, AsynchronousSocketChannel socket, ClientPeer peer,
            BncServerConnection server) {
        super(ircControl, peer);
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void disconnect() {
        status = IOStatus.DISCONNECTING;
        if (output != null) {
            output.tstop();
            ThreadUtils.safeSleep(2_000);
        }
        try {
            socket.close();
        } catch (IOException ioe) {
            LOG.error(ioe, ioe);
        }
        status = IOStatus.DISCONNECTED;
    }

    private String getHostName() {
        try {
            return "from " + socket.getRemoteAddress().toString();
        } catch (IOException ioe) {
            LOG.error("Could not read remote address", ioe);
            return "";
        }
    }

    // private long connectionTime = System.currentTimeMillis();

    @Override
    public IIrcServerPeer getServerPeer() {
        return null; // myself
    }

    @Override
    public void processMsg(String raw) {
        if (status == IOStatus.CONNECTING) {
            IIrcMessage msg = IrcDecoder.INSTANCE.buildFromRaw(null, raw);

            if (nick == null) {
                if (msg instanceof Nick) {
                    nick = ((Nick) msg).getNewNick();
                    sendGreetings(server.mySelf, server.serverData, msg.getServerPeer());
                } else {
                    super.processMsg(raw);
                }
            } else { // waiting for login/password
                if (msg instanceof Privmsg) {
                    readAccount((Privmsg) msg);
                    if (account != null) {
                        status = IOStatus.CONNECTED;
                    }
                }
            }
        } else {
            super.processMsg(raw);
        }
    }

    private void readAccount(Privmsg pmsg) {
        Target target = pmsg.getTo();
        Target myself = server.mySelf;

        if (myself.equals(target)) {
            String content = pmsg.getMessage();
            String[] parts = content.split("\\s+");
            if (parts.length == 2) {
                String login = parts[0];
                String password = parts[1];
                Account acc = server.getAccountStore().getFor(login, password);
                if (acc != null) {
                    account = acc;
                    acc.getRelatedManager().currentlyConnected.add(this);
                    LOG.info(acc.getUsername() + " logged in successfully " + getHostName());
                    return;
                }

                LOG.info("Failed login attempt " + getHostName() + " (" + login + "/" + password + ")");
            }
            NoticeAuth pasBon = new NoticeAuth(pmsg.getServerPeer(), "Invalid login or password.");
            sendMsg(pasBon.getIrcForm());
        }
    }

    void sendGreetings(Target myNick, BncServer nw, IIrcServerPeer bncServer) {
        // TODO review this stuff
        ServerMsg smsg1 = new ServerMsg(bncServer, myNick, 1, null, "Welcome to MomoBouncer");
        sendMsg(smsg1.getIrcForm());

        ServerMsg smsg2 = new ServerMsg(bncServer, myNick, 2, null,
                "This is an IRC proxy/bouncer. Unauthorized users must disconnect immediately.");
        sendMsg(smsg2.getIrcForm());

        ServerMsg smsg3 = new ServerMsg(bncServer, myNick, 3, null, "This bouncer has been up since ??");
        // TODO smth like server.getGlobalStartTime()
        sendMsg(smsg3.getIrcForm());

        ServerMsg smsg4 = new ServerMsg(bncServer, myNick, 4, null, "");
        sendMsg(smsg4.getIrcForm());

        ServerMsg smsg5 = new ServerMsg(bncServer, myNick, 5, null, "");
        sendMsg(smsg5.getIrcForm());

        NoticeAuth na1 = new NoticeAuth(bncServer, "Welcome to JBouncer. http://www.jibble.org/jbouncer/");
        sendMsg(na1.getIrcForm());

        NoticeAuth na2 = new NoticeAuth(bncServer,
                "WThis is an IRC proxy/bouncer. Unauthorized users must disconnect immediately.");
        sendMsg(na2.getIrcForm());

        NoticeAuth na3 = new NoticeAuth(bncServer, "To connect, tell me your password by typing like: "
                + ColorUtils.toBold(ColorUtils.toUnderline("login") + " " + ColorUtils.toUnderline("password")));
        sendMsg(na3.getIrcForm());
    }

    public void tstart() {
        output = new BncClientOutput(socket);
        output.launch();

        IIrcInput input = new BncClientInput(this, socket);
        input.launch();
    }
}
