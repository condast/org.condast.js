package test.condast.community;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.condast.commons.io.IOUtils;
import org.condast.commons.project.ProjectFolderUtils;
import org.condast.commons.strings.StringUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import test.condast.community.core.CommunityParser;
import test.condast.community.core.CommunityParser.Resources;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private ExecutorService service;
	
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				logger.info("PARSING FILES: ");
				CommunityParser parser = CommunityParser.getDefaultParser();
				CommunityParser.ComData[] data = parser.parse();
				Gson gson = new Gson();
				String results = gson.toJson(data, CommunityParser.ComData[].class);
				logger.info("\n\n" + results );
				String userdir = ProjectFolderUtils.getDefaultUserDir("tmp" );
				File file = new File( userdir, Resources.POSTCODE.toString() );
				if(!file.exists()) {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				FileWriter writer = new FileWriter( file );
				try {
					writer.write(results);
				}
				finally {
					IOUtils.close(writer );
				}
				logger.info("COMPLETE: ");
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}
	};
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		//service = Executors.newCachedThreadPool();
		//service.execute(runnable);
		String userdir = ProjectFolderUtils.getDefaultUserDir("tmp" );
		File file = new File( userdir, Resources.POSTCODE.toString() );
		if(!file.exists()) {
			logger.info("File not found: " + file.getPath());
			return;
		}
		String[] pc = CommunityParser.getPostcodes(file, "Appingedam");  		
		StringBuilder builder = new StringBuilder();
		for( String str: pc )
			builder.append(str);
		logger.info(builder.toString());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		service.shutdown();
		Activator.context = null;
	}

}
