// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Applet1.java

package kifujl;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Referenced classes of package kifujl:
//            Applet1

class Applet1_go_rep_mouseAdapter extends MouseAdapter {

	Applet1 adaptee;

	Applet1_go_rep_mouseAdapter(final Applet1 adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		adaptee.go_rep_mouseClicked();
	}
}
