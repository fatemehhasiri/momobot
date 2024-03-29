package net.mauhiz.irc.gui;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractSwtTab {

    protected static void setListSize(List list, int numLines, int width) {
        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = numLines * list.getItemHeight(); // height for 10 rows
        data.widthHint = width;
        list.setSize(data.widthHint, data.heightHint);
        list.setLayoutData(data);
    }

    protected static void setTextSize(StyledText text, int numLines, int width) {
        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = numLines * text.getLineHeight(); // height for 10 rows
        data.widthHint = width;
        text.setSize(data.widthHint, data.heightHint);
        text.setLayoutData(data);
    }

    protected static void setTextSize(Text text, int numLines, int width) {
        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = numLines * text.getLineHeight(); // height for 10 rows
        data.widthHint = width;
        text.setSize(data.widthHint, data.heightHint);
        text.setLayoutData(data);
    }

    protected final Composite compo;

    protected final CTabItem folder;
    protected StyledText receiveBox;
    protected final SwtIrcClient swtIrcClient;

    protected AbstractSwtTab(final SwtIrcClient swtIrcClient) {
        this.swtIrcClient = swtIrcClient;
        folder = new CTabItem(swtIrcClient.folderBar, SWT.CLOSE);
        compo = new Composite(swtIrcClient.folderBar, SWT.BORDER | SWT.FILL);
        compo.setBackground(compo.getDisplay().getSystemColor(SWT.COLOR_DARK_BLUE));

        GridLayout gridLayout = new GridLayout(1, false);
        compo.setLayout(gridLayout);

        folder.setControl(compo);
    }

    public void appendText(String msg) {
        if (receiveBox == null) {
            throw new IllegalStateException("Must call initReceiveBox()");
        }
        receiveBox.append(msg + SystemUtils.LINE_SEPARATOR);
        // TODO replaceStyleRange
        //        receiveBox.append(ColorUtils.toHTML(msg) + "<br />");
    }

    protected abstract String getFolderName();

    protected void initReceiveBox(Composite parent) {
        receiveBox = new StyledText(parent, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        receiveBox.setBackground(receiveBox.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        receiveBox.setText(StringUtils.EMPTY);
        receiveBox.setEditable(false);
        setTextSize(receiveBox, 25, 400);
    }
}