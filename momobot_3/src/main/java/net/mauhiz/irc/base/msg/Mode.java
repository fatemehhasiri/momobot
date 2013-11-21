package net.mauhiz.irc.base.msg;

import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcChannel;
import net.mauhiz.irc.base.data.IrcCommands;
import net.mauhiz.irc.base.data.Target;
import net.mauhiz.util.UtfChar;

/**
 * @author mauhiz
 */
public class Mode extends AbstractIrcMessage implements IrcChannelMessage {

    public static boolean isModifier(UtfChar utfChar) {
        return utfChar.isEquals('+') || utfChar.isEquals('-');
    }

    private final ArgumentList modeArgs;
    private final Target modifiedObject;

    public Mode(IIrcServerPeer server, Target from, Target modifiedObject) {
        this(server, from, modifiedObject, null);
    }

    public Mode(IIrcServerPeer server, Target from, Target modifiedObject, ArgumentList modeArgs) {
        super(server, from);
        this.modifiedObject = modifiedObject;
        this.modeArgs = modeArgs;
    }

    @Override
    public Mode copy() {
        return new Mode(server, from, modifiedObject, modeArgs.copy());
    }

    /**
     * @return a copy of the arguments
     */
    public ArgumentList getArgs() {
        return modeArgs.copy();
    }

    @Override
    public IrcChannel[] getChans() {
        if (modifiedObject instanceof IrcChannel) {
            return new IrcChannel[] { (IrcChannel) modifiedObject };
        }
        return new IrcChannel[0];
    }

    @Override
    public IrcCommands getIrcCommand() {
        return IrcCommands.MODE;
    }

    @Override
    public String getIrcForm() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getIrcForm());
        sb.append(' ').append(modifiedObject);
        sb.append(' ').append(modeArgs.getMessage());
        return sb.toString();
    }

    public Target getModifiedObject() {
        return modifiedObject;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (from == null) {
            return "* Setting mode: " + modeArgs.getRemainingData() + " " + modifiedObject;
        }
        return "* " + niceFromDisplay() + " sets mode: " + modeArgs.getRemainingData() + " " + modifiedObject;
    }
}
