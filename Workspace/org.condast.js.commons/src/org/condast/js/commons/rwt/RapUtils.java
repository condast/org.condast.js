package org.condast.js.commons.rwt;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.JavaScriptExecutor;

public class RapUtils {

	public static void jump(String path) {

	    JavaScriptExecutor executor =
	            RWT.getClient().getService(JavaScriptExecutor.class);

	    executor.execute("window.top.location.replace(" +
	            JsonValue.valueOf(path).toString() + ");");
	}
}
