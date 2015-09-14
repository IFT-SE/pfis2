package info.programmerflow.remote;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	public void earlyStartup() {

		PFISPlugin plugin = PFISPlugin.getInstance();
		plugin.start();
	}

}
