package net.mauhiz.board.impl.common.assistant.swing.button;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;

/**
 *  The TextIcon will paint a String of text as an Icon. The Icon
 *  can be used by any Swing component that supports icons.
 *
 *  TextIcon supports two different layout styles:
 *  <ul>
 *  <li>Horizontally - does normal rendering of the text by using the
 *	  Graphics.drawString(...) method
 *  <li>Vertically - Each character is displayed on a separate line
 *  </ul>
 *
 *  TextIcon was designed to be rendered on a specific JComponent as it
 *  requires FontMetrics information in order to calculate its size and to do
 *  the rendering. Therefore, it should only be added to component it was
 *  created for.
 *
 *  By default the text will be rendered using the Font and foreground color
 *  of its associated component. However, this class does allow you to override
 *  these properties.
 */
public class HorizontalTextIcon extends TextIcon {
	/**
	 *  Convenience constructor to create a TextIcon with a HORIZONTAL layout.
	 *
	 *  @param component  the component to which the icon will be added
	 *  @param text       the text to be rendered on the Icon
	 */
	public HorizontalTextIcon(final JComponent component, final String text) {
		super(component, text);
	}

	/**
	*  Paint the icons of this compound icon at the specified location
	*
	*  @param c The component to which the icon is added
	*  @param g the graphics context
	*  @param x the X coordinate of the icon's top-left corner
	*  @param y the Y coordinate of the icon's top-left corner
	*/
	@Override
	public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
		final Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(getFont());
		g2.setColor(getForeground());
		synchronized (TextIcon.class) {
			final FontMetrics fm = g2.getFontMetrics();
			g2.translate(x, y + fm.getAscent());
		}
		g2.drawString(text, padding, 0);
	}

	/**
	 *  Calculate the size of the Icon using the FontMetrics of the Font.
	 */
	@Override
	protected void calculateIconDimensions() {
		final Font lfont = getFont();

		final FontMetrics fm = component.getFontMetrics(lfont);

		iconWidth = fm.stringWidth(text) + padding * 2;
		iconHeight = fm.getHeight();

		component.revalidate();
	}
}
