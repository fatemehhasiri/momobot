package net.mauhiz.irc.gui.actions;

import net.mauhiz.irc.base.data.IrcServer;
import net.mauhiz.irc.base.data.Target;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.gui.GuiTriggerManager;
import net.mauhiz.util.AbstractAction;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Text;

/**
 * @author mauhiz
 */
public class SendAction extends AbstractAction {
    private final Text bar;
    private final GuiTriggerManager gtm;
    private final IrcServer server;
    private final Target target;

    public SendAction(Text bar1, GuiTriggerManager gtm1, IrcServer server1, Target target) {
        super();
        gtm = gtm1;
        bar = bar1;
        server = server1;
        this.target = target;
    }

    @Override
    protected void doAction() {
        String toSend = bar.getText();
        if (StringUtils.isEmpty(toSend)) {
            return;
        }
        Privmsg msg = new Privmsg(null, target, server, toSend);
        gtm.getClient().sendMsg(msg);
        bar.setText("");
    }

    @Override
    protected boolean isAsynchronous() {
        return false; // this should be quick 
    }
}
