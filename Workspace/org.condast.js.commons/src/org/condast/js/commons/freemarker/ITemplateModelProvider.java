package org.condast.js.commons.freemarker;

import java.util.Map;

public interface ITemplateModelProvider {

	 String S_ERR_CONFLICTING_PROPERTY = "Conflicting property: ";

	 Map<String, Object> getModel();
}
