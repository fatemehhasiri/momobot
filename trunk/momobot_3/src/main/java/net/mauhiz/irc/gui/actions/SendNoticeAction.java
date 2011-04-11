package net.mauhiz.irc.gui.actions;

import net.mauhiz.irc.base.data.IrcServer;
import net.mauhiz.irc.base.msg.Notice;
import net.mauhiz.irc.gui.GuiTriggerManager;
import net.mauhiz.util.AbstractAction;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Text;

/**
 * @author mauhiz
 */
public class SendNoticeAction extends AbstractAction {
    private final Text bar;
    private final GuiTriggerManager gtm;
    private final IrcServer server;
    private String target;

    public SendNoticeAction(Text bar1, GuiTriggerManager gtm1, IrcServer server1, String target) {
        gtm = gtm1;
        bar = bar1;
        server = server1;
        target = target;
    }

    @Override
    protected void doAction() {
        String toSend = bar.getText();
        if (StringUtils.isEmpty(toSend)) {
            return;
        }
        Notice msg = new Notice(null, target, server, toSend);
        gtm.getClient().sendMsg(msg);
        bar.setText("");
    }

    @Override
    protected boolean isAsynchronous() {
        return false; // this should be quick 
    }
}
