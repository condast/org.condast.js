package org.condast.js.commons.ui.widgets.edit;

import org.condast.commons.ui.edit.AbstractEditingSupport;
import org.condast.commons.ui.widgets.celleditors.AbstractCheckBoxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;

public class CheckBoxEditingSupport<T extends Object> extends AbstractEditingSupport<T,Boolean> {
	private static final long serialVersionUID = 1L;

	public CheckBoxEditingSupport(ColumnViewer viewer, AbstractCheckBoxCellEditor<T> editor) {
		super(viewer, editor );
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Boolean onGetValue( T element) {
		AbstractCheckBoxCellEditor<T> editor = (AbstractCheckBoxCellEditor<T>) super.getCellEditor( element );
		editor.setData(element);
		return ( editor.getValue() == null )? false: (Boolean) editor.getValue();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void onSetValue( T element, Boolean userInputValue) {
		AbstractCheckBoxCellEditor<T> editor = (AbstractCheckBoxCellEditor) super.getCellEditor( element );
		editor.setData(element);
		editor.setValue(userInputValue);
	}
}