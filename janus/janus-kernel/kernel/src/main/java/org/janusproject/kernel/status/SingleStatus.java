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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;

/**
 * A concrete status implementation, suitable either for 
 * instantiating or subclassing.
 * 
 * @author $Author: ngaud$
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SingleStatus implements Status {
    
	private static final long serialVersionUID = -5184174283439785754L;

	/**
	 * The severity of this status
	 */
	private StatusSeverity severity;

	/** Indicates is the status want to be silent.
	 */
	private boolean isLoggable = true;

	/**
	 * The id of the package/class/module providing the status
	 */
	private String providerId;

	/**
	 * The code identifying the status according to the <code>providerId</code>
	 */
	private int code;

	/** 
	 * Message, localized to the current locale.
	 */
	private String message;

	/** 
	 * Wrapped exception, or <code>null</code> if none.
	 */
	private Throwable exception;

	/** Create empty new status object.
	 */
	protected SingleStatus() {
		//
	}
	
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
	public SingleStatus(StatusSeverity iseverity, String iproviderId, int icode, String imessage, Throwable iexception) {
		setProviderId(iproviderId);
		setSeverity(iseverity);
		setCode(icode);
		setMessage(imessage);
		setException(iexception);
	}

	/**
	 * Creates a new status object.  
	 * The created status has no children.
	 * 
	 * @param iseverity - the severity
	 * @param iproviderId - the unique identifier of the relevant package/class/module providing the status
	 * @param icode - The code identifying the status according to the <code>iproviderId</code>
	 */
	public SingleStatus(StatusSeverity iseverity, String iproviderId, int icode) {
		setProviderId(iproviderId);
		setSeverity(iseverity);
		setCode(icode);
	}

	/**
	 * Creates a new status object.  
	 * The created status has no children.
	 * 
	 * @param iseverity - the severity
	 * @param iproviderId - the unique identifier of the relevant package/class/module providing the status
	 * @param icode - The code identifying the status according to the <code>iproviderId</code>
	 * @param imessage - a human-readable message
	 */
	public SingleStatus(StatusSeverity iseverity, String iproviderId, int icode, String imessage) {
		setProviderId(iproviderId);
		setSeverity(iseverity);
		setCode(icode);
		setMessage(imessage);
	}

	/**
	 * Creates a new status object.  
	 * The created status has no children.
	 * 
	 * @param iseverity - the severity
	 * @param iproviderId - the unique identifier of the relevant package/class/module providing the status
	 * @param icode - The code identifying the status according to the <code>iproviderId</code>
	 * @param iexception - exception a low-level exception, or <code>null</code> if not applicable
	 */
	public SingleStatus(StatusSeverity iseverity, String iproviderId, int icode, Throwable iexception) {
		setProviderId(iproviderId);
		setSeverity(iseverity);
		setCode(icode);
		setMessage(iexception.getLocalizedMessage());
		setException(iexception);
	}

	/**
	 * Set the severity of this status
	 * @param iseverity - the severity 
	 */
	public void setSeverity(StatusSeverity iseverity) {
		assert(iseverity != null);
		this.severity = iseverity;
	}

	/** {@inheritDoc}
	 */
	@Override
	public StatusSeverity getSeverity() {
		assert(this.severity!=null);
		return this.severity;
	}

	/**
	 * Set the status code.
	 * 
	 * @param icode is the code identifying the status according to the <code>providerId</code>
	 */
	public void setCode(int icode) {
		this.code = icode;
	}

	/** {@inheritDoc}
	 */
	@Override
	public int getCode() {
		return this.code;
	}

	/**
	 * Set the id of the package/class/module providing the status 
	 * @param iproviderId - the id
	 */
	public void setProviderId(String iproviderId) {
		assert(iproviderId != null && iproviderId.length() > 0);
		this.providerId = iproviderId;
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getProviderID() {
		return this.providerId;
	}

	/**
	 * Sets the associated exception.
	 * @param iexception - a low-level exception, or <code>null</code> if not applicable
	 */
	public void setException(Throwable iexception) {
		this.exception = iexception;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Throwable getException() {
		return this.exception;
	}

	/**
	 * Sets the associated message.
	 * @param imessage - a human-readable message
	 */
	public void setMessage(String imessage) {
		this.message = imessage;
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getMessage() {		
		return this.message;
	} 

	/** {@inheritDoc}
	 */
	@Override
	public boolean isSuccess() {
		return this.severity.isSuccess();
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isFailure() {
		return this.severity.isFailure();
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean matches(StatusSeverity iseverity) {
		assert(iseverity!=null);
		return matches(iseverity.getMask());
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean matches(int iseverity) {
		int cMask = this.severity.getMask();
		return (cMask & iseverity) != 0;
	}

	/**
	 * Returns a string representation of the status, suitable 
	 * for logging purposes.
	 */
	@Override
	public String toString() {
		String ex = null;
		if (this.exception!=null) {
			StringBuilder buffer = new StringBuilder();
			buffer.append(this.exception.getClass().getCanonicalName());
			if (this.exception.getLocalizedMessage()!=null) {
				buffer.append("\n"); //$NON-NLS-1$
				buffer.append(this.exception.getLocalizedMessage());
			}			
			for(StackTraceElement elt : this.exception.getStackTrace()) {
				buffer.append("\n\t"); //$NON-NLS-1$
				buffer.append(elt.toString());
			}
			ex = buffer.toString();
		}
		return Locale.getString(SingleStatus.class, "TO_STRING", //$NON-NLS-1$
				this.severity.name(),
				this.providerId,
				Integer.toString(this.code),
				this.message,
				ex);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logOn(Logger logger) {
		if (logger!=null && this.severity!=StatusSeverity.OK) {
			Level level = this.severity.toLevel();
			if (level!=Level.OFF) {
				if (this.exception!=null) {
					logger.log(level, toString(), this.exception);
				}
				else {
					logger.log(level, toString());
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLoggable() {
		return this.isLoggable && this.severity.toLevel()!=Level.OFF;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoggable(boolean loggable) {
		this.isLoggable = loggable;
	}

	@Override
    public boolean equals(Object object) {
        if (object instanceof Status) {
            Status ostatus = (Status)object;
            return ostatus.getSeverity()==getSeverity()
            	&& ostatus.getCode()==getCode()
            	&& ostatus.getProviderID()==getProviderID();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.severity.hashCode();
        hash = 31 * hash + this.code;
        hash = 31 * hash + ((this.providerId!=null) ? this.providerId.hashCode() : 0);
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Status pack(String provider) {
    	return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Status pack(Class<?> provider) {
    	return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public Status pack(Object provider) {
    	return this;
    }
    
}
