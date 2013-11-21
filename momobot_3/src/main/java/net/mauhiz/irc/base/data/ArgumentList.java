package net.mauhiz.irc.base.data;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import net.mauhiz.util.UtfChar;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ArgumentList implements Iterable<String> {

    private int index;
    private final String message;

    public ArgumentList(String message) {
        assert message != null;
        this.message = message;
    }

    public List<String> asList() {
        String[] args = StringUtils.split(getRemainingData(), ' ');
        return Arrays.asList(args);
    }

    public ArgumentList copy() {
        ArgumentList al = new ArgumentList(message);
        al.index = index;
        return al;
    }

    public String getMessage() {
        return message;
    }

    public String getRemainingData() {
        return message.substring(index);
    }

    public boolean isEmpty() {
        return index >= message.length();
    }

    @Override
    public ListIterator<String> iterator() {
        return asList().listIterator();
    }

    private String nextArgument(boolean consume) {
        if (isEmpty()) {
            return null;
        }
        int startIndex = index;
        Logger.getLogger(ArgumentList.class).debug("Working on " + message.substring(index));
        int endIndex = message.indexOf(' ', startIndex);

        if (endIndex < 0) {
            String ret = getRemainingData();

            if (consume) {
                index = message.length();
            }

            return ret;
        }

        // skip next whitespace utf characters
        if (consume) {
            int newIndex = endIndex;
            while (UtfChar.charAt(message, newIndex).isSpaceChar()) {
                newIndex++;
            }
            index = newIndex;
        }

        return message.substring(startIndex, endIndex);
    }

    public String peek() {
        return nextArgument(false);
    }

    public String poll() {
        return nextArgument(true);
    }

    public void reset() {
        index = 0;
    }

    @Override
    public String toString() {
        return getRemainingData();
    }

}
