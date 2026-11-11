package org.condast.js.commons.freemarker;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import freemarker.core.Environment;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

public class TemplateEngine implements Closeable{

	public static final String S_CONDAST = "condast";
	public static final String S_CONDAST_MODEL = "/resources/CondastModel.properties";

	public static final String S_DYNAMIC = "dynamic";

	private static final String S_INFO_TEMPLATE_LOOKUP = "FreeMarker requested key: ";
	
	private final Configuration configuration;

	private Collection<ITemplateModelProvider> providers;
	
	private Map<String, Object> model;

    private List<IModelListener> listeners;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public TemplateEngine( String id ) {
		model = new HashMap<>();
        this.listeners = new ArrayList<>();

        providers = new ArrayList<>();
		configuration = new Configuration(
				Configuration.VERSION_2_3_32);

		configuration.setDefaultEncoding("UTF-8");
		configuration.setOutputFormat(
				HTMLOutputFormat.INSTANCE);

		configuration.setTemplateExceptionHandler(
				TemplateExceptionHandler.RETHROW_HANDLER);

		configuration.setLogTemplateExceptions(false);
	}

    public void addListener( IModelListener listener) {
    	this.listeners.add(listener);
    }

	public void removeListener( IModelListener listener) {
    	this.listeners.remove(listener);
    }
	
	public void createModel() {
		this.model.put(S_DYNAMIC, new ModelListener());
		InputStream inp = TemplateEngine.class.getResourceAsStream(S_CONDAST_MODEL);
		PropertiesModelLoader loader = new PropertiesModelLoader(S_CONDAST, inp );
		addProvider(loader);
	}
	
	public void addProvider( ITemplateModelProvider provider) {
		if(this.providers.contains(provider))
			return;
		this.providers.add(provider);
		this.includeModel(provider.getModel());
	}

	public void removeProvider( ITemplateModelProvider provider) {
		this.providers.remove(provider);
	}

	public void includeModel( Map<String, Object> source ) {
		mergeModels(model, source);
	}

	public void removeModel( String id ) {
		this.model.remove(id);
	}

	/**
	 * Process the model 
	 * @param fileName
	 * @param inp
	 * @param writer
	 * @throws IOException
	 * @throws TemplateException
	 */
	public void process( String fileName, InputStream inp, Writer writer)
					throws IOException, TemplateException {

		// Add the models of the providers to the model.
		for( ITemplateModelProvider p: this.providers )
			this.includeModel( p.getModel() );

		InputStreamReader reader =
				new InputStreamReader(
						inp, StandardCharsets.UTF_8);

		logger.fine("MODEL:\n" + toString());
		Template template = new Template(
				fileName, reader, configuration);

		//Create the correct form
		template.process(model, writer);
	}

	@SuppressWarnings("unchecked")
	protected void buildModel( StringBuilder builder, Map<String, Object> model ) {
		Iterator<Map.Entry<String, Object>> iterator = model.entrySet().iterator();
		while( iterator.hasNext()) {
			Map.Entry<String, Object> entry = iterator.next();
			builder.append( entry.getKey());
			builder.append(".");
			if( entry.getValue() instanceof Map)
				buildModel( builder, (Map<String, Object>) entry.getValue());
			else {
				builder.deleteCharAt( builder.length()-1);
				builder.append(":\t");
				builder.append( entry.getValue());
				builder.append("\n");
			}
		}
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MODEL:\n");
		buildModel( builder, this.model);
		return builder.toString();
	}

	@Override
	public void close() throws IOException {
		this.providers.clear();
	}

	@SuppressWarnings("unchecked")
	private static void mergeModels(
			Map<String, Object> target,
			Map<String, Object> source) {

		for (Map.Entry<String, Object> entry : source.entrySet()) {

			String key = entry.getKey();
			Object incoming = entry.getValue();
			Object existing = target.get(key);

			if (existing instanceof Map<?, ?> &&
					incoming instanceof Map<?, ?>) {

				mergeModels(
						(Map<String, Object>) existing,
						(Map<String, Object>) incoming);

			} else {
				target.put(key, incoming);
			}
		}  
	}
	
	private class ModelListener implements TemplateHashModel {

	    @Override
	    public TemplateModel get(String key)
	            throws TemplateModelException {

	        return new DynamicValue(key);
	    }

	    @Override
	    public boolean isEmpty() {
	        return false;
	    }

	    private class DynamicValue
	            implements TemplateHashModel, TemplateScalarModel {

	        private final String path;

	        public DynamicValue(String path) {
	            this.path = path;
	        }

	        @Override
	        public TemplateModel get(String attribute)
	                throws TemplateModelException {

	            return new DynamicValue(path + "." + attribute);
	        }

	        @Override
	        public String getAsString()
	                throws TemplateModelException {

	            String label = path.substring(
	                    path.lastIndexOf('.') + 1);

	            logger.fine(S_INFO_TEMPLATE_LOOKUP + path);

	            TemplateModel model = getModel(path);

	            for (IModelListener listener : listeners) {

	                ModelEvent event = new ModelEvent(
	                        ModelListener.this, label, model);

	                listener.notifyModelEvent(event);

	                if (event.hasChanged())
	                    return event.getValue();
	            }

	            throw new TemplateModelException(
	                    "No dynamic value found for: " + path);
	        }

	        @Override
	        public boolean isEmpty() {
	            return false;
	        }
	    }

	    private TemplateModel getModel(String path)
	            throws TemplateModelException {

	        Environment environment =
	                Environment.getCurrentEnvironment();

	        TemplateModel model = environment.getDataModel();

	        for (String attribute : path.split("\\.")) {

	            if (!(model instanceof TemplateHashModel))
	                return null;

	            model = ((TemplateHashModel) model).get(attribute);

	            if (model == null)
	                return null;
	        }

	        return model;
	    }
	}}