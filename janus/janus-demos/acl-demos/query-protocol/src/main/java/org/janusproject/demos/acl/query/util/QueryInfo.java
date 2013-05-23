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
package org.janusproject.demos.acl.query.util;

import org.janusproject.acl.Performative;

/** Describes the information about a query.
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class QueryInfo {
	private String query;
	private Performative performative;
	private String ontology;
	private String language;
	
	/**
	 * 
	 * @param query is the query description.
	 * @param performative is the performative of the query.
	 * @param ontology is the ontology of the query.
	 * @param language is the language of the query.
	 */
	public QueryInfo(String query, Performative performative, String ontology, String language) {
		this.query = query;
		this.performative = performative;
		this.ontology = ontology;
		this.language = language;
	}

	/** Replies the query.
	 * 
	 * @return the query.
	 */
	public String getQuery() {
		return this.query;
	}

	/** Change the query.
	 * 
	 * @param query
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/** Replies the performative.
	 * 
	 * @return the performative.
	 */
	public Performative getPerformative() {
		return this.performative;
	}
	
	/** Change the performative.
	 * 
	 * @param performative
	 */
	public void setPerformative(Performative performative) {
		this.performative = performative;
	}

	/** Replies the ontology.
	 * 
	 * @return the ontology.
	 */
	public String getOntology() {
		return this.ontology;
	}

	/** Change the ontology.
	 * 
	 * @param ontology
	 */
	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	/** Replies the language.
	 * 
	 * @return the language.
	 */
	public String getLanguage() {
		return this.language;
	}

	/** Change the language.
	 * 
	 * @param language
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	
}
