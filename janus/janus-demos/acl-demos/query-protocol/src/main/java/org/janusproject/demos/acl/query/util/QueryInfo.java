package org.janusproject.demos.acl.query.util;

import org.janusproject.acl.Performative;

/**
 * @author $Author: flacreus$
 * @author $Author: sroth-01$
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
	
	public QueryInfo(String query, Performative performative, String ontology, String language) {
		this.query = query;
		this.performative = performative;
		this.ontology = ontology;
		this.language = language;
	}

	public String getQuery() {
		return this.query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Performative getPerformative() {
		return this.performative;
	}

	public void setPerformative(Performative performative) {
		this.performative = performative;
	}

	public String getOntology() {
		return this.ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
