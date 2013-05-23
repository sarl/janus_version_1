/* 
 * $Id$
 * 
 * Copyright (c) 2004-10, Janus Core Developers <Sebastian RODRIGUEZ, Nicolas GAUD, Stephane GALLAND>
 * All rights reserved.
 *
 * http://www.janus-project.org
 */
package org.janusproject.extras.ui.eclipse.base.utils;

import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * A Simple Console that redirects System.err and System.out to this console.
 * 
 * @author Sebastian RODRIGUEZ &lt;sebastian@sebastianrodriguez.com.ar&gt;
 * @version $FullVersion$
 * @mavengroupid org.janus-project.kernel
 * @mavenartifactid xxxx
 * 
 */
public class SystemConsole extends MessageConsole {
	private static final SystemConsole instance = new SystemConsole();
	private MessageConsoleStream errStream;
	private MessageConsoleStream outStream;

	private SystemConsole() {
		super("System Console", null);
		errStream = new MessageConsoleStream(this);
		errStream.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		outStream = new MessageConsoleStream(this);

		System.setErr(new PrintStream(errStream));
		System.setOut(new PrintStream(outStream));
	}
	
	public static final SystemConsole getSystemConsole(){
		return instance;
	}
}
