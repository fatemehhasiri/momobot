// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Applet1.java

package kifujl;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

// Referenced classes of package kifujl:
//            Applet1

class Applet1_HenkaChoice_itemAdapter implements ItemListener {

	Applet1 adaptee;

	Applet1_HenkaChoice_itemAdapter(final Applet1 adaptee) {
		this.adaptee = adaptee;
	}

	@Override
	public void itemStateChanged(final ItemEvent e) {
		adaptee.HenkaChoice_itemStateChanged();
	}
}
