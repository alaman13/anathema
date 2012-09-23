package net.sf.anathema.lib.gui.table;

import com.google.common.base.Preconditions;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

// NOT_PUBLISHED
public class DisableableProxyAction extends AbstractAction {
	private static final long serialVersionUID = 505097053360803709L;
	private final Action action;
	private boolean enabled = true;

	public DisableableProxyAction(Action action) {
		Preconditions.checkNotNull(action);
		this.action = action;
		action.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
            public void propertyChange(PropertyChangeEvent evt) {
				firePropertyChange(evt.getPropertyName(), evt.getOldValue(),
						evt.getNewValue());
			}
		});
	}

	@Override
	public boolean isEnabled() {
		return enabled && action.isEnabled();
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		}
		this.enabled = enabled;
		firePropertyChange("enabled", !isEnabled(), isEnabled());
	}

	@Override
    public void actionPerformed(ActionEvent e) {
		action.actionPerformed(e);
	}

	@Override
	public Object getValue(String key) {
		return action.getValue(key);
	}

	@Override
	public void putValue(String key, Object newValue) {
		action.putValue(key, newValue);
	}
}