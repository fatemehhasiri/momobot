package net.mauhiz.board.remote;

import java.io.IOException;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;

import org.apache.commons.lang.StringUtils;

public class MoveTrigger implements IPrivmsgTrigger {

	@Override
	public void doTrigger(final Privmsg im, final IIrcControl control) {
		if (im instanceof CtcpMove) {
			final String moveText = ((CtcpMove) im).getCtcpContent();
			final String[] args = StringUtils.split(moveText, ' ');
			final String gameId = args[0];
			final String move = args[1];
			try {
				BoardManager.getInstance().receiveMove(gameId, move);
			} catch (final IOException ioe) {
				final Privmsg ans = new Privmsg(im, "Failed to receive move: " + ioe);
				control.sendMsg(ans);
			}
		}
	}

	@Override
	public boolean isActivatedBy(final String text) {
		return true;
	}

}
