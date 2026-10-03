package org.condast.js.commons.ui.widgets.wizard.xml;

import org.condast.commons.ui.wizard.IFlowControlWizard;
import org.condast.commons.ui.wizard.IHeadlessWizardContainer.ContainerTypes;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;

public interface IXmlFlowWizard<T extends Object> extends IFlowControlWizard<T>{

	public static final String S_WIZARD_DESC = "/AIEONF-INF/wizard.xml";

	void createPageControls(Composite parent);

	void setWindowTitle(String title);

	void setPreviousNext(boolean previousNext);

	void addPages();

	/**
	 * Add a page to the the wizard
	 * @param pageName
	 * @param description
	 * @param message
	 * @param type
	 */
	IndexStore addPage(String pageName, String description, String message, ContainerTypes type, boolean onCancel,
			boolean onFinish);

	IndexStore getCurrent();

	void setTitleStyle(String titleStyle);

	void setButtonbarStyle(String buttonbarStyle);

	boolean isHelpAvailable();

	void performSave(IWizardPage arg0);

	boolean performCancel();

	boolean performFinish();

	void dispose();

}