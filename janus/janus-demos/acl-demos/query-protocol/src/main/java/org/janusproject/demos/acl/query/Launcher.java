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
package org.janusproject.demos.acl.query;

import java.util.logging.Level;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.ACLRepresentation;
import org.janusproject.acl.Performative;
import org.janusproject.acl.encoding.PayloadEncoding;
import org.janusproject.demos.acl.query.agent.ACLAgentAnswerer;
import org.janusproject.demos.acl.query.agent.ACLAgentInquirer;
import org.janusproject.demos.acl.query.util.QueryInfo;
import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;


/**
 * DEMO : Query Protocol
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Launcher {
	
	/**
	 * Launcher
	 * @param args
	 */
	public static void main(String[] args) {
		LoggerUtil.setGlobalLevel(Level.ALL);
		LoggerUtil.setShortLogMessageEnable(true);
		
		Kernel k = Kernels.get(false);
		
		ACLAgentAnswerer receiver = new ACLAgentAnswerer(ACLRepresentation.XML, PayloadEncoding.UTF8);
		
		ACLAgentInquirer smartSenderPizza = new ACLAgentInquirer(ACLRepresentation.XML, PayloadEncoding.UTF8);
		ACLAgentInquirer smartSenderCountry = new ACLAgentInquirer(ACLRepresentation.XML, PayloadEncoding.UTF8);
		
		ACLAgentInquirer idiotSenderQueryIfWithSelect = new ACLAgentInquirer(ACLRepresentation.XML, PayloadEncoding.UTF8);
		ACLAgentInquirer idiotSenderQueryRefWithAsk = new ACLAgentInquirer(ACLRepresentation.XML, PayloadEncoding.UTF8);
		ACLAgentInquirer idiotSenderWrongLanguage = new ACLAgentInquirer(ACLRepresentation.XML, PayloadEncoding.UTF8);
		
//		String sparqlQueryCountry =	Locale.getString(Launcher.class, "SPARQL_QUERY_COUNTRY");
		String sparqlQueryCountry = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX : " + //$NON-NLS-1$
									"<http://www.co-ode.org/ontologies/pizza/pizza.owl#> " + //$NON-NLS-1$
									"SELECT ?i WHERE {?i rdf:type :Country}"; //$NON-NLS-1$
		
//		String sparqlQueryPizza = Locale.getString(Launcher.class, "SPARQL_QUERY_PIZZA");
		String sparqlQueryPizza = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX : " + //$NON-NLS-1$
									"<http://www.co-ode.org/ontologies/pizza/pizza.owl#> " + //$NON-NLS-1$
									"SELECT ?s WHERE { ?s rdfs:subClassOf :NamedPizza}"; //$NON-NLS-1$
		
		String sparqlQueryAskWithRef = Locale.getString(Launcher.class, "SPARQL_QUERY_ASK_WITH_REF"); //$NON-NLS-1$
		
		String ontology = Locale.getString(Launcher.class, "OWL_FILE_NAME"); //$NON-NLS-1$
		String sparqlLanguage = Locale.getString(Launcher.class, "LANGUAGE_SPARQL"); //$NON-NLS-1$
		String prologLanguage = Locale.getString(Launcher.class, "LANGUAGE_PROLOG"); //$NON-NLS-1$
		
		QueryInfo goodQueryPizza = new QueryInfo(sparqlQueryPizza, Performative.QUERY_REF, ontology, sparqlLanguage);
		QueryInfo goodQueryCountry = new QueryInfo(sparqlQueryCountry, Performative.QUERY_REF, ontology, sparqlLanguage);
		
		QueryInfo queryIfWithSelect = new QueryInfo(sparqlQueryPizza, Performative.QUERY_IF, ontology, sparqlLanguage);
		QueryInfo queryRefWithAsk = new QueryInfo(sparqlQueryAskWithRef, Performative.QUERY_REF, ontology, sparqlLanguage);
		QueryInfo queryWrongLanguage = new QueryInfo(sparqlQueryPizza, Performative.QUERY_REF, ontology, prologLanguage);
		
		String SPARQLAgentName = Locale.getString(Launcher.class, "SPARQLAgent"); //$NON-NLS-1$
		String SmartSenderPizzaName = Locale.getString(Launcher.class, "SmartSenderPizza"); //$NON-NLS-1$
		String SmartSenderCountryName = Locale.getString(Launcher.class, "SmartSenderCountry"); //$NON-NLS-1$
		String idiotSenderQueryIfWithSelectName = Locale.getString(Launcher.class, "idiotSenderQueryIfWithSelect"); //$NON-NLS-1$
		String idiotSenderQueryRefWithAskName = Locale.getString(Launcher.class, "idiotSenderQueryRefWithAsk"); //$NON-NLS-1$
		String idiotSenderWrongLanguageName = Locale.getString(Launcher.class, "idiotSenderWrongLanguage"); //$NON-NLS-1$
		
		k.submitLightAgent(receiver, SPARQLAgentName);
		
		k.submitLightAgent(smartSenderPizza, SmartSenderPizzaName, goodQueryPizza);
		k.submitLightAgent(smartSenderCountry, SmartSenderCountryName, goodQueryCountry);
		
		k.submitLightAgent(idiotSenderQueryIfWithSelect, idiotSenderQueryIfWithSelectName, queryIfWithSelect);
		k.submitLightAgent(idiotSenderQueryRefWithAsk, idiotSenderQueryRefWithAskName, queryRefWithAsk);
		k.submitLightAgent(idiotSenderWrongLanguage, idiotSenderWrongLanguageName, queryWrongLanguage);
        		
		k.launchDifferedExecutionAgents();
		
		Kernels.killAll();
	}	
}
