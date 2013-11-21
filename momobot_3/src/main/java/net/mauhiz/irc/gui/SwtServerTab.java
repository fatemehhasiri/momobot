package net.mauhiz.irc.gui;

import net.mauhiz.irc.base.data.IIrcServerPeer;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class SwtServerTab extends AbstractSwtTab {

    class CloseHandler extends CTabFolder2Adapter {

        @Override
        public void close(CTabFolderEvent event) {
            if (event.item == folder) {
                swtIrcClient.doDisconnect(server);
                Logger.getLogger(SwtChanTab.class).info("Closed server tab: " + server);
            }
        }
    }

    class JoinHandler implements Listener {
        private final Text joinField;

        JoinHandler(Text joinField) {
            this.joinField = joinField;
        }

        @Override
        public void handleEvent(Event arg0) {
            swtIrcClient.doJoin(server, server.getNetwork().findChannel(joinField.getText()));
        }
    }

    class ListHandler implements Listener {

        @Override
        public void handleEvent(Event arg0) {
            swtIrcClient.gtm.client.sendMsg(new net.mauhiz.irc.base.msg.List(server, null));
        }
    }

    final IIrcServerPeer server;

    public SwtServerTab(final SwtIrcClient swtIrcClient, final IIrcServerPeer server) {
        super(swtIrcClient);
        this.server = server;

        folder.setText(getFolderName());
        Composite mainPanel = new Composite(compo, SWT.BORDER | SWT.FILL);
        mainPanel.setLayout(new GridLayout(2, false));
        initReceiveBox(mainPanel);
        initChanList(mainPanel);
        Composite joinBar = new Composite(compo, SWT.BORDER);
        joinBar.setLayoutData(new GridData(SWT.FILL));
        joinBar.setLayout(new GridLayout(2, false));
        final Text joinField = new Text(joinBar, SWT.SINGLE);
        joinField.setEditable(true);
        Button joinButton = new Button(joinBar, SWT.PUSH);
        joinButton.setText("Join");
        joinButton.addListener(SWT.Selection, new JoinHandler(joinField));

        swtIrcClient.folderBar.addCTabFolder2Listener(new CloseHandler());
    }

    @Override
    protected final String getFolderName() {
        return server.getNetwork().getAlias();
    }

    private void initChanList(Composite parent) {
        Composite chanListAndButtons = new Composite(parent, SWT.BORDER);
        chanListAndButtons.setLayout(new GridLayout(1, false));
        org.eclipse.swt.widgets.List chanList = new org.eclipse.swt.widgets.List(chanListAndButtons, SWT.BORDER
                | SWT.V_SCROLL);
        setListSize(chanList, 20, 150);
        Button listButton = new Button(chanListAndButtons, SWT.PUSH);
        listButton.setText("Refresh channel list");
        listButton.addListener(SWT.Selection, new ListHandler());
    }
}