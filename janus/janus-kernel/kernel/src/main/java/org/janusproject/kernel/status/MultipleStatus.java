/* 
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on <http://www.janus-project.org>
 * Copyright (C) 2004-2012 Janus Core Developers
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
package org.janusproject.kernel.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;

/**
 * A concrete multi-status implementation, 
 * suitable either for instantiating or subclassing.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class MultipleStatus implements MultiStatus {
    
	private static final long serialVersionUID = 3199372440098579754L;
	
	/** 
	 * List of child statuses.
	 */
	private List<Status> children = new ArrayList<Status>(2);
	
	/** Indicates is the status want to be silent.
	 */
	private boolean isLoggable = true;

	/**
	 * Creates a new status object.  
	 * The created status has no children.
	 * 
	 * @param iseverity - the severity
	 * @param iproviderId - the unique identifier of the relevant package/class/module providing the status
	 * @param icode - The code identifying the status according to the <code>iproviderId</code>
	 * @param imessage - a human-readable message
	 * @param iexception - exception a low-level exception, or <code>null</code> if not applicable
	 */
	public MultipleStatus(StatusSeverity iseverity, String iproviderId, int icode, String imessage, Throwable iexception) {
		addStatus(iseverity, iproviderId, icode, imessage, iexception);
	}

	/**
	 * Creates a new status object.  
	 * 
	 * @param status
	 */
	public MultipleStatus(Status... status) {
		addStatus(status);
	}
	
	/**
	 * Creates a new status object.  
	 * 
	 * @param status
	 */
	public MultipleStatus(Collection<? extends Status> status) {
		addStatus(status);
	}

	/** Add a status.
	 * 
	 * @param iseverity - the severity
	 * @param iproviderId - the unique identifier of the relevant package/class/module providing the status
	 * @param icode - The code identifying the status according to the <code>iproviderId</code>
	 * @param imessage - a human-readable message
	 * @param iexception - exception a low-level exception, or <code>null</code> if not applicable
	 */
	public final void addStatus(StatusSeverity iseverity, String iproviderId, int icode, String imessage, Throwable iexception) {
		addStatus(new SingleStatus(iseverity, iproviderId, icode, imessage, iexception));
	}
	
	/**
	 * Add a new status objects.
	 * 
	 * @param status
	 */
	public final void addStatus(Status... status) {
		addStatus(Arrays.asList(status));
	}

	/**
	 * Add a new status objects.
	 * 
	 * @param status
	 */
	public void addStatus(Collection<? extends Status> status) {
		for(Status newChild : status) {
			if (newChild!=null) {
				this.children.add(getInsertIndex(this.children, newChild), newChild);
			}
		}
	}
	
	private static int getInsertIndex(List<Status> list, Status newChild) {
		assert(list!=null);
		assert(newChild!=null);
		int first = 0;
		int last = list.size();
		int level = newChild.getSeverity().ordinal();
		int clevel;
		int c;
		
		while (last>first) {
			c = (first+last)/2;
			clevel = list.get(c).getSeverity().ordinal();
			if (level>=clevel) {
				last = c;
			}
			else {
				first = c+1;
			}
		}
		
		return first;
	}

	/** {@inheritDoc}
	 */
	@Override
	public StatusSeverity getSeverity() {
		Status status = getHigherStatus();
		assert(status!=null);
		assert(status.getSeverity()!=null);
		return status.getSeverity();
	}

	/** {@inheritDoc}
	 */
	@Override
	public int getCode() {
		return getHigherStatus().getCode();
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getProviderID() {
		return getHigherStatus().getProviderID();
	}

	/** {@inheritDoc}
	 */
	@Override
	public Throwable getException() {
		return getHigherStatus().getException();
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getMessage() {		
		return getHigherStatus().getMessage();
	} 

	/** {@inheritDoc}
	 */
	@Override
	public boolean isSuccess() {
		if (isEmpty()) return false;
		return getHigherStatus().isSuccess();
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isFailure() {
		if (isEmpty()) return false;
		return getHigherStatus().isFailure();
	}

	/** Indicates if this multiple status is empty or not.
	 * 
	 * @return <code>true</code> if empty, otherwise <code>false</code>
	 */
	public boolean isEmpty() {
		return this.children.isEmpty();
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean matches(StatusSeverity iseverity) {
		int first = 0;
		int last = this.children.size();
		int level = iseverity.ordinal();
		int clevel;
		int c;
		
		while (last>first) {
			c = (first+last)/2;
			clevel = this.children.get(c).getSeverity().ordinal();
			if (level==clevel) return true;
			if (level>clevel) {
				last = c;
			}
			else {
				first = c+1;
			}
		}
		
		return false;
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean matches(int iseverity) {
		for(Status child : this.children) {
			if (child.matches(iseverity))
				return true;
		}
		return false;
	}

	/**
	 * Returns a string representation of the status, suitable 
	 * for logging purposes.
	 */
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		boolean first=true;
		for(Status child : this.children) {
			if (first) first = false;
			else buf.append("\n"); //$NON-NLS-1$
			buf.append(child.toString());
		}
		if (buf.length()==0) {
			buf.append(Locale.getString(
					MultipleStatus.class,
					"EMPTY_MULTIPLE_STATE")); //$NON-NLS-1$
		}
		return buf.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logOn(Logger logger) {
		if (logger!=null) {
			for(Status child : this.children) {
				child.logOn(logger);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLoggable() {
		if (!this.isLoggable || isEmpty()) return false;
		return getHigherStatus().isLoggable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoggable(boolean loggable) {
		this.isLoggable = loggable;
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object object) {
        if (object instanceof MultiStatus) {
            if (object==this) return true;
            return this.children.equals(((MultiStatus)object).getInnerStatus());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return 7+this.children.hashCode();
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public Status getHigherStatus() {
		assert(!this.children.isEmpty());
		return this.children.get(0);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public List<Status> getInnerStatus() {
		return Collections.unmodifiableList(this.children);
	}
	
	/** Clear the list of inner status.
	 */
	public void clear() {
		this.children.clear();
	}

    /**
     * {@inheritDoc}
     */
    @Override
	public Status pack(String provider) {
    	if (!this.children.isEmpty()) {
    		
        	Status packedChild;
	    	List<Status> packedChildren = new ArrayList<Status>(this.children.size());
	    	Status successStatus = null;
	    	
	    	for(Status child : this.children) {
	    		assert(child!=null);
	    		packedChild = child.pack(provider);
	    		assert(packedChild!=null);
	    		if ((packedChild.getSeverity()==StatusSeverity.OK)
	    			||
	    			(packedChild.getSeverity()==StatusSeverity.INFO)) {
	    			if (successStatus==null) successStatus = packedChild;
	    		}
	    		else {
	    			packedChildren.add(getInsertIndex(packedChildren, packedChild), packedChild);
	    		}
	    	}
	    	
	    	if (successStatus!=null && packedChildren.isEmpty()) {
	    		packedChildren.add(successStatus);
	    	}
	    	
	    	this.children.clear();
	    	this.children = packedChildren;
	    	
    	}
    	
    	Status st;
    	
    	switch(this.children.size()) {
    	case 0:
    		st = StatusFactory.ok(provider);
    		st.setLoggable(this.isLoggable);
    		break;
    	case 1:
    		st = getHigherStatus();
    		st.setLoggable(this.isLoggable);
    		break;
    	default:
    		st = this;
    	}
		return st;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Status pack(Class<?> provider) {
    	assert(provider!=null);
    	return pack(provider.getCanonicalName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Status pack(Object provider) {
    	assert(provider!=null);
    	return pack(Integer.toString(System.identityHashCode(provider)));
    }

}
