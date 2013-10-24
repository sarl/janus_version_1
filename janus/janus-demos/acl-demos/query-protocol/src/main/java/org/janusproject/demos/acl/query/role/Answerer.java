/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http:  www.janus-project.org>
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
 * along with this program.  If not, see <http:  www.gnu.org/licenses/>.
 */
package org.janusproject.demos.acl.query.role;

import java.io.IOException;
import java.io.InputStream;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.acl.ACLMessage;
import org.janusproject.acl.Performative;
import org.janusproject.acl.protocol.AbstractFipaProtocol;
import org.janusproject.acl.protocol.EnumFipaProtocol;
import org.janusproject.acl.protocol.FipaConversationManager;
import org.janusproject.acl.protocol.ProtocolState;
import org.janusproject.acl.protocol.query.FipaQueryProtocol;
import org.janusproject.acl.protocol.query.QueryProtocolState;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

/** Role that is answering.
 * 
 * @author $Author: flacreus$
 * @author $Author: sroth$
 * @author $Author: cstentz$
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 */
public class Answerer extends Role {
	private FipaConversationManager conversationManager;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status activate(Object... parameters) {
		for (Object parameter : parameters) {
			if (parameter instanceof FipaConversationManager) {
				this.conversationManager = (FipaConversationManager) parameter;
				return StatusFactory.ok(this);
			}
		}
		
		return StatusFactory.cancel(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status live() {
		this.conversationManager.removeConversations(QueryProtocolState.DONE);
		
		if (this.conversationManager.hasACLMessages(EnumFipaProtocol.FIPA_QUERY, Performative.QUERY_IF, Performative.QUERY_REF)) {
			FipaQueryProtocol queryProtocol = (FipaQueryProtocol) this.conversationManager.createProtocol(EnumFipaProtocol.FIPA_QUERY);
			queryProtocol.initiateAsParticipant();
		}
		
		if (this.conversationManager.getConversations() != null) {
			if (this.conversationManager.getConversations().isEmpty() && getPlayer(Inquirer.class) == null) {
				leaveMe();
			} else {
				for (AbstractFipaProtocol conversation : this.conversationManager.getConversations()) {
					if (conversation instanceof FipaQueryProtocol) {
						HandleQuery((FipaQueryProtocol) conversation);
					}
				}
			}
		}
		
		return StatusFactory.ok(this);
	}
	
	private ProtocolState HandleQuery(FipaQueryProtocol conversation) {
		switch ((QueryProtocolState) conversation.getState()) {
		
		case NOT_STARTED:
			ACLMessage query = conversation.getQuery();
  			if (query != null) {
  				print(Locale.getString(Answerer.class, "QUERY_RECEIVED", query.getSender().getUUID(), query.toXML())); //$NON-NLS-1$
  			}
			break;
			
		case SENDING_ANSWER:
			ACLMessage answer = computeAnswer(conversation.getQuery());
			if (answer.getPerformative() == Performative.AGREE) {
				conversation.agree(answer);
			} else if (answer.getPerformative() == Performative.REFUSE) {
				conversation.refuse(answer);
			}
			
			print(Locale.getString(Answerer.class, "ANSWER_SENT")); //$NON-NLS-1$
			break;
			
		case SENDING_RESULT:
			// TODO failure
			conversation.inform(getQueryResult(conversation.getQuery()));
			break;
			
		case DONE:
		case CANCELED:
		case CANCELING:
		case REFUSED:
		case SENDING_QUERY:
		case WAITING_ANSWER:
		case WAITING_QUERY:
		case WAITING_RESULT:
		default:
			break;
		}
		
		return conversation.getState();
	}
	
	private static ACLMessage computeAnswer(ACLMessage query) {
		String keyProperties;
		Performative performative = Performative.REFUSE;
		
		if (!("SPARQL".equalsIgnoreCase(query.getLanguage()))) { //$NON-NLS-1$
			keyProperties = "ANSWER_WRONG_LANGUAGE"; //$NON-NLS-1$
		} else if (query.getPerformative() == Performative.QUERY_IF && query.getContent().getContent().toString().contains("SELECT")) { //$NON-NLS-1$
			keyProperties = "ANSWER_WRONG_STATEMENT_REF"; //$NON-NLS-1$
		} else if (query.getPerformative() == Performative.QUERY_REF && query.getContent().getContent().toString().contains("ASK")) { //$NON-NLS-1$
			keyProperties = "ANSWER_WRONG_STATEMENT_IF"; //$NON-NLS-1$
		} else {
			performative = Performative.AGREE;
			keyProperties = "ANSWER_OK"; //$NON-NLS-1$
		}
		
		String content = Locale.getString(Answerer.class, keyProperties);
		return new ACLMessage(content, performative);
	}
	
	private static String getQueryResult(ACLMessage query) {
		OntModel m = ModelFactory.createOntologyModel();
		 
		InputStream in = FileManager.get().open("src/main/resources/pizza.owl");  //$NON-NLS-1$
		if (in == null) {
			throw new IllegalArgumentException(Locale.getString(Answerer.class, "ERROR_OWL_FILE")); //$NON-NLS-1$
		}
		try {
			 
			m.read(in, ""); //$NON-NLS-1$
			
			Query sparqlQuery = QueryFactory.create(query.getContent().getContent().toString());
			QueryExecution qexec = QueryExecutionFactory.create(sparqlQuery, m);
			
			StringBuffer sb = new StringBuffer();
			
			try {
	            ResultSet results = qexec.execSelect();
	            while ( results.hasNext() ) {
	                QuerySolution soln = results.nextSolution();
	                sb.append(soln);
	            }
	        } finally {
	            qexec.close();
	            try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
			return sb.toString();
		}
		finally {
			try {
				in.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void print(String str){
		super.print("[" + getPlayer() + "] : " + str); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
