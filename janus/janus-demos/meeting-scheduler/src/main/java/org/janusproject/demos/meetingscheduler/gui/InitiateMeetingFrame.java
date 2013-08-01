package org.janusproject.demos.meetingscheduler.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.janusproject.demos.meetingscheduler.ontology.Meeting;
import org.janusproject.demos.meetingscheduler.util.DateRangeListCellRenderer;
import org.janusproject.demos.meetingscheduler.util.KernelWatcher;

import com.miginfocom.calendar.DatePicker;
import com.miginfocom.calendar.ThemeDatePicker;
import com.miginfocom.calendar.activity.ActivityDepository;
import com.miginfocom.calendar.activity.ActivityList;
import com.miginfocom.theme.Themes;
import com.miginfocom.util.dates.DateChangeEvent;
import com.miginfocom.util.dates.DateChangeListener;
import com.miginfocom.util.dates.DateRange;
import com.miginfocom.util.dates.ImmutableDateRange;

/**
 * The UI shown for creating a new meeting.
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 *
 */
public class InitiateMeetingFrame extends JFrame implements ActionListener,
		DateChangeListener {

	private KernelWatcher kw;

	private JList/*<String>*/ participantList;

	private JList/*<ImmutableDateRange>*/ suggestedHoursList;
	private JList/*<ImmutableDateRange>*/ selectedHoursList;
	private DefaultListModel/*<ImmutableDateRange>*/ suggestedHoursListModel = new DefaultListModel/*<ImmutableDateRange>*/();
	private DefaultListModel/*<ImmutableDateRange>*/ selectedHoursListModel = new DefaultListModel/*<ImmutableDateRange>*/();

	private String initiator_name;
	private JTextField description_field;

	private static final String DP_THEME_CTX1 = "datePicker1";
	private DatePicker datePicker;

	private ActivityDepository depository;

	private ImmutableDateRange range;

	private JSpinner hoursSpinner;

	private static final long serialVersionUID = 234360639496126275L;

	public InitiateMeetingFrame(String name, KernelWatcher kw) {

		// Init themes

		try {
			Themes.loadTheme("src/main/resources/themes/DatePicker1.tme",
					DP_THEME_CTX1, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.initiator_name = name;
		this.kw = kw;
		this.depository = ActivityDepository.getInstance(name);

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,
				getDefaultCloseOperation()));
		this.setSize(800, 300);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JPanel listbox = new JPanel();
		listbox.setLayout(new BoxLayout(listbox, BoxLayout.LINE_AXIS));

		suggestedHoursList = new JList/*<ImmutableDateRange>*/(
				suggestedHoursListModel);
		selectedHoursList = new JList/*<ImmutableDateRange>*/(
				selectedHoursListModel);
		
		suggestedHoursList.setCellRenderer(new DateRangeListCellRenderer());

		JPanel list_left = new JPanel();
		list_left.setLayout(new BoxLayout(list_left, BoxLayout.Y_AXIS));
		list_left.add(new JLabel("Suggested timeslots"));
		list_left.add(new JScrollPane(suggestedHoursList));

		JPanel list_right = new JPanel();
		list_right.setLayout(new BoxLayout(list_right, BoxLayout.Y_AXIS));
		list_right.add(new JLabel("Selected timeslots"));
		list_right.add(new JScrollPane(selectedHoursList));

		listbox.add(list_left);

		JButton toRight = new JButton("<<");
		listbox.add(toRight);
		toRight.setActionCommand("ADDLEFT");
		toRight.addActionListener(this);

		JButton toLeft = new JButton(">>");
		listbox.add(toLeft);
		toLeft.setActionCommand("ADDRIGHT");
		toLeft.addActionListener(this);

		listbox.add(list_right);

		description_field = new JTextField("Description");

		participantList = new JList/*<String>*/(this.kw.getAllAgentExcept(name)
				.toArray(new String[0]));
		JScrollPane scrollPaneParticipants = new JScrollPane(participantList);

		JPanel bottombox = new JPanel();
		bottombox.setLayout(new BoxLayout(bottombox, BoxLayout.Y_AXIS));

		JButton sendProposalButton = new JButton("Send meeting proposal");
		sendProposalButton.setActionCommand("SENDMEETING");
		sendProposalButton.addActionListener(this);

		bottombox.add(description_field);
		bottombox.add(sendProposalButton);

		panel.add(createDatePickerPanel(), BorderLayout.NORTH);
		panel.add(scrollPaneParticipants, BorderLayout.EAST);
		panel.add(listbox, BorderLayout.CENTER);
		panel.add(bottombox, BorderLayout.SOUTH);
		this.add(panel);
	}

	private JComponent createDatePickerPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 5));
		panel.setBorder(new CompoundBorder(new TitledBorder(
				"Select a date for the meeting"),
				new EmptyBorder(10, 10, 10, 10)));

		datePicker = new ThemeDatePicker(DP_THEME_CTX1);
		datePicker.setEditable(true);
		datePicker.getDateAreaContainer().getDateArea()
				.addDateChangeListener(this, false);

		panel.add(new JLabel("Day Select:"));
		panel.add(datePicker);
		panel.add(new JLabel("Number of hours of the meeting"));
		this.hoursSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
		this.hoursSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshSlotList();
			}
		});
		panel.add(hoursSpinner);
		panel.setOpaque(false);

		return panel;
	}

	public void refreshSlotList() {
		if (this.range == null) {
			return;
		}

		ActivityList existingActivites = depository.getActivities();

		Integer rangeHours = (Integer) this.hoursSpinner.getValue();

		suggestedHoursListModel.removeAllElements();
		@SuppressWarnings("unchecked")
		Iterator<ImmutableDateRange> x = this.range.iterator(
				DateRange.RANGE_TYPE_HOUR, rangeHours);
		while (x.hasNext()) {
			ImmutableDateRange date = x.next();
			if (!existingActivites
					.hasOverlapping(date.getDateRangeForReading())) {
				suggestedHoursListModel.addElement(date);
				
			}
		}
	}

	private static <T> List<T> castArray(Class<T> type, Object[] t) {
		return (List<T>)Arrays.asList(t);
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		if (cmd == "SENDMEETING") {
			List<String> participants = castArray(String.class, participantList.getSelectedValues()); 
			if (participants.size() == 0) {
				JOptionPane.showMessageDialog(this,
						"You must choose at least one participant", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			List/*<ImmutableDateRange>*/ hours = Collections
					.list(selectedHoursListModel.elements());
			if (hours.size() == 0) {
				JOptionPane.showMessageDialog(this,
						"You must choose at least one time slot", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			String description = description_field.getText();
			if (description.equals("")) {
				JOptionPane.showMessageDialog(this,
						"You must choose a description for the meeting", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			Meeting meeting = new Meeting(this.initiator_name, hours,
					description);
			this.kw.getChannel(this.initiator_name).createMeeting(meeting,
					this.kw.getAgentByNames(participants));
			this.dispose();
		} else if (cmd == "ADDRIGHT") { // >>
			for (ImmutableDateRange range : castArray(ImmutableDateRange.class, suggestedHoursList
					.getSelectedValues())) {
				selectedHoursListModel.addElement(range);
				suggestedHoursListModel.removeElement(range);
			}
		} else if (cmd == "ADDLEFT") { // <<
			for (ImmutableDateRange range : castArray(ImmutableDateRange.class, selectedHoursList
					.getSelectedValues())) {
				suggestedHoursListModel.addElement(range);
				selectedHoursListModel.removeElement(range);
			}
		}
	}

	@Override
	public void dateRangeChanged(DateChangeEvent e) {
		ImmutableDateRange range = null;

		if (e.getType() == DateChangeEvent.PRESSED) {
			range = e.getNewRange();
		} else if (e.getType() == DateChangeEvent.SELECTED) {
			range = e.getNewRange();
		}

		if (range == null) {
			return;
		}

		this.range = range;

		this.refreshSlotList();
	}

}

