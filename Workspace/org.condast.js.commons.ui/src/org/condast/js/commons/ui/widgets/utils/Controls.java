package org.condast.js.commons.ui.widgets.utils;

import java.util.Date;

import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.swt.DateWidget;
import org.eclipse.swt.widgets.Text;

public class Controls {

	/**
	 * Set the date for the given date widget
	 * @param widget
	 * @param date
	 * @return
	 */
	public static boolean setDate( DateWidget widget, Date date ){
		if( date == null )
			return false;
		widget.setInput(date, true);
		return false;
	}

	/**
	 * Set the text in the text box if this is possible
	 * @param text
	 * @param txt
	 * @return
	 */
	public static boolean setText( Text text, String txt ){
		if( StringUtils.isEmpty( txt ))
			return false;
		text.setText(txt);
		return true;
	}
}
