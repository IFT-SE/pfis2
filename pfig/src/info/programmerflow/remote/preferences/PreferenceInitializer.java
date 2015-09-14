package info.programmerflow.remote.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import info.programmerflow.remote.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences()  {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String path;
		try {
			path = Platform.getInstallLocation().getURL().getPath();
		} catch (Exception e) {
			path = System.getProperty("user.dir");
		}
		store.setDefault(PreferenceConstants.P_PATH, path);
		store.setDefault(PreferenceConstants.P_SERVER,
				"http://programmerflow.info/xmlrpc/");
//		"http://cobra.watson.ibm.com/xmlrpc/");
	}

}
