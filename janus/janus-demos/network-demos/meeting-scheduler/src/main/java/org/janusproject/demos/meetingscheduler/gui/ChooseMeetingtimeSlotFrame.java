package org.janusproject.demos.meetingscheduler.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.janusproject.demos.meetingscheduler.ontology.MeetingTimeSlot;
import org.janusproject.demos.meetingscheduler.util.DateRangeTableCellRenderer;
import org.janusproject.demos.meetingscheduler.util.KernelWatcher;

import com.miginfocom.util.dates.ImmutableDateRange;

/**
 * The Frame shown when all meeting responses have been received
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 *
 */
public class ChooseMeetingtimeSlotFrame extends JFrame implements
		ActionListener {

	private static final long serialVersionUID = -4757588276034608043L;
	private Map<ImmutableDateRange, MeetingTimeSlot> slots;
	private Vector<Vector<Object>> data;
	private JTable slotsTable;
	private KernelWatcher kw;
	private String who;
	private UUID id;

	public ChooseMeetingtimeSlotFrame(String who, UUID id,
			Map<ImmutableDateRange, MeetingTimeSlot> slots, KernelWatcher kw) {
		this.slots = slots;
		this.id = id;
		this.kw = kw;
		this.who = who;
		setTitle(who + " choose meeting slot");

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,
				getDefaultCloseOperation()));
		this.setSize(500, 300);
		this.setLocation(300, 400);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Time Slot");
		columnNames.add("Rank");
		columnNames.add("Participants");

		data = new Vector<Vector<Object>>();
	
		for (Entry<ImmutableDateRange, MeetingTimeSlot> entry : this.slots
				.entrySet()) {
			Vector<Object> row = new Vector<Object>();
			row.add(entry.getKey());
			MeetingTimeSlot value = entry.getValue();
			row.add(value.getValue());
			if (value.hasAllParticipants()) {
				row.add("All participants");
			} else {
				row.add(this.kw.getAgentsNames(value.getParticipants()));
			}
			data.add(row);
		}

		DefaultTableModel model = new DefaultTableModel(data, columnNames);

		slotsTable = new JTable(model);
		slotsTable.setAutoCreateRowSorter(true);
		TableColumn col = slotsTable.getColumnModel().getColumn(0);
		col.setCellRenderer(new DateRangeTableCellRenderer());

		JButton submitButton = new JButton("Submit");
		submitButton.setActionCommand("SUBMIT");
		submitButton.addActionListener(this);

		JScrollPane scrollPane = new JScrollPane(slotsTable);
		panel.add(submitButton, BorderLayout.SOUTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		this.add(panel);
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd == "SUBMIT") {
			int selectedRow = slotsTable.getSelectedRow();
			if (selectedRow == -1) {
				return;
			}
			this.kw.getChannel(this.who).confirmMeeting(
					this.id,
					(ImmutableDateRange) data.get(selectedRow).get(0));
			this.dispose();
		}

	}

}
