package com.example.e4.rcp.todo.lifecycle;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

import com.example.e4.rcp.todo.dialogs.ServerLoginDialog;
import com.example.e4.rcp.todo.preferences.PreferenceConstants;

public class Manager {

	public static final String SERVER_URI_DEFAULT = "http://localhost:8080/todo";
	// We add the nodePath in case you move the lifecycle handler to
	// another plug-in later
	@Inject
	@Preference(nodePath = PreferenceConstants.TODO_NODEPATH, value = PreferenceConstants.USER_PREF_KEY)
	private String user;

	@Inject
	@Preference(nodePath = PreferenceConstants.TODO_NODEPATH, value = PreferenceConstants.SERVER_URI_PREF_KEY)
	private String serverUri;

	@PostContextCreate
	public void postContextCreate(@Preference IEclipsePreferences prefs,
			IApplicationContext appContext, Display display) {

		final Shell shell = new Shell(SWT.TOOL | SWT.NO_TRIM);
		ServerLoginDialog dialog = new ServerLoginDialog(shell);
		if (user != null) {
			dialog.setUser(user);
		}
		dialog.setServerUri(serverUri != null ? serverUri : SERVER_URI_DEFAULT);

		// close the static splash screen
		appContext.applicationRunning();

		// position the shell
		 setLocation(display, shell);
		
		// open the dialog
		if (dialog.open() != Window.OK) {
			// close the application
			System.exit(-1);
		} else {
			// get the user from the dialog
			String userValue = dialog.getUser();
			// store the user values in the preferences
			prefs.put(PreferenceConstants.USER_PREF_KEY, userValue);

			String dialogsServerUri = dialog.getServerUri();
			prefs.put(PreferenceConstants.SERVER_URI_PREF_KEY, dialogsServerUri);
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		}
		
	}

	private void setLocation(Display display, Shell shell) {
		Monitor monitor = display.getPrimaryMonitor();
		Rectangle monitorRect = monitor.getBounds();
		Rectangle shellRect = shell.getBounds();
		int x = monitorRect.x + (monitorRect.width - shellRect.width) / 2;
		int y = monitorRect.y + (monitorRect.height - shellRect.height) / 2;
		shell.setLocation(x, y);
	}
}