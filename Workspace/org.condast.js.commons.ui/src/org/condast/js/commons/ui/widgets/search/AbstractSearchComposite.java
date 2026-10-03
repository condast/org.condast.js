/*******************************************************************************
 * Copyright (c) 2016 Condast and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Condast                - EetMee
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.condast.js.commons.ui.widgets.search;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractSearchComposite extends Composite {
	private static final long serialVersionUID = 1L;

	public static final String S_LINKED_IN = "LinkedIn";

	protected Composite resultsComposite;
	protected Composite thisComposite;

	protected Text text_search;

	protected DateTime dateTime_frm;
	protected DateTime dateTime_to;
	protected Text text_lokatie;
	protected Text text_radius;
	protected Button buttonIncludeLokatieInSearch;
	protected Button buttonIncludeDatumInSearch;

	private Map<String, Composite> botComposites;

	protected static final String RWT_FRONTEND = "frontend";

	protected AbstractSearchComposite(Composite parent, int style) {
		super(parent, style);
		this.thisComposite = this;
		botComposites = new HashMap<>();
		this.createComposite(parent, style);
		this.initValues();
	}

	protected void createComposite(Composite parent,int style){

		GridLayout layout = new GridLayout(2,false);
		layout.marginHeight=0;
		layout.marginWidth=0;
		layout.verticalSpacing=0;
		layout.horizontalSpacing=0;
		this.setLayout(layout);
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		this.setLayoutData(gd);

		Composite compSearchBalk = new Composite(this,SWT.NONE);
		GridLayout layoutSb = new GridLayout(2,false);
		layoutSb.marginWidth=300;
		layoutSb.marginHeight=50;
		compSearchBalk.setLayout(layoutSb);
		GridData gd_compSearchBalk = new GridData(SWT.FILL,SWT.FILL,true,false,2,1);
		gd_compSearchBalk.widthHint = 441;
		compSearchBalk.setLayoutData(gd_compSearchBalk);
		compSearchBalk.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);
		text_search = new Text(compSearchBalk, SWT.BORDER);
		//gd_text.widthHint = 150;
		GridData gd_text_search = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		gd_text_search.widthHint = 25;
		text_search.setLayoutData(gd_text_search);

		Button btnSearch = new Button( compSearchBalk, SWT.NONE);
		btnSearch.setText("Datacop Search");
		btnSearch.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				executeSearchButton();
			}
		});

		//searchDetailsComposite();

		this.resultsComposite = implResultComposite(thisComposite, SWT.NONE);
		resultsComposite.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);
	}

	protected void searchDetailsComposite(){
		Composite compSearch = new Composite(this,SWT.NONE);
		GridLayout layoutSearch = new  GridLayout(1,false);
		layoutSearch.marginHeight=0;
		layoutSearch.marginWidth=0;

		compSearch.setLayout(layoutSearch);
		compSearch.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,1,1));
		compSearch.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);


		Group grpFilter = new Group( compSearch, SWT.NONE);
		grpFilter.setLayout(new GridLayout(1, false));
		grpFilter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpFilter.setText("Social Media/website search:");
		grpFilter.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);

		Composite composite_1 = new Composite(grpFilter, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_1.setLayout(new GridLayout(1, false));
		composite_1.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);

		int styleSbComposite = SWT.NONE;

		this.createBotComposites(composite_1, styleSbComposite);

		Group grpPostCode = new Group(compSearch, SWT.NONE);
		grpPostCode.setLayout(new GridLayout(2, false));
		GridData gd_grpPostCode = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_grpPostCode.widthHint=250;
		grpPostCode.setLayoutData(gd_grpPostCode);
		grpPostCode.setText("Lokatie");
		grpPostCode.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);
		grpPostCode.setEnabled(true);

		buttonIncludeLokatieInSearch = new Button(grpPostCode,SWT.CHECK);
		buttonIncludeLokatieInSearch.setText("Include in search");
		buttonIncludeLokatieInSearch.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);
		GridData gd = new GridData();
		gd.horizontalSpan=2;
		buttonIncludeLokatieInSearch.setLayoutData(gd);

		Label label = new Label(grpPostCode,SWT.NONE);
		label.setText("Plaats of Postcode");
		label.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);
		text_lokatie = new Text(grpPostCode, SWT.BORDER);
		text_lokatie.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		text_lokatie.setText("3512 JE");
		text_lokatie.setToolTipText("Vul in plaats of postcode");

		label = new Label(grpPostCode,SWT.NONE);
		label.setText("straal (km");
		label.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);
		text_radius = new Text(grpPostCode, SWT.BORDER);
		text_radius.setText("5");
		text_radius.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_radius.setToolTipText("Het zoekgebied is een criteria");


		Group grpDate = new Group(compSearch, SWT.NONE);
		GridData gd_grpDate = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_grpDate.widthHint = 200;
		grpDate.setLayoutData(gd_grpDate);
		grpDate.setText("Datum:");
		grpDate.setLayout(new GridLayout(2, false));
		grpDate.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);
		grpDate.setEnabled(false);

		buttonIncludeDatumInSearch = new Button(grpDate,SWT.CHECK);
		buttonIncludeDatumInSearch.setText("Include in search");
		gd = new GridData();
		gd.horizontalSpan=2;
		buttonIncludeDatumInSearch.setLayoutData(gd);
		buttonIncludeDatumInSearch.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);

		Label lblFrom = new Label(grpDate, SWT.NONE);
		lblFrom.setText("Van:");
		lblFrom.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);
		dateTime_frm = new DateTime(grpDate, SWT.BORDER + SWT.DATE + SWT.DROP_DOWN);
		dateTime_frm.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblTo = new Label(grpDate, SWT.NONE);
		lblTo.setText("Tot:");
		lblTo.setData(RWT.CUSTOM_VARIANT, RWT_FRONTEND);
		dateTime_to = new DateTime(grpDate, SWT.BORDER + SWT.DATE + SWT.DROP_DOWN);
		dateTime_to.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	}

	protected void putComposite( String name, Composite composite ){
		this.botComposites.put( name, composite);
	}

	protected abstract void executeSearchButton();

	/**
	 * Create the search bot composites that were registered through a declarative service
	 * @param parent
	 * @param style
	 */
	protected abstract void createBotComposites( Composite parent, int style );

	/**
	 * Get the composite with the given name
	 * @param name
	 * @return
	 */
	protected Composite getComposite( String name ){
		return botComposites.get( name );
	}

	protected abstract Composite implResultComposite(Composite parent,int style);

	protected void initValues(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int year = calendar.get( Calendar.YEAR );
		int month = calendar.get( Calendar.MONTH );
		int day = calendar.get( Calendar.DAY_OF_MONTH );
		if(dateTime_to!=null)
			dateTime_to.setDate(year, month, day);

		calendar.add(Calendar.DAY_OF_MONTH, -1);
		year = calendar.get( Calendar.YEAR );
		month = calendar.get( Calendar.MONTH );
		day = calendar.get( Calendar.DAY_OF_MONTH );
		if(dateTime_frm!=null)
			dateTime_frm.setDate(year, month, day);
	}
}