package net.mauhiz.board.impl.common.gui.rotation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * @author mauhiz
 * Ne marche pas :|
 */
class FlippingButtonUI extends BasicButtonUI {
	static final FlippingButtonUI FLIPPER = new FlippingButtonUI();


	public FlippingButtonUI() {
		super();
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		JButton button = (JButton) c;
		String text = button.getText();
		Icon icon = button.isEnabled() ? button.getIcon() : button.getDisabledIcon();

		Graphics2D g2 = (Graphics2D) g;
		// save
		AffineTransform tr = g2.getTransform();
		
		// flip
		g2.rotate(Math.PI);
		
		// translate the origin
		g2.translate(c.getWidth(), c.getHeight());

		if (icon != null) {
			icon.paintIcon(c, g, 0, 0);
		}
		
		if (text != null) {
			g.drawString(text, 0, 0);
		}

		// restore
		g2.setTransform(tr);
	}
}