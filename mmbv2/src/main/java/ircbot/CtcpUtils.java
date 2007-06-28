package ircbot;

/**
 * @author mauhiz
 */
public class CtcpUtils implements ICtcpCommands, IIrcCommands {
    /**
     * @param toQuote
     *            le message � quoter
     * @return une commande CTCP
     */
    public static String ctcpQuote(final String toQuote) {
        return STX + toQuote + STX;
    }

    /**
     * @param msg
     *            le message � envoyer
     * @param recipient
     *            le destinataire (chan, user..)
     * @param out
     *            le moyen d'exp�dition
     */
    public static void sendAction(final String msg, final String recipient, final OutputQueue out) {
        sendCtcpMsg(E_ACTION + SPC + msg, recipient, out);
    }

    /**
     * @param msg
     *            le message � envoyer
     * @param recipient
     *            le destinataire (chan, user..)
     * @param out
     *            le moyen d'exp�dition
     */
    public static void sendCtcpMsg(final String msg, final String recipient, final OutputQueue out) {
        out.put(PRIVMSG + SPC + recipient + SPC + ':' + ctcpQuote(msg));
    }

    /**
     * @param string
     * @param user
     * @param outputThread
     */
    public static void sendCtcpNotice(final String string, final IrcUser user, final OutputQueue outputThread) {
        sendCtcpNotice(string, user.getNick(), outputThread);
    }

    /**
     * @param msg
     *            le message � envoyer
     * @param recipient
     *            le destinataire (chan, user..)
     * @param out
     *            le moyen d'exp�dition
     */
    public static void sendCtcpNotice(final String msg, final String recipient, final OutputQueue out) {
        out.put(NOTICE + SPC + recipient + SPC + ':' + ctcpQuote(msg));
    }

    /**
     * constructeur priv�.
     */
    protected CtcpUtils() {
        throw new UnsupportedOperationException();
    }
}
