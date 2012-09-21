package net.sf.anathema.lib.gui.selection;

import net.sf.anathema.lib.gui.IView;
import net.sf.anathema.lib.gui.list.ListSelectionMode;

import javax.swing.ListCellRenderer;
import java.util.List;

public interface IListObjectSelectionView<V> extends
		IVetoableObjectSelectionView<V>, IView {

	void setCellRenderer(ListCellRenderer renderer);

  void setSelectionType(ListSelectionMode type);
}