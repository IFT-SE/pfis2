package info.programmerflow.remote;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IPreferencesService;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * PFISPlugin is the main plugin class. It establishes a GUID for the user and creates log entries.
 * @author jalawran
 *
 */
public class PFISPlugin {
	private final static int INTERVAL = 60*60*1000; // Hourly interval
	private static UUID session;
	private static String referrer;
	private static String user;
	private static PFISPlugin instance;

	// Maintain a cache of what was logged previously (used in logOnce method)
	private static ConcurrentHashMap<UUID,UUID> cache = new ConcurrentHashMap<UUID,UUID>();

	/**
	 * Flush out the cache at hourly intervals to avoid memory problems.
	 * @author jalawran
	 *
	 */
	private static class FlushJob extends Job {

		public FlushJob() {
			super("Flush cache job");
			setSystem(true);
		}

		@Override
		protected IStatus run(IProgressMonitor arg0) {
			log("Cache flush","Size",Integer.toString(cache.size()));
			cache.clear();
			schedule(INTERVAL);
			return Status.OK_STATUS;
		}

	}
	/**
	 * PFISPlugin is a singleton class
	 */
	private PFISPlugin() {
		// Generate GUID unique to the session
		session = UUID.randomUUID();
		// Register listeners
		AllListener.getInstance().registerListeners();
	}
	public static PFISPlugin getInstance() {
		if (instance == null) {
			instance = new PFISPlugin();
			new FlushJob().schedule(INTERVAL);
		}
		return instance;
	}
	public static void writeLog(String action, String target, String ref) {
/*		if (action == null)
			try {
				throw new Exception("hey!");
			} catch (Exception e1) {
				e1.printStackTrace();
			}*/
		if (ref != null) referrer = ref; 
		Object[] params = new Object[]{getUser(),new Long(System.currentTimeMillis()).toString(),action,target,referrer,session.toString()};
		try {
			LoggerJob.log(params);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		referrer = target;
	}
	public static void log(String action, String target, String ref) {
		writeLog(action, target, ref);
	}
	public static void logException(Exception e) {
		StringWriter w = new StringWriter();
		PrintWriter p = new PrintWriter(w);
		e.printStackTrace(p);
		log("Exception", e.toString(), w.toString());
	}
	public static void logOnce(String action, String target, String ref) {
		UUID key = UUID.nameUUIDFromBytes((action+target+ref).getBytes());
		if (!cache.containsKey(key)) {
			cache.put(key, key);
			log(action,target,ref);
		}
	}
	/**
	 * Returns a GUID unique to the user.
	 */
	public static String getUser() {
		if (user == null) {
			IPreferencesService s = Platform.getPreferencesService();
			Preferences[] nodes = {new ConfigurationScope().getNode(Activator.PLUGIN_ID)};
			user = s.get("user", "pfis", nodes);
			if (user.equals("pfis")) {
				UUID uuid = UUID.randomUUID();
				user = uuid.toString();
				nodes[0].put("user", user);
				try {
					nodes[0].flush();
				} catch (BackingStoreException e) {
				}
			}
		}
		return user;
	}
	/**
	 * Send startup log data about the platform, product, OS, architecture, IP, Host.
	 */
	public void start() {
		log("Begin", "", "");
		log("User / Database Directory", System.getProperty("user.dir"), Activator.getDatabase());
		log("Session","Plugin version","PFIS 1.3");
		log("Session","OS",Platform.getOS());
		log("Session","Architecture",Platform.getOSArch());
		log("Session","Product",Platform.getProduct().getDescription()); //.getDescription());
		try {
			InetAddress addr = InetAddress.getLocalHost();
			log("Session","IP", addr.getHostAddress());
			log("Session","Host", addr.getHostName());
		} catch (UnknownHostException e) {
		}
	}
	
}
