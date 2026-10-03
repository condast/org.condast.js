package org.condast.js.commons.ui.widgets.wizard.xml;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.ui.wizard.IButtonWizardContainer.Buttons;
import org.condast.commons.ui.wizard.IHeadlessWizardContainer.ContainerTypes;

/**
 * Used for temporary storage
 * @author Kees
 *
 */
public class IndexStore{

	String pageName;
	String description;
	String message;
	public ContainerTypes type;

	public String composite_clss;
	public int composite_style;
	public String url;
	public String source;

	public boolean barOnTop;
	public String titleStyle;

	public String buttonStyle;
	Map<Buttons, ButtonInfo> buttonInfo;
	boolean onCancel;
	boolean onFinish;

	int index;

	protected IndexStore( String pageName, String description, String message, ContainerTypes type, int index ) {
		super();
		this.pageName = pageName;
		this.index = index;
		this.description = description;
		this.message = message;
		this.type = type;
		this.buttonInfo = new HashMap<>();
		this.onCancel = ( index == 0 );
		this.onFinish = ( index == 0 );
	}

	int getIndex() {
		return index;
	}

	public String getURL(){
		return this.url;
	}

	public String getSource(){
		return this.source;
	}

	public void addButtonInfo( Buttons button, boolean visible, boolean enabled ){
		ButtonInfo bi = new ButtonInfo();
		bi.enabled = enabled;
		bi.visible = visible;
		buttonInfo.put( button, bi );
	}

	boolean isButtonVisible( Buttons button ){
		ButtonInfo bi = buttonInfo.get( button );
		return ( bi == null )?false: bi.visible;
	}

	boolean isButtonEnabled( Buttons button ){
		ButtonInfo bi = buttonInfo.get( button );
		return ( bi == null )?false: bi.enabled;
	}

	String getButtonPushed( Buttons button ){
		ButtonInfo bi = buttonInfo.get( button );
		return ( bi == null )?null: bi.push;
	}

	Buttons[] getButtons(){
		return buttonInfo.keySet().toArray( new Buttons[ this.buttonInfo.size() ]);
	}

	/**
	 * Add the id of the push id
	 * @param text
	 * @param enabled
	 */
	public void addPushPage( Buttons button, String push ){
		buttonInfo.get( button ).push = push;
	}

	private static class ButtonInfo{

		boolean enabled;
		boolean visible;

		//The id of the page that the wizard will jump to after
		//pushing this button
		String push;
	}
}
