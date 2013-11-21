package net.mauhiz.board.impl.common.assistant.swing.button;

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
public class FlippingButtonUI extends BasicButtonUI {
	public static final FlippingButtonUI FLIPPER = new FlippingButtonUI();

	@Override
	public void paint(final Graphics g, final JComponent c) {
		if (c instanceof JButton) {
			final JButton button = (JButton) c;
			final String text = button.getText();
			final Icon icon = button.isEnabled() ? button.getIcon() : button.getDisabledIcon();
			if (g instanceof Graphics2D) {
				final Graphics2D g2 = (Graphics2D) g;
				// save
				final AffineTransform tr = g2.getTransform();

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
	}
}