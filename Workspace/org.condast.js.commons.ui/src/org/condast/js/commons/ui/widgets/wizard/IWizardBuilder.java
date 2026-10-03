package org.condast.js.commons.ui.widgets.wizard;

import org.condast.js.commons.ui.widgets.wizard.xml.IXmlFlowWizard;

public interface IWizardBuilder<T extends Object> {

	/* (non-Javadoc)
	 * @see net.osgi.jp2p.chaupal.xml.IFactoryBuilder#build()
	 */
	void build( IXmlFlowWizard<T> wizard);

	boolean complete();

	/* (non-Javadoc)
	 * @see net.osgi.jp2p.chaupal.xml.IFactoryBuilder#isCompleted()
	 */
	boolean isCompleted();

	boolean hasFailed();

}