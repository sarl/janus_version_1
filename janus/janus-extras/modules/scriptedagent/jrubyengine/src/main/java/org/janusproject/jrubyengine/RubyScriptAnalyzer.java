/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2010-2012 Janus Core Developers
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
package org.janusproject.jrubyengine;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.arakhne.vmutil.locale.Locale;

/**
 * This class is used to generate the list of classes and functions of a ruby file
 * 
 * @author $Author: sgalland$
 * @author $Author: ngaud$
 * @author $Author: gvinson$
 * @author $Author: rbuecher$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class RubyScriptAnalyzer {

	/**
	 * The String to recognize a class
	 */
	private final static String CLASSE = "class "; //$NON-NLS-1$

	/**
	 * The String to recognize a function
	 */
	private final static String FONC = "def "; //$NON-NLS-1$

	/**
	 * The String to recognize the of of a function or class
	 */
	private final static String END = "end"; //$NON-NLS-1$

	private final Logger logger = Logger.getLogger(this.getClass().toString());

	/**
	 * Attributs use to stock the list of classes and functions
	 */
	private Map<String, List<String>> classesAndFunctionsList;

	/**
	 * path of the script to analyze
	 */
	private String path;

	/**
	 * Default constructor
	 */
	public RubyScriptAnalyzer() {
		this.path = ""; //$NON-NLS-1$
		this.classesAndFunctionsList = new HashMap<String, List<String>>();
	}

	/**
	 * 
	 * @param path
	 */
	public RubyScriptAnalyzer(String path) {
		this.path = path;
		this.classesAndFunctionsList = new HashMap<String, List<String>>();
		generateListe();
	}

	/**
	 * Function use to generate the list of classes and function and stock it in the hashmap "liste"
	 * 
	 * @param path
	 *            : absolute path of the ruby file
	 */
	public void generateListe(String path) {
		File f = new File(path);
		
		if (!(f.exists() && f.isFile())) {
			this.logger.severe(Locale.getString(RubyScriptAnalyzer.class, "NOT_A_VALID_PATH", this.path)); //$NON-NLS-1$
			return;
		}
		
		String tempClasse = ""; //$NON-NLS-1$
		String tempFonc = ""; //$NON-NLS-1$
		int cptEnd = 0;
		List<String> tliste = new ArrayList<String>();

		try {
			FileInputStream fstream = new FileInputStream(path);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			try {
				while ((strLine = br.readLine()) != null) {
					// if it's the begin of a class
					if ((strLine.indexOf(CLASSE) == 0 && strLine.charAt(CLASSE.length() - 1) == ' ') || (strLine.indexOf('\t' + CLASSE) >= 0 || strLine.indexOf(' ' + CLASSE) >= 0)) {
						tempClasse = strLine.substring(strLine.indexOf(CLASSE) + 6, strLine.length()).trim();
						// if(tempClasse.indexOf(' ')>0)
						// tempClasse=tempClasse.substring(0, tempClasse.indexOf(' '));
						cptEnd++;
					}
					// if it's the begin of a function
					if ((strLine.indexOf(FONC) >= 0 && strLine.charAt(FONC.length() - 1) == ' ') || strLine.indexOf('\t' + FONC) >= 0 || strLine.indexOf(' ' + FONC) >= 0) {
						tempFonc = strLine.substring(strLine.indexOf(FONC) + 4, strLine.length()).trim();
						// if(tempFonc.indexOf(' ')>0)
						// tempFonc=tempFonc.substring(0, tempFonc.indexOf(' '));
						tliste.add(tempFonc);
						cptEnd++;
					}
					// if it's the end of function or class
					if ((strLine.indexOf(END) >= 0 && strLine.length() == END.length()) || ((strLine.indexOf('\t' + END) >= 0 || strLine.indexOf(' ' + END) >= 0) && strLine.indexOf(END) + END.length() == strLine.length())) {
						cptEnd--;
						if (cptEnd == 0) {
							if (tempClasse == "" && this.classesAndFunctionsList.containsKey("")) { //$NON-NLS-1$ //$NON-NLS-2$
								// end of function
								this.classesAndFunctionsList.get("").add(tempFonc); //$NON-NLS-1$
							} else
								// end of class
								getClassesAndFunctionsList().put(tempClasse, new ArrayList<String>(tliste));
							tliste.clear();
							tempClasse = ""; //$NON-NLS-1$
							tempFonc = ""; //$NON-NLS-1$
						}
					}
				}
			} catch (IOException e1) {
				this.logger.severe(Locale.getString(RubyScriptAnalyzer.class, "IO_EXCEPTION", e1.getMessage())); //$NON-NLS-1$
			}
			in.close();
		} catch (FileNotFoundException e) {
			this.logger.severe(Locale.getString(RubyScriptAnalyzer.class, "NOT_A_VALID_PATH", this.path)); //$NON-NLS-1$
		} catch (IOException e) {
			this.logger.severe(Locale.getString(RubyScriptAnalyzer.class, "IO_EXCEPTION", e.getMessage())); //$NON-NLS-1$
		}
	}

	/**
	 * Generate the hashmap containing classes and functions names presents in the ruby script functions in class have the class name for key functions out of a class have "" for key
	 */
	public void generateListe() {
		if (this.path != "") { //$NON-NLS-1$
			generateListe(this.path);
		} else {
			this.logger.severe(Locale.getString(RubyScriptAnalyzer.class, "NOT_A_VALID_PATH", this.path)); //$NON-NLS-1$
		}
	}

	/**
	 * show the content of "liste" in the consol
	 */
	public void printClassesAndFunctionsList() {
		if (this.classesAndFunctionsList.size() == 0) {
			generateListe();
		}

		for (Entry<String, List<String>> entry : this.getClassesAndFunctionsList().entrySet()) {
			if (!entry.getKey().equals("")) { //$NON-NLS-1$
				System.out.println("class " + entry.getKey()); //$NON-NLS-1$
			}
			for (String f : entry.getValue()) {
				if (entry.getKey().equals("")) { //$NON-NLS-1$
					this.logger.info("def " + f); //$NON-NLS-1$
				} else {
					this.logger.info("\tdef " + f); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * @return an list containing just classes of the liste
	 */
	public List<String> getClassList() {
		List<String> l = new ArrayList<String>();
		for (Entry<String, List<String>> entry : this.getClassesAndFunctionsList().entrySet()) {
			l.add(entry.getKey());
		}
		return l;
	}

	/**
	 * Return true if the Class name is found in the file (default path)
	 * 
	 * @param c - Class name to search
	 * @return true if the Class name is found in the file (default path)
	 */
	public boolean existClass(String c) {
		return existClass(c, this.path);
	}

	/**
	 * Return true if the Class name is found in the file
	 * 
	 * @param c - Class name to search
	 * @param path - the path of the script
	 * @return true if the Class name is found in the file
	 */
	public boolean existClass(String c, String path) {
		generateListe(path);
		for (Entry<String, List<String>> entry : this.getClassesAndFunctionsList().entrySet()) {
			if (entry.getKey().equals(c.trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return true if the Function name is found in the file (default path)
	 * 
	 * @param f - Function name to search
	 * @return true if the Function name is found in the file (default path)
	 */
	public boolean existFunction(String f) {
		return existFunction(f, this.path);
	}

	/**
	 * Return true if the Function name is found in the file
	 * 
	 * @param f - Function name to search
	 * @param path - path of the file to test
	 * @return true if the Function name is found in the file
	 */
	public boolean existFunction(String f, String path) {
		generateListe(path);
		for (Entry<String, List<String>> entry : this.getClassesAndFunctionsList().entrySet()) {
			for (String func : entry.getValue()) {
				if (func.equals(f.trim())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the path to the directory to be tested
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @return the list of available ruby classes and functions
	 */
	public Map<String, List<String>> getClassesAndFunctionsList() {
		return this.classesAndFunctionsList;
	}

	/**
	 * 
	 * @param list
	 */
	public void setClassesAndFunctionsList(Map<String, List<String>> list) {
		this.classesAndFunctionsList = list;
	}

}
