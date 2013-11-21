package net.mauhiz.board.impl.common.assistant.swing.button;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;

public abstract class TextIcon implements Icon, PropertyChangeListener {
	private static final boolean AUTO_RESIZE = true; // false ne marche pas encore
	protected JComponent component;
	protected Font font;
	protected Color foreground;
	protected int iconHeight;
	protected int iconWidth;
	protected int padding;
	protected String text;

	public TextIcon(final JComponent component, final String text) {
		super();
		this.component = component;
		setText(text);
		component.addPropertyChangeListener("font", this);
	}

	/**
	 *  Get the Font used to render the text. This will default to the Font
	 *  of the component unless the Font has been overridden by using the
	 *  setFont() method.
	 *
	 *  @return the Font used to render the text
	 */
	public Font getFont() {
		if (font == null) {
			return component.getFont();
		}
		return font;
	}

	/**
	 *  Get the foreground Color used to render the text. This will default to
	 *  the foreground Color of the component unless the foreground Color has
	 *  been overridden by using the setForeground() method.
	 *
	 *  @return the Color used to render the text
	 */
	public Color getForeground() {
		if (foreground == null) {
			return component.getForeground();
		}
		return foreground;
	}

	/**
	 *  Gets the height of this icon.
	 *
	 *  @return the height of the icon in pixels.
	 */
	@Override
	public int getIconHeight() {
		return iconHeight;
	}

	/**
	 *  Gets the width of this icon.
	 *
	 *  @return the width of the icon in pixels.
	 */
	@Override
	public int getIconWidth() {
		return iconWidth;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent e) {
		//  Handle font change when using the default font

		if (font == null && AUTO_RESIZE) {
			calculateIconDimensions();
		}
	}

	/**
	 *  Set the Font to be used for rendering the text
	 *
	 *  @param font  the Font to be used for rendering the text
	 */
	public void setFont(final Font font) {
		this.font = font;
		if (AUTO_RESIZE) {
			calculateIconDimensions();
		} else {
			component.repaint();
		}
	}

	/**
	 *  Set the foreground Color to be used for rendering the text
	 *
	 *  @param foreground  the foreground Color to be used for rendering the text
	 */
	public void setForeground(final Color foreground) {
		this.foreground = foreground;
		component.repaint();
	}

	/**
	 *  By default the size of the Icon is based on the size of the rendered
	 *  text. You can specify some padding to be added to the start and end
	 *  of the text when it is rendered.
	 *
	 *  @param padding  the padding amount in pixels
	 */
	public void setPadding(final int padding) {
		this.padding = padding;
		if (AUTO_RESIZE) {
			calculateIconDimensions();
		} else {
			component.repaint();
		}

	}

	/**
	 *  Set the text to be rendered on the Icon
	 *
	 *  @param text  the text to be rendered on the Icon
	 */
	public final void setText(final String text) {
		this.text = text;
		if (AUTO_RESIZE) {
			calculateIconDimensions();
		} else {
			component.repaint();
		}
	}

	protected abstract void calculateIconDimensions();
}
