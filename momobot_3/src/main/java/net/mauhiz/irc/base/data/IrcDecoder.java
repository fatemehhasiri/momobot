package net.mauhiz.irc.base.data;

import java.util.ArrayList;
import java.util.List;

import net.mauhiz.irc.MomoStringUtils;
import net.mauhiz.irc.base.IrcSpecialChars;
import net.mauhiz.irc.base.msg.IIrcMessage;
import net.mauhiz.irc.base.msg.Join;
import net.mauhiz.irc.base.msg.Kick;
import net.mauhiz.irc.base.msg.Mode;
import net.mauhiz.irc.base.msg.Nick;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.base.msg.Part;
import net.mauhiz.irc.base.msg.Ping;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.msg.Quit;
import net.mauhiz.irc.base.msg.ServerError;
import net.mauhiz.irc.base.msg.ServerMsg;
import net.mauhiz.irc.base.msg.SetTopic;
import net.mauhiz.util.UtfChar;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

/**
 * @author mauhiz
 */
public enum IrcDecoder implements IrcSpecialChars, IIrcDecoder {
    INSTANCE;

    private static IIrcMessage buildFromCommand(IrcCommands command, IIrcServerPeer server, ArgumentList args,
            Target from, String msg) {

        switch (command) {
            case NOTICE:
                Target to = decodeTarget(server, args.poll());
                return new Notice(server, from, to, msg);

            case PING:
                return new Ping(server, from, msg);

            case MODE:
                Target modifiedObject = decodeTarget(server, args.poll());
                return new Mode(server, from, modifiedObject, args);

            case JOIN:
                String[] chans = StringUtils.split(args.poll(), ',');
                String[] keys = StringUtils.split(args.poll(), ',');
                IrcChannel[] channels = toIrcChannels(server.getNetwork(), chans);
                return new Join(server, (IrcUser) from, channels, keys);

            case PART:
                chans = StringUtils.split(args.poll(), ',');
                channels = toIrcChannels(server.getNetwork(), chans);
                return new Part(server, (IrcUser) from, msg, channels);

            case PRIVMSG:
                to = decodeTarget(server, args.poll());
                if (QUOTE_STX.equals(UtfChar.charAt(msg, 0))) {
                    String ctcpContent = StringUtils.strip(msg, QUOTE_STX.toString());
                    return CtcpFactory.decode(server, from, to, ctcpContent);
                }
                return new Privmsg(server, from, to, msg);

            case QUIT:
                return new Quit(server, from, msg);

            case NICK:
                return new Nick(server, (IrcUser) from, msg);

            case KICK:
                to = decodeTarget(server, args.poll());
                String nick = args.poll();
                IrcUser target = server.getNetwork().findUser(nick, false);
                return new Kick(server, from, (IrcChannel) to, target, msg);

            case ERROR:
                return new ServerError(server, msg);

            case TOPIC:
                to = decodeTarget(server, args.poll());
                return new SetTopic(server, from, (IrcChannel) to, msg);

            default:
                return null;
        }
    }

    private static Target decodeTarget(IIrcServerPeer peer, String fromStr) {
        if (fromStr == null) {
            return null;

        } else if (MomoStringUtils.isChannelName(fromStr)) {
            return peer.getNetwork().findChannel(fromStr);
        }

        HostMask mask = HostMask.getInstance(fromStr);

        if (mask == null) { // not a host mask
            if (StringUtils.contains(fromStr, '.')) {
                peer.setIrcForm(fromStr);
                return peer;
            }

            return peer.getNetwork().findUser(fromStr, true);
        }

        return peer.getNetwork().findUser(mask, true);
    }

    private static String getCmd(ArgumentList args, String raw) {
        String cmd = args.poll();

        if (cmd == null) {
            throw new IllegalArgumentException("Malformed IRC message: " + raw);
        }
        return cmd;
    }

    private static IrcChannel[] toIrcChannels(IrcNetwork network, String[] chans) {
        List<IrcChannel> channels = new ArrayList<>(chans.length);
        for (String chan : chans) {
            channels.add(network.findChannel(chan));
        }
        return channels.toArray(new IrcChannel[channels.size()]);
    }

    private static ArgumentList tokenizeArgs(String raw) {
        String argumentStr = StringUtils.substringBefore(raw, " :");
        ArgumentList args = new ArgumentList(argumentStr);
        if (args.isEmpty()) {
            throw new IllegalArgumentException("Malformed IRC message: " + raw);
        }
        return args;
    }

    /**
     * @param raw
     * @return raw IRC msg
     */
    @Override
    public IIrcMessage buildFromRaw(IIrcServerPeer server, String raw) {
        ArgumentList args = tokenizeArgs(raw);
        String next = args.peek();
        String fromStr = next != null && UtfChar.charAt(next, 0).isEquals(':') ? args.poll().substring(1) : null;
        String cmd = getCmd(args, raw);
        String msg = StringUtils.substringAfter(raw, " :");
        Target from = decodeTarget(server, fromStr);

        if (StringUtils.isNumeric(cmd)) {
            // skip the 'to'
            args.poll();
            return new ServerMsg(server, from, Integer.parseInt(cmd), args, msg);
        }

        IrcCommands command;

        try {
            command = IrcCommands.valueOf(cmd);

        } catch (IllegalArgumentException iae) {
            throw new NotImplementedException("Unknown command on network " + server.getNetwork() + ": " + cmd);
        }

        IIrcMessage ircMessage = buildFromCommand(command, server, args, from, msg);
        if (ircMessage == null) {
            throw new NotImplementedException("Unsupported command on network " + server.getNetwork() + ": " + command);
        }

        return ircMessage;
    }
}
