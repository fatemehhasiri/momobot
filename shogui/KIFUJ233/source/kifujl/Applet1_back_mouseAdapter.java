// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   Applet1.java

package kifujl;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Applet1_back_mouseAdapter extends MouseAdapter {

	Applet1 adaptee;

	Applet1_back_mouseAdapter(final Applet1 adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		adaptee.back_mouseClicked();
	}
}
