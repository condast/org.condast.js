package org.condast.commons.js.legal.core;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerConfiguration {

	private Configuration cfg;

	private Map<String, Object> root;


	public FreemarkerConfiguration() {
		super();
		root = new HashMap<>();
	}

	public void config( String templateDir) throws IOException {
		// Create your Configuration instance, and specify if up to what FreeMarker
		// version (here 2.3.26) do you want to apply the fixes that are not 100%
		// backward-compatible. See the Configuration JavaDoc for details.
		cfg = new Configuration(Configuration.VERSION_2_3_26);

		// Specify the source where the template files come from. Here I set a
		// plain directory for it, but non-file-system sources are possible too:
		cfg.setDirectoryForTemplateLoading(new File( templateDir));

		// Set the preferred charset template files are stored in. UTF-8 is
		// a good choice in most applications:
		cfg.setDefaultEncoding("UTF-8");

		// Sets how errors will appear.
		// During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

		// Don't log exceptions inside FreeMarker that it will thrown at you anyway:
		cfg.setLogTemplateExceptions(false);

		// Wrap unchecked exceptions thrown during template processing into TemplateException-s.
		//cfg.setWrapUncheckedExceptions(true);
	}

	public Template getTemplate( String template ) throws Exception {
		return  cfg.getTemplate( template);
	}

	public String writeOutput( String template ) throws Exception {
		Writer out = new StringWriter();
		Template temp = getTemplate(template);
		temp.process(root, out);
		return out.toString();
	}
}
