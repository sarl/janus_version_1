/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2012 Janus Core Developers
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.janusproject.scriptedagent;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptEngineManager;

import org.arakhne.vmutil.FileSystem;
import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.scriptedagent.exception.InvalidDirectoryException;

/**
 * Agent which is able to run scripts.
 * 
 * @author $Author: sgalland$
 * @author $Author: cwintz$
 * @author $Author: ngaud$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class ScriptedAgent extends Agent {

	private static final long serialVersionUID = -6624883872941692209L;

	/** Inactivity delay (in milliseconds) allowed for the internal tee manager.
	 * The tee manager is providing a convenient way to catch the outputs of
	 * the commands when they are launched as inside an interactive interpreter.
	 */
	static final long TEE_MANAGER_RELEASE_DELAY = 60000;
	
	private static SoftReference<ScriptEngineManager> sharedManager = null;

	/** Replies the manager of script engines that may be shared between all the
	 * scripts agents.
	 * @return the manager of script engines.
	 */
	public static ScriptEngineManager getSharedScriptEngineManager() {
		synchronized(ScriptedAgent.class) {
			ScriptEngineManager manager = (sharedManager==null) ? null : sharedManager.get();
			if (manager==null) {
				manager = new ScriptEngineManager();
				sharedManager = new SoftReference<>(manager);
			}
			return manager;
		}
	}

	private ScriptExecutionContext scriptExecutor;

	private TeeManager teeManager = null;

	/**
	 * Creates a new scripted agent.
	 * 
	 * @param interpreter is the script interpreter to use.
	 */
	public ScriptedAgent(ScriptExecutionContext interpreter) {
		super();
		this.scriptExecutor = interpreter;
		initRepository();
	}

	private void initRepository() {
		ScriptRepository repos = this.scriptExecutor.getScriptRepository();
		if (repos==null) {
			repos = new ScriptRepository();
			this.scriptExecutor.setScriptRepository(repos);
		}
		if (repos.isEmpty()) {
			ScriptPath pathAnnotation = getClass().getAnnotation(ScriptPath.class);
			if (pathAnnotation!=null) {
				String[] paths = pathAnnotation.paths();
				if (paths!=null && paths.length>0) {
					for(String path : paths) {
						repos.addDirectory(FileSystem.convertStringToURL(path, false, true));
					}
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		this.scriptExecutor.setLogger(getLogger());
		return StatusFactory.ok(this);
	}

	private void ensureTeeManager() {
		if (this.teeManager==null) {
			this.teeManager = new TeeManager(
					getScriptExecutor(),
					getKernelContext().getScheduledExecutorService());
		}
		else {
			this.teeManager.preExecution(getScriptExecutor());
		}
	}
	
	private void releaseTeeManager() {
		if (this.teeManager!=null) {
			this.teeManager.resetWriters(getScriptExecutionContext());
			this.teeManager = null;
		}
	}

	/**
	 * Returns the script context associated with this agent.
	 * 
	 * @return the scriptExecutor
	 */
	protected final ScriptExecutionContext getScriptExecutor() {
		return this.scriptExecutor;
	}

	/**
	 * Set the script context to be used by this agent.
	 * 
	 * @param scriptExecutor is the scriptExecutor to set
	 */
	protected final void setScriptExecutor(ScriptExecutionContext scriptExecutor) {
		assert(scriptExecutor!=null);
		this.scriptExecutor = scriptExecutor;
		try {
			initRepository();
		}
		catch (InvalidDirectoryException e) {
			Logger logger = getLogger();
			if (logger!=null) {
				logger.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * Returns the script repository where scripts files are located.
	 * 
	 * @return the script repository.
	 */
	protected final ScriptRepository getScriptRepository() {
		return this.scriptExecutor.getScriptRepository();
	}

	/**
	 * Replies the context of execution of the scripts.
	 * 
	 * @return the script context.
	 */
	protected final ScriptExecutionContext getScriptExecutionContext() {
		return this.scriptExecutor;
	}

	/** Set the standard input to by used by the interpreter.
	 * 
	 * @param stdin
	 */
	protected final void setScriptStandardInput(Reader stdin) {
		this.scriptExecutor.setStandardInput(stdin);
	}

	/** Set the standard output to by used by the interpreter.
	 * 
	 * @param stdout
	 */
	protected final void setScriptStandardOutput(Writer stdout) {
		this.scriptExecutor.setStandardOutput(stdout);
	}

	/** Set the standard error output to by used by the interpreter.
	 * 
	 * @param stderr
	 */
	protected final void setScriptStandardError(Writer stderr) {
		this.scriptExecutor.setStandardError(stderr);
	}

	/**
	 * Evaluate a script with the given basename in the
	 * directories managed by the script repository.
	 * <p>
	 * The text printed out on the standard outputs are not replied.
	 * See {@link #runScriptAsInteractiveInterpreter(String)} to obtain the
	 * same output as inside an interactive interpreter.
	 * 
	 * @param scriptBasename is the basename targeting the script file.
	 * @return the value returned by the script.
	 */
	protected final Object runScript(String scriptBasename) {
		return this.scriptExecutor.runScript(scriptBasename);
	}

	/**
	 * Evaluate a script with the given basename in the
	 * directories managed by the script repository.
	 * <p>
	 * If you want to obtain the real result of the execution,
	 * and not only the printed-out text, see
	 * {@link #runScript(String)}.
	 * <p>
	 * This function capture the output streams (standard and
	 * error) and append them to the result of the command. 
	 * 
	 * @param scriptBasename is the basename targeting the script file.
	 * @return the value returned by the script.
	 */
	protected final String runScriptAsInteractiveInterpreter(String scriptBasename) {
		ensureTeeManager();
		Object r = runScript(scriptBasename);
		return this.teeManager.postExecution(r);
	}

	/**
	 * Evaluate a script in the given file.
	 * <p>
	 * The text printed out on the standard outputs are not replied.
	 * See {@link #runScriptAsInteractiveInterpreter(File)} to obtain the
	 * same output as inside an interactive interpreter.
	 * 
	 * @param scriptFilename
	 * @return the value returned by the script.
	 */
	protected final Object runScript(File scriptFilename) {
		return this.scriptExecutor.runScript(scriptFilename);
	}

	/**
	 * Evaluate a script in the given file.
	 * <p>
	 * If you want to obtain the real result of the execution,
	 * and not only the printed-out text, see
	 * {@link #runScript(File)}.
	 * <p>
	 * This function capture the output streams (standard and
	 * error) and append them to the result of the command. 
	 * 
	 * @param scriptFilename
	 * @return the value returned by the script.
	 */
	protected final String runScriptAsInteractiveInterpreter(File scriptFilename) {
		ensureTeeManager();
		Object r = runScript(scriptFilename);
		return this.teeManager.postExecution(r);
	}

	/**
	 * Evaluate a script in the given file.
	 * <p>
	 * The text printed out on the standard outputs are not replied.
	 * See {@link #runScriptAsInteractiveInterpreter(URL)} to obtain the
	 * same output as inside an interactive interpreter.
	 * 
	 * @param scriptFilename
	 * @return the value returned by the script.
	 */
	protected final Object runScript(URL scriptFilename) {
		return this.scriptExecutor.runScript(scriptFilename);
	}

	/**
	 * Evaluate a script in the given file.
	 * <p>
	 * If you want to obtain the real result of the execution,
	 * and not only the printed-out text, see
	 * {@link #runScript(URL)}.
	 * <p>
	 * This function capture the output streams (standard and
	 * error) and append them to the result of the command. 
	 * 
	 * @param scriptFilename
	 * @return the value returned by the script.
	 */
	protected final String runScriptAsInteractiveInterpreter(URL scriptFilename) {
		ensureTeeManager();
		Object r = runScript(scriptFilename);
		return this.teeManager.postExecution(r);
	}

	/**
	 * Run the command in the context and return the result
	 * of the evaluation.
	 * <p>
	 * The text printed out on the standard outputs are not replied.
	 * See {@link #runCommandAsInteractiveInterpreter(String)} to obtain the
	 * same output as inside an interactive interpreter.
	 * 
	 * @param aCommand
	 *            is the command which will be executed in the context
	 * @return the return of the script context for the command.
	 */
	protected final Object runCommand(String aCommand) {
		return this.scriptExecutor.runCommand(aCommand);
	}

	/**
	 * Run the command in the context and replies all the
	 * things printed out on the standard output or on
	 * the standard error.
	 * <p>
	 * If you want to obtain the real result of the execution,
	 * and not only the printed-out text, see
	 * {@link #runCommand(String)}.
	 * <p>
	 * This function capture the output streams (standard and
	 * error) and append them to the result of the command. 
	 * 
	 * @param aCommand
	 *            is the command which will be executed in the context
	 * @return the return of the script context for the command.
	 */
	protected final String runCommandAsInteractiveInterpreter(String aCommand) {
		ensureTeeManager();
		Object r = runCommand(aCommand);
		return this.teeManager.postExecution(r);
	}

	/**
	 * Invokes the function with the given name and the given parameters, and that
	 * is defined in the given script. The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * <p>
	 * The text printed out on the standard outputs are not replied.
	 * See {@link #runFunctionAsInteractiveInterpreter(String, String, Object...)}
	 * to obtain the same output as inside an interactive interpreter.
	 * 
	 * @param scriptBasename is the basename targeting the script file.
	 * @param functionName is the name of the function to invoke.
	 * @param params is the list of the parameters to pass to the function.
	 * @return the default output
	 */
	protected final Object runFunction(String scriptBasename, String functionName, Object... params) {
		return this.scriptExecutor.runFunction(scriptBasename, functionName, params);
	}

	/**
	 * Invokes the function with the given name and the given parameters, and that
	 * is defined in the given script. The script to load is locaded in
	 * one of the directories managed by the script directory repository.
	 * <p>
	 * If you want to obtain the real result of the execution,
	 * and not only the printed-out text, see
	 * {@link #runFunction(String, String, Object...)}.
	 * <p>
	 * This function capture the output streams (standard and
	 * error) and append them to the result of the command. 
	 * 
	 * @param scriptBasename is the basename targeting the script file.
	 * @param functionName is the name of the function to invoke.
	 * @param params is the list of the parameters to pass to the function.
	 * @return the default output
	 */
	protected final String runFunctionAsInteractiveInterpreter(String scriptBasename, String functionName, Object... params) {
		ensureTeeManager();
		Object r = runFunction(scriptBasename, functionName, params);
		return this.teeManager.postExecution(r);
	}

	/**
	 * Invokes the function with the given name and the given parameters, and that
	 * is defined in the given script.
	 * <p>
	 * The text printed out on the standard outputs are not replied.
	 * See {@link #runFunctionAsInteractiveInterpreter(File, String, Object...)}
	 * to obtain the same output as inside an interactive interpreter.
	 * 
	 * @param scriptFilename is the filename of the script to load.
	 * @param functionName is the name of the function to invoke.
	 * @param params is the list of the parameters to pass to the function.
	 * @return the default output
	 */
	protected final Object runFunction(File scriptFilename, String functionName, Object... params) {
		return this.scriptExecutor.runFunction(scriptFilename, functionName, params);
	}

	/**
	 * Invokes the function with the given name and the given parameters, and that
	 * is defined in the given script.
	 * <p>
	 * If you want to obtain the real result of the execution,
	 * and not only the printed-out text, see
	 * {@link #runFunction(File, String, Object...)}.
	 * <p>
	 * This function capture the output streams (standard and
	 * error) and append them to the result of the command. 
	 * 
	 * @param scriptFilename is the filename of the script to load.
	 * @param functionName is the name of the function to invoke.
	 * @param params is the list of the parameters to pass to the function.
	 * @return the default output
	 */
	protected final Object runFunctionAsInteractiveInterpreter(File scriptFilename, String functionName, Object... params) {
		ensureTeeManager();
		Object r = runFunction(scriptFilename, functionName, params);
		return this.teeManager.postExecution(r);
	}

	/**
	 * Invokes the function with the given name and the given parameters, and that
	 * is defined in the given script.
	 * <p>
	 * The text printed out on the standard outputs are not replied.
	 * See {@link #runFunctionAsInteractiveInterpreter(URL, String, Object...)}
	 * to obtain the same output as inside an interactive interpreter.
	 * 
	 * @param scriptFilename is the filename of the script to load.
	 * @param functionName is the name of the function to invoke.
	 * @param params is the list of the parameters to pass to the function.
	 * @return the default output
	 */
	protected final Object runFunction(URL scriptFilename, String functionName, Object... params) {
		return this.scriptExecutor.runFunction(scriptFilename, functionName, params);
	}

	/**
	 * Invokes the function with the given name and the given parameters, and that
	 * is defined in the given script.
	 * <p>
	 * If you want to obtain the real result of the execution,
	 * and not only the printed-out text, see
	 * {@link #runFunction(URL, String, Object...)}.
	 * <p>
	 * This function capture the output streams (standard and
	 * error) and append them to the result of the command. 
	 * 
	 * @param scriptFilename is the filename of the script to load.
	 * @param functionName is the name of the function to invoke.
	 * @param params is the list of the parameters to pass to the function.
	 * @return the default output
	 */
	protected final Object runFunctionAsInteractiveInterpreter(URL scriptFilename, String functionName, Object... params) {
		ensureTeeManager();
		Object r = runFunction(scriptFilename, functionName, params);
		return this.teeManager.postExecution(r);
	}

	/**
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.5
	 */
	private static class TeeWriter extends Writer {

		private final Writer otherWriter;
		private final StringBuilder buffer;

		/**
		 * @param buffer is the buffer to write in.
		 * @param otherWriter is the other writer to write in.
		 */
		public TeeWriter(StringBuilder buffer, Writer otherWriter) {
			assert(buffer!=null);
			this.otherWriter = otherWriter;
			this.buffer = buffer;
		}

		/** Replies the wrapped writer.
		 * 
		 * @return the wrapped writer.
		 */
		public Writer getWrappedWriter() {
			return this.otherWriter;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			this.buffer.append(cbuf, off, len);
			if (this.otherWriter!=null)
				this.otherWriter.write(cbuf, off, len);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void flush() throws IOException {
			if (this.otherWriter!=null)
				this.otherWriter.flush();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void close() throws IOException {
			if (this.otherWriter!=null) 
				this.otherWriter.close();
		}

	}

	/**
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.5
	 */
	private class TeeManager implements Runnable {

		private final StringBuilder buffer = new StringBuilder();
		private TeeWriter stdout;
		private TeeWriter stderr;
		private long lastActivity = 0;
		private ScheduledFuture<?> future = null; 

		/**
		 * @param context is the context to associate to this manager.
		 * @param executionService is the service that may be used to release the TeeManager when not used.
		 */
		public TeeManager(ScriptExecutionContext context, ScheduledExecutorService executionService) {
			this.stdout = new TeeWriter(this.buffer, context.getStandardOutput());
			this.stderr = new TeeWriter(this.buffer, context.getStandardError());
			context.setStandardOutput(this.stdout);
			context.setStandardError(this.stderr);
			this.lastActivity = System.currentTimeMillis();
			if (executionService!=null) {
				this.future = executionService.scheduleWithFixedDelay(this, TEE_MANAGER_RELEASE_DELAY, TEE_MANAGER_RELEASE_DELAY, TimeUnit.MILLISECONDS);
			}
		}
		
		/** Restore the writers.
		 * 
		 * @param context is the context to associate to this manager.
		 */
		public void resetWriters(ScriptExecutionContext context) {
			if (this.future!=null) {
				this.future.cancel(true);
			}
			context.setStandardOutput(this.stdout.getWrappedWriter());
			context.setStandardError(this.stderr.getWrappedWriter());
		}

		/** Reset the buffer to prepare the execution of a new command.
		 * 
		 * @param context is the context to associate to this manager.
		 */
		public void preExecution(ScriptExecutionContext context) {
			Writer w;
			this.lastActivity = System.currentTimeMillis();
			this.buffer.setLength(0);
			w = context.getStandardOutput();
			if (w!=this.stdout.getWrappedWriter()) {
				this.stdout = new TeeWriter(this.buffer, w);
			}
			w = context.getStandardError();
			if (w!=this.stderr.getWrappedWriter()) {
				this.stderr = new TeeWriter(this.buffer, w);
			}
		}

		/** Replies the buffered text.
		 * 
		 * @param commandResult is the result of the command to put in the buffer.
		 * @return the buffer content.
		 */
		public String postExecution(Object commandResult) {
			if (commandResult!=null) {
				String rr = commandResult.toString();
				this.buffer.append(rr);
				if (!rr.endsWith("\n")) //$NON-NLS-1$
					this.buffer.append("\n"); //$NON-NLS-1$
			}
			String r = this.buffer.toString();
			this.buffer.setLength(0);
			return r;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void run() {
			long current = System.currentTimeMillis();
			if (current > this.lastActivity+TEE_MANAGER_RELEASE_DELAY) {
				releaseTeeManager();
			}
		}

	}

}
