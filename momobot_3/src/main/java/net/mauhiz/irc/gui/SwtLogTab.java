package net.mauhiz.irc.gui;

import net.mauhiz.irc.base.data.IIrcServerPeer;
import net.mauhiz.irc.base.data.IrcServerFactory;

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

public class SwtLogTab extends AbstractSwtTab {
    class CloseHandler extends CTabFolder2Adapter {

        @Override
        public void close(CTabFolderEvent event) {
            if (folder == event.item) {
                event.doit = false;
            }
        }
    }

    static class ConnectHandler implements Listener {
        private final Text connectField;
        private final SwtIrcClient swtIrcClient;

        ConnectHandler(Text connectField, SwtIrcClient swtIrcClient) {
            this.connectField = connectField;
            this.swtIrcClient = swtIrcClient;
        }

        @Override
        public void handleEvent(Event event) {
            IIrcServerPeer server = IrcServerFactory.createServer(connectField.getText());
            server.introduceMyself("momo", "mmb", "momobot le 3eme");
            swtIrcClient.doConnect(server);
        }
    }

    public SwtLogTab(final SwtIrcClient swtIrcClient) {
        super(swtIrcClient);
        folder.setText(getFolderName());
        initReceiveBox(compo);

        Composite connectBar = new Composite(compo, SWT.BORDER);
        connectBar.setLayoutData(new GridData(SWT.FILL));
        connectBar.setLayout(new GridLayout(2, false));
        final Text connectField = new Text(connectBar, SWT.SINGLE);
        connectField.setEditable(true);
        connectField.setText("irc://irc.quakenet.org:6667/");
        setTextSize(connectField, 1, 400);

        Button joinButton = new Button(connectBar, SWT.PUSH);
        joinButton.setText("Connect");
        joinButton.addListener(SWT.Selection, new ConnectHandler(connectField, swtIrcClient));

        swtIrcClient.folderBar.addCTabFolder2Listener(new CloseHandler());
    }

    @Override
    protected final String getFolderName() {
        return "Console";
    }
}