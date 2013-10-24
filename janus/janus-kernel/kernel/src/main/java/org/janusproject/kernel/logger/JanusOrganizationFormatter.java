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
package org.janusproject.kernel.logger;

import java.util.logging.LogRecord;

import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.time.KernelTimeManager;

/**
 * This class provides a tuned formatter for log records.
 * <p>
 * The format of each line is:<br>
 * <code>&lt;DATE&gt; &lt;LEVEL&gt;: &lt;MESSAGE&gt;</code><br>
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JanusOrganizationFormatter
extends AbstractJanusFormatter {

	private final String organization;
	
	/**
	 * @param timeManager is the kernel time manager.
	 * @param organization is the name of the organization.
	 */
	public JanusOrganizationFormatter(KernelTimeManager timeManager, String organization) {
		super(timeManager);
		assert(organization!=null);
		this.organization = organization;
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getFormatterOwner() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalizedMessage(LogRecord record, String message) {
		String action = record.getLevel().getLocalizedName();
		return Locale.getString(JanusOrganizationFormatter.class,
				LoggerUtil.isShortLogMessageEnable()
				? "SHORT_MESSAGE" //$NON-NLS-1$
				: "MESSAGE", //$NON-NLS-1$
				formatDate(),
				this.organization,
				action,
				message);
	}
	
}
