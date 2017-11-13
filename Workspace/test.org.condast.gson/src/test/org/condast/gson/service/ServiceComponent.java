package test.org.condast.gson.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import org.condast.commons.na.model.IApplication;
import org.condast.commons.persistence.service.IPersistencyController;
import org.condast.commons.ui.factory.ICompositeFactory;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Composite;

import nl.eetmee.commons.service.AbstractCFServiceComponent;

public class ServiceComponent extends AbstractCFServiceComponent<Object>{

	private static Logger logger = Logger.getLogger( ServiceComponent.class.getName() );

	private Collection<ICompositeFactory<?, IApplication>> factories;

	private static ServiceComponent component;
	
	public ServiceComponent() {
		super(null);
	}

	public static ICompositeFactory<Object, IApplication> getFactory(){
		return component;
	}
	
	@Override
	protected void onRunService() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean onFactoryAdded(ICompositeFactory<?, IApplication> fc) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * returns true if the given id is supported
	 * @param sc
	 * @return
	 */
	public static boolean isReady( ISupportedServices.Composites sc ){
		return component.isReady( sc.name() );
	}

	/**
	 * returns true if the given id is supported
	 * @param sc
	 * @return
	 */
	public static boolean isSupported( ISupportedServices.Composites sc ){
		String[] ids = component.getSupportedCompositeIDs();
		for( String id: ids ){
			if( sc.name().equals( id ))
				return true;
		}
		return false;
	}

	@Override 
	public String[] getSupportedCompositeIDs() {
		Collection<String> results = new ArrayList<String>();
		for( ICompositeFactory<?, IApplication> factory: this.factories ){
			results.addAll( Arrays.asList( factory.getSupportedCompositeIDs() ));
		}
		return results.toArray( new String[ results.size() ]);
	}

	@Override
	public Composite getComposite(String id, Composite parent, int style) {
		for( ICompositeFactory<?, IApplication> factory: this.factories ){
			Composite composite = factory.getComposite(id, parent, style);
			if( composite != null )
				return composite;
		}
		return null;
	}

	@Override
	public IWizard getWizard(String id, Composite parent, int style) {
		for( ICompositeFactory<?, IApplication> factory: this.factories ){
			IWizard wizard = factory.getWizard( id, parent, style);
			if( wizard != null )
				return wizard;
		}
		return null;
	}
	

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Get the composite represented by the given enum
	 * @param comp
	 * @param parent
	 * @param style
	 * @return
	 */
	public static IPersistencyController<?, IApplication> getController( ISupportedServices.Controllers contr ){
		return component.getService().getController( contr.name() );
	}
}