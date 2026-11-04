package org.condast.js.commons.freemarker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesModelLoader implements ITemplateModelProvider{

	private static final String S_ERR_INVALID_PROPERTY = "Invalid property: ";
	
	private String id;
	
	private Reader reader;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public PropertiesModelLoader(String id, File file) throws FileNotFoundException {
		this(id,  new FileReader( file));
	}

	public PropertiesModelLoader(String id, InputStream inp) {
		this(id,  new InputStreamReader( inp, StandardCharsets.UTF_8));
	}

	public PropertiesModelLoader(String id, Reader reader) {
		super();
		this.id = id;
		this.reader = reader;
	}

	public String getId() {
		return id;
	}
    
	@Override
	public Map<String, Object> getModel() {
		try {
			return load(reader );
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, Object> load(
			File file ) throws IOException {
		try (Reader reader = new FileReader( file)){
			return load( reader );
		}
	}

	public Map<String, Object> load(
			InputStream input) throws IOException {

		try (Reader reader = new InputStreamReader(
				input, StandardCharsets.UTF_8)) {
			return load( reader );
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> load(
			Reader reader) throws IOException {
		Properties properties = new Properties();
		properties.load(reader);

		Map<String, Object> model = new HashMap<>();

		for (String key : properties.stringPropertyNames()) {

			String[] parts = key.split("\\.");
			Map<String, Object> current = model;

			for (int i = 0; i < parts.length - 1; i++) {

				Object existing = current.get(parts[i]);

				if (existing == null) {
					Map<String, Object> child = new HashMap<>();
					current.put(parts[i], child);
					current = child;
				} else if (existing instanceof Map) {
					current = (Map<String, Object>) existing;
				} else {
					throw new IllegalArgumentException(
							ITemplateModelProvider.S_ERR_CONFLICTING_PROPERTY + key);
				}
			}

			String last = parts[parts.length - 1];

			if (current.containsKey(last)) {
				throw new IllegalArgumentException(
						S_ERR_INVALID_PROPERTY + key);
			}
			current.put( last, properties.getProperty(key));   
		}

		return model;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.id);
		builder.append(": ");
		builder.append( super.toString());
		return builder.toString();
	}
}