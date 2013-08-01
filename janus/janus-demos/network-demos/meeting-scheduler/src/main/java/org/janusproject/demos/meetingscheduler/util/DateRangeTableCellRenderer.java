package org.janusproject.demos.meetingscheduler.util;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

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
public class DateRangeTableCellRenderer extends DefaultTableCellRenderer{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8197602968331988703L;

	public Component getTableCellRendererComponent(
			JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
    {
         JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,column);
         
         ImmutableDateRange date = (ImmutableDateRange) value;
       label.setText("From "+date.getStart().getTime()+" to "+date.getEnd().getTime());

       return label;

    }
}
