package net.mauhiz.board.impl.common.assistant.swing.button;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;

import net.mauhiz.util.AbstractNamedRunnable;
import net.mauhiz.util.ExecutionType;
import net.mauhiz.util.MonitoredRunnable;
import net.mauhiz.util.PerformanceMonitor;

import org.apache.commons.lang.StringUtils;

public class RotatingJButton extends JButton {

	class ButtonDecorator extends MonitoredRunnable {

		public ButtonDecorator() {
			super("Button Decorator", "Decorated button[flip=" + flip + "] with text: " + iconText);
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.PARALLEL_CACHED;
		}

		@Override
		public void mrun() {
			final Icon ti = getTextIcon();
			final Icon realIcon = flip ? getRotatedIcon(ti) : ti;

			if (getIcon() != realIcon) {
				new SetIcon("Set Icon", realIcon).launch();
			}
		}
	}

	class ButtonEnabler extends MonitoredRunnable {
		private final boolean enabled;

		ButtonEnabler(final boolean enabled) {
			super("Button Toggler");
			this.enabled = enabled;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		public String getLogMsg() {
			return "Button " + (enabled ? "enabled" : "disabled");
		}

		@Override
		public void mrun() {
			superEnable(enabled);
		}
	}

	class SetIcon extends AbstractNamedRunnable {
		private final Icon realIcon;

		SetIcon(final String name, final Icon realIcon) {
			super(name);
			this.realIcon = realIcon;
		}

		@Override
		public ExecutionType getExecutionType() {
			return ExecutionType.GUI_ASYNCHRONOUS;
		}

		@Override
		protected void trun() {
			setIcon(realIcon);
		}
	}

	static final Map<String, TextIcon> ICON_CACHE = new HashMap<>();

	static final Map<Icon, RotatedIcon> ROTATION_CACHE = new HashMap<>();
	private static final long serialVersionUID = -4947530979709086579L;
	boolean flip;

	String iconText = "";

	@Override
	public void addActionListener(final ActionListener l) {
		super.addActionListener(l);
		if (!isEnabled()) {
			setEnabled(true);
		}
	}

	@Override
	public void removeActionListener(final ActionListener l) {
		super.removeActionListener(l);
		if (getActionListeners().length == 0) {
			setEnabled(false);
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		new ButtonEnabler(enabled).launch();
	}

	public void setText(final String iconText, final boolean flip) {
		if (StringUtils.equals(iconText, this.iconText)) {
			if (this.flip == flip) {
				return;
			}

			if (StringUtils.isBlank(this.iconText)) {
				return;
			}
		}
		this.flip = flip;
		this.iconText = iconText;
		new ButtonDecorator().launch();
	}

	RotatedIcon getRotatedIcon(final Icon normal) {
		synchronized (normal) {
			RotatedIcon reverse = ROTATION_CACHE.get(normal);

			if (reverse == null) {
				reverse = new RotatedIcon(normal, Rotation.UPSIDE_DOWN);
				ROTATION_CACHE.put(normal, reverse);
			}

			return reverse;
		}
	}

	Icon getTextIcon() {
		synchronized (iconText.intern()) {
			TextIcon ti = ICON_CACHE.get(iconText);

			if (ti == null) {
				final PerformanceMonitor pm = new PerformanceMonitor();
				ti = new HorizontalTextIcon(RotatingJButton.this, iconText);
				ICON_CACHE.put(iconText, ti);
				pm.perfLog("Text Icon '" + iconText + "' created", getClass());
			}

			return ti;
		}
	}

	void superEnable(final boolean enabled) {
		super.setEnabled(enabled);
	}
}
