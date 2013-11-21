package net.mauhiz.irc.base;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.mauhiz.irc.MomoStringUtils;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IChannelProperties;
import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcNetwork;
import net.mauhiz.irc.base.data.IrcUser;
import net.mauhiz.irc.base.data.Topic;
import net.mauhiz.irc.base.data.UserChannelMode;
import net.mauhiz.irc.base.data.WhoisRequest;
import net.mauhiz.irc.base.ident.IdentServer;
import net.mauhiz.irc.base.io.IIrcIO;
import net.mauhiz.irc.base.io.IOStatus;
import net.mauhiz.irc.base.io.IrcClientIO;
import net.mauhiz.irc.base.msg.IIrcMessage;
import net.mauhiz.irc.base.msg.IPrivateIrcMessage;
import net.mauhiz.irc.base.msg.Join;
import net.mauhiz.irc.base.msg.Kick;
import net.mauhiz.irc.base.msg.Mode;
import net.mauhiz.irc.base.msg.Nick;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.msg.NumericReplies;
import net.mauhiz.irc.base.msg.Part;
import net.mauhiz.irc.base.msg.Ping;
import net.mauhiz.irc.base.msg.Pong;
import net.mauhiz.irc.base.msg.Quit;
import net.mauhiz.irc.base.msg.ServerError;
import net.mauhiz.irc.base.msg.ServerMsg;
import net.mauhiz.irc.base.msg.SetTopic;
import net.mauhiz.irc.base.trigger.ITriggerManager;
import net.mauhiz.util.UtfChar;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public class IrcClientControl extends AbstractIrcControl implements IIrcClientControl {
    /**
     * logger.
     */
    private static final Logger LOG = Logger.getLogger(IrcClientControl.class);

    /**
     * key = {@link IIrcServerPeer}, value = {@link IIrcIO}.
     */
    private final Map<IIrcServerPeer, IIrcIO> serverToIo = new HashMap<>();

    public IrcClientControl(ITriggerManager... managers) {
        super(managers);
    }

    /**
     * @see net.mauhiz.irc.base.IIrcControl#close()
     */
    @Override
    public void close() {
        Collection<IIrcIO> ios = serverToIo.values();
        for (IIrcIO io : ios) {
            io.disconnect();
        }
        serverToIo.clear();
    }

    @Override
    public boolean connect(IIrcServerPeer peer) {
        IIrcIO existing = serverToIo.get(peer);
        if (existing != null) {
            /* already connected */
            return existing.getStatus() == IOStatus.CONNECTED;
        }

        IrcUser myself = peer.getMyself();
        if (myself == null) {
            throw new IllegalArgumentException("I have no name. Who am I?");
        }
        new IdentServer(myself).launch();
        IrcClientIO ircio = new IrcClientIO(this, peer);
        ircio.connect();
        serverToIo.put(peer, ircio);
        return ircio.waitForConnection() == IOStatus.CONNECTED;
    }

    protected void handleKick(Kick kick) {
        IIrcServerPeer serverPeer = kick.getServerPeer();
        IrcChannel chan = kick.getChan();
        IrcUser target = kick.getTarget();
        chan.remove(target);
        if (target.equals(serverPeer.getMyself())) {
            serverPeer.getNetwork().remove(chan);
        }
    }

    protected void handleNamReply(ServerMsg msg) {
        IrcNetwork network = msg.getServerPeer().getNetwork();
        ArgumentList args = msg.getArgs();
        args.poll(); // =
        String chanName = args.poll();
        IrcChannel chan = network.findChannel(chanName);
        if (chan == null) {
            return;
        }

        ArgumentList prefixedNames = new ArgumentList(msg.getMsg());
        for (String prefixedName : prefixedNames) {
            UtfChar prefix = UtfChar.charAt(prefixedName, 0);
            if (UserChannelMode.isDisplay(prefix)) {
                prefixedName = prefixedName.substring(1);
            }
            IrcUser next = network.findUser(prefixedName, true);
            chan.add(next);
            chan.getModes(next).add(UserChannelMode.fromDisplay(prefix));
        }
        LOG.debug("Names Reply on " + chan + ": " + prefixedNames);
    }

    private void handleNickInUse(ServerMsg message) {
        ArgumentList args = message.getArgs();
        String nickInUse = args.peek();
        LOG.warn(nickInUse + " is already in use");

        // TODO do nothing if already have a valid nickname
        // TODO use alternate nicknames from config ?
        String newNick = nickInUse + "_";
        IIrcServerPeer server = message.getServerPeer();
        sendMsg(new Nick(server, null, newNick));
        IrcUser oldMyself = server.getMyself();
        message.getServerPeer().introduceMyself(newNick, oldMyself.getMask().getUser(), oldMyself.getFullName());
    }

    protected MsgState handleNotice(Notice notice, IIrcIO io) {
        if (io != null && io.getStatus() == IOStatus.CONNECTING) {
            if (notice.getFrom() != null) {
                IIrcServerPeer serverPeer = notice.getServerPeer();
                IrcNetwork network = serverPeer.getNetwork();
                io.setStatus(IOStatus.CONNECTED);
                LOG.info("connected to " + network.getAlias());
            }
            /* dont let it be processed */
            return MsgState.NO_MORE_PROCESSING;
        }
        return MsgState.AVAILABLE;
    }

    protected void handleQuit(Quit message) {
        IrcUser quitter = (IrcUser) message.getFrom();
        if (quitter != null) {
            IrcNetwork network = message.getServerPeer().getNetwork();
            for (IrcChannel every : network.getChannels()) {
                every.remove(quitter);
            }
            network.remove(quitter);
        }
    }

    protected void handleRplList(ServerMsg message) {
        IIrcServerPeer peer = message.getServerPeer();
        IrcNetwork nw = peer.getNetwork();
        String chanName = message.getArgs().poll();
        // for now, ignore next arg which is channel length
        IrcChannel chan = nw.findChannel(chanName, true);
        ArgumentList chanDetails = new ArgumentList(message.getMsg());
        String modes = chanDetails.peek();
        if (StringUtils.isNotEmpty(modes) && modes.endsWith("]") && UtfChar.charAt(modes, 0).isEquals(']')) {
            chanDetails.poll();
            processMode(new Mode(peer, peer, chan, new ArgumentList(modes)));
        }
        String topic = chanDetails.getRemainingData();
        IChannelProperties props = chan.getProperties();
        if (props.getTopic() == null) {
            props.setTopic(new Topic(null, null, topic));
        } else {
            props.getTopic().setValue(topic);
        }

    }

    protected void handleRplTopic(ServerMsg rplTopic) {
        IrcNetwork network = rplTopic.getServerPeer().getNetwork();
        String chanName = rplTopic.getArgs().peek();
        String topic = rplTopic.getMsg();
        LOG.debug("topic set on " + chanName + " : " + topic);
        IrcChannel chan = network.findChannel(chanName);
        if (chan != null) {
            chan.getProperties().getTopic().setValue(topic);
        }
    }

    protected void handleWhoisChannels(ServerMsg whoisChannels) {
        IrcNetwork network = whoisChannels.getServerPeer().getNetwork();
        String nick = whoisChannels.getArgs().peek();
        IrcUser ircUser = network.findUser(nick, true);

        ArgumentList chanNames = new ArgumentList(whoisChannels.getMsg());
        for (String chanName : chanNames) {
            if (!MomoStringUtils.isChannelName(chanName)) {
                chanName = chanName.substring(1);
            }
            IrcChannel channel = network.findChannel(chanName, false); // osef des infos qui sont pas sur mon chan
            if (channel != null) {
                channel.add(ircUser);
            }
        }
    }

    protected void handleWhoisUser(ServerMsg whoisUser) {
        ArgumentList args = whoisUser.getArgs();
        String nick = args.poll();
        IrcUser ircUser = whoisUser.getServerPeer().getNetwork().findUser(nick, true);
        ircUser.getMask().setUser(args.poll());
        ircUser.getMask().setHost(args.poll());
        ircUser.setFullName(whoisUser.getMsg());
    }

    private void handleYourModeIs(ServerMsg message) {
        IIrcServerPeer peer = message.getServerPeer();
        Mode mode = new Mode(peer, peer, peer.getMyself(), message.getArgs());
        processMode(mode);
    }

    @Override
    public MsgState process(IIrcMessage message, IIrcIO io) {

        if (message instanceof Ping) {
            IIrcServerPeer serverPeer = message.getServerPeer();
            sendMsg(new Pong(serverPeer, ((Ping) message).getPingId()));
            return MsgState.NO_MORE_PROCESSING;
        } else if (message instanceof Join) {
            IrcChannel[] joined = ((Join) message).getChans();
            IrcUser joiner = ((Join) message).getFrom();
            for (IrcChannel channel : joined) {
                channel.add(joiner);
            }
        } else if (message instanceof Kick) {
            handleKick((Kick) message);

        } else if (message instanceof Mode) {
            processMode((Mode) message);

        } else if (message instanceof Nick) {
            IIrcServerPeer serverPeer = message.getServerPeer();
            IrcUser target = ((Nick) message).getFrom();
            IrcNetwork network = serverPeer.getNetwork();
            network.updateNick(target, ((Nick) message).getNewNick());
        } else if (message instanceof Part) {
            IrcChannel[] fromChan = ((Part) message).getChannels();
            if (fromChan != null) {
                IrcUser leaver = ((Part) message).getFrom();
                for (IrcChannel channel : fromChan) {
                    channel.remove(leaver);
                }
            }
        } else if (message instanceof Quit) {
            handleQuit((Quit) message);

        } else if (message instanceof ServerError) {
            IIrcServerPeer serverPeer = message.getServerPeer();
            quit(serverPeer);
        } else if (message instanceof ServerMsg) {
            processServerMsg((ServerMsg) message);
        } else if (message instanceof SetTopic) {
            IrcChannel chan = ((SetTopic) message).getChan();
            IrcUser changer = (IrcUser) message.getFrom();
            Topic newTopic = new Topic(changer.getNick(), new Date(), ((SetTopic) message).getTopic());
            chan.getProperties().setTopic(newTopic);
        } else if (message instanceof Notice) {
            return handleNotice((Notice) message, io);
        }
        return MsgState.AVAILABLE;
    }

    protected void processMode(Mode message) {
        ArgumentList toks = message.getArgs();

        if (message.getModifiedObject() instanceof IrcChannel) {
            IrcChannel chan = (IrcChannel) message.getModifiedObject();
            String modeInfo = toks.poll(); // consume 1st argument
            boolean set = false;

            for (int idx = 0; idx < modeInfo.length(); idx++) {
                UtfChar next = UtfChar.charAt(modeInfo, idx);

                if (Mode.isModifier(next)) {
                    set = next.isEquals('+');
                    continue;
                }

                UtfChar modeItem = UtfChar.charAt(modeInfo, idx);
                UserChannelMode newMode = UserChannelMode.fromCmd(modeItem);

                if (newMode == null) {
                    // channel mode
                    chan.getProperties().process(set, modeItem);

                } else {
                    // user mode
                    String target = toks.poll();
                    IrcUser targetUser = message.getServerPeer().getNetwork().findUser(target, true);
                    Set<UserChannelMode> userModes = chan.getModes(targetUser);

                    if (set) {
                        userModes.add(newMode);

                    } else {
                        userModes.remove(newMode);
                    }
                }
            }
        } else {
            IrcUser target = (IrcUser) message.getModifiedObject();
            String modeQuery = toks.poll();
            UtfChar modifier = UtfChar.charAt(modeQuery, 0);
            UtfChar mode = UtfChar.charAt(modeQuery, 1);
            if (mode.isEquals('i')) {
                target.getProperties().setInvisible(modifier.isEquals('+'));

            } else {
                LOG.warn("TODO process user mode: " + modeQuery);
            }
        }
    }

    private void processServerMsg(ServerMsg message) {
        int code = message.getCode();
        if (code <= 5) {
            LOG.info("Greeting #" + code + ": " + message.getMsg());
            return;
        }
        NumericReplies reply = NumericReplies.fromCode(code);
        if (reply == null) {
            LOG.warn("Unknown code: " + code);
            return;
        }
        switch (reply) {
            case RPL_UMODEIS:
                handleYourModeIs(message);
                break;
            case RPL_TOPIC:
                handleRplTopic(message);
                break;
            case RPL_TOPICINFO:
                LOG.info("topic info: " + message.getArgs());
                break;
            case RPL_LUSERCLIENT:
                LOG.info("client statistics: " + message.getMsg());
                break;
            case RPL_LUSERCHANNELS:
                LOG.info("number of channels: " + message.getArgs());
                break;
            case RPL_LUSERME:
                LOG.info("server userme: " + message.getMsg());
                break;
            case RPL_MOTD:
                LOG.info("Motd LINE: " + message.getMsg());
                break;
            case RPL_NAMREPLY:
                handleNamReply(message);
                break;
            case RPL_ENDOFNAMES:
                LOG.debug("End of Names Reply");
                break;
            case RPL_LUSEROP:
                LOG.info("number of operators: " + message.getArgs());
                break;
            case RPL_MOTDSTART:
                LOG.debug("Start of MOTD: " + message.getArgs());
                break;
            case ERR_NOTEXTTOSEND:
                LOG.warn("Server told me that I tried to send an empty msg");
                break;
            case RPL_ENDOFMOTD:
                LOG.debug("End of MOTD");
                break;
            case RPL_LUSERUNKNOWN:
                LOG.info("number of unknown users: " + message.getArgs());
                break;
            case ERR_QNETSERVICEIMMUNE:
                LOG.warn("I cannot do harm to a service! " + message.getArgs());
                break;
            case RPL_WHOISUSER:
                handleWhoisUser(message);
                break;
            case ERR_NOSUCHNICK:
                WhoisRequest.end(message.getArgs(), false);
                break;
            case RPL_WHOISCHANNELS:
                handleWhoisChannels(message);
                break;
            case RPL_WHOISSERVER:
                ArgumentList args = message.getArgs();
                LOG.info(args.poll() + " is on node " + args.poll());
                break;
            case RPL_ENDOFWHOIS:
                WhoisRequest.end(message.getArgs(), true);
                break;
            case ERR_CHANOPRIVSNEEDED:
                LOG.warn("I am not channel operator. " + message.getArgs().peek());
                break;
            case ERR_NOTREGISTERED:
                LOG.warn("I should register before sending commands!");
                break;
            case RPL_WHOISIDLE:
                LOG.debug("User has been idle : " + message.getArgs().peek());
                break;
            case RPL_WHOISOPERATOR:
                args = message.getArgs();
                WhoisRequest.end(args, true);
                LOG.debug(args + " " + message.getMsg());
                break;
            case ERR_NICKNAMEINUSE:
                handleNickInUse(message);
                break;
            case ERR_NOTONCHANNEL:
            case ERR_NOSUCHCHANNEL:
                LOG.warn("[TODO process] " + message.getArgs());
                break;
            case RPL_LISTSTART:
                LOG.info("Starting to receive channels list");
                break;
            case RPL_LISTEND:
                LOG.info("Finished receiving channels list");
                break;
            case RPL_LIST:
                handleRplList(message);
                break;
            default:
                message.getServerPeer().getNetwork().handleSpecific(message, reply);
                break;
        }
    }

    @Override
    public void quit(IIrcServerPeer peer) {
        IIrcIO io = serverToIo.get(peer);
        if (io != null) {
            io.disconnect();
            serverToIo.remove(peer);
        }
    }

    /**
     * @see net.mauhiz.irc.base.IIrcControl#sendMsg(net.mauhiz.irc.base.msg.IIrcMessage)
     */
    @Override
    public void sendMsg(IIrcMessage msg) {
        LOG.info(msg);

        if (msg instanceof IPrivateIrcMessage) {
            if (StringUtils.isEmpty(((IPrivateIrcMessage) msg).getMessage())) {
                LOG.info("Not sending empty msg", new IllegalArgumentException());
                return;
            }
        }

        String ircForm = msg.getIrcForm();
        IIrcIO io = serverToIo.get(msg.getServerPeer());
        if (io.getStatus() != IOStatus.DISCONNECTED) {
            io.sendMsg(ircForm);

            if (msg instanceof IPrivateIrcMessage) {
                io.processMsg(ircForm); // process it back
            }

            if (msg instanceof Quit) {
                quit(msg.getServerPeer());
            }
        }
    }
}
