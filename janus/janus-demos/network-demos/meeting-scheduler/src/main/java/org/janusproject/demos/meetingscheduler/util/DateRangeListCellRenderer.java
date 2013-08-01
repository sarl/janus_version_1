package org.janusproject.demos.meetingscheduler.util;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import com.miginfocom.util.dates.ImmutableDateRange;


/**
 * 
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 *
 */
public class DateRangeListCellRenderer extends DefaultListCellRenderer
{
	private static final long serialVersionUID = 8197602968331988703L;

	@SuppressWarnings("rawtypes")
	public Component getListCellRendererComponent(
        JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
         JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
         
         ImmutableDateRange date = (ImmutableDateRange) value;
       label.setText("From "+date.getStart().getTime()+" to "+date.getEnd().getTime());

       return label;

    }
}
