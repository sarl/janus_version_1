package org.janusproject.demos.meetingscheduler.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;

import org.janusproject.demos.meetingscheduler.ontology.Meeting;
import org.janusproject.demos.meetingscheduler.ontology.MeetingTimeSlot;
import org.janusproject.demos.meetingscheduler.role.MeetingListener;
import org.janusproject.demos.meetingscheduler.util.KernelWatcher;

import com.miginfocom.beans.ActivityGridLayoutBean;
import com.miginfocom.beans.DateAreaBean;
import com.miginfocom.calendar.activity.ActivityDepository;
import com.miginfocom.calendar.activity.ActivityInteractor;
import com.miginfocom.calendar.activity.DefaultActivity;
import com.miginfocom.calendar.activity.view.ActivityView;
import com.miginfocom.calendar.datearea.ActivityDragResizeEvent;
import com.miginfocom.calendar.datearea.ActivityDragResizeListener;
import com.miginfocom.calendar.datearea.ActivityMoveEvent;
import com.miginfocom.calendar.datearea.ActivityMoveListener;
import com.miginfocom.calendar.datearea.DefaultDateArea;
import com.miginfocom.calendar.decorators.GridCellRangeDecorator;
import com.miginfocom.calendar.grid.Grid;
import com.miginfocom.util.MigUtil;
import com.miginfocom.util.dates.DateChangeEvent;
import com.miginfocom.util.dates.DateRange;
import com.miginfocom.util.dates.DateRangeI;
import com.miginfocom.util.dates.ImmutableDateRange;
import com.miginfocom.util.dates.MutableDateRange;
import com.miginfocom.util.dates.TimeSpanListEvent;
import com.miginfocom.util.gfx.geometry.AbsRect;
import com.miginfocom.util.gfx.geometry.numbers.AtEnd;
import com.miginfocom.util.gfx.geometry.numbers.AtStart;
import com.miginfocom.util.states.ToolTipProvider;

/**
 * The main UI, show the meetings like OS X iCal.
 * 
 * @author bfeld
 * @author ngrenie
 * @version $FullVersion$
 * @mavengroupid $Groupid$
 * @mavenartifactid $ArtifactId$
 * 
 */
public class AgentCalendarUI extends JFrame implements MeetingListener,
		ActivityMoveListener, ActivityDragResizeListener {

	private static final long serialVersionUID = 1366912984828214678L;

	private transient DateAreaBean currentDateArea;

	private transient DefaultActivity newCreatedAct = null;

	private ActivityGridLayoutBean activityGridLayoutBean = new ActivityGridLayoutBean();

	private String name;

	private KernelWatcher kw;

	public AgentCalendarUI(String name, KernelWatcher kw) {
		setTitle("Calendar of " + name);

		// Base
		this.name = name;
		this.kw = kw;

		// GUI
		initComponents();
		configureComponents();

		((JComponent) getContentPane()).setOpaque(true);
		((JComponent) getContentPane()).setBackground(Color.WHITE);

		setSize(1000, 800);
		setLocationRelativeTo(null);
		overviewDateArea.getDateArea().setSelectedRange(
				new DateRange(System.currentTimeMillis(),
						DateRangeI.RANGE_TYPE_WEEK, 1, null, null));

		weekButton.doClick();

		// Run on the EDT so that the sizes are figured out already
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				overviewDateArea.scrollToShowRange(new DateRange(), 0.0f, 0.3f);

				MutableDateRange dr = dayDateArea.getDateArea()
						.getVisibleDateRange().getDateRangeClone();
				dr.setStartField(Calendar.HOUR_OF_DAY, 12);
				dr.setStartField(Calendar.MINUTE, 0);
				dayDateArea.scrollToShowRange(dr, 0.0f, 0.6f);
			}
		});

		dayDateArea.setActivityDepositoryContext(this.name);
		monthDateArea.setActivityDepositoryContext(this.name);
		currentDateArea.setActivityDepositoryContext(this.name);

		// Listener
		this.kw.getChannel(this.name).addMeetingListener(this);
	}

	private void configureComponents() {
		currentDateArea = dayDateArea;

		// activityGridLayoutBean is new to v6.5 and is a layout that sizes the
		// hight of the top whole day panel to the content.
		activityGridLayoutBean.setActivitySizeFirst(16);
		activityGridLayoutBean.setActivitySize(17);
		activityGridLayoutBean.setRowPadding(2);
		activityGridLayoutBean.setMinimumRowSize(16);
		activityGridLayoutBean.setRoundActivityTo(DateRangeI.RANGE_TYPE_DAY);
		topDayArea.setSecondaryDimensionLayout(activityGridLayoutBean);

		// Add a tooltip provider that is much more configurable than for a
		// normal Swing component
		ToolTipProvider myTTP = new ToolTipProvider() {
			public int configureToolTip(JToolTip toolTip, MouseEvent e,
					Object source) {
				if (e.getID() == MouseEvent.MOUSE_MOVED
						&& source instanceof ActivityView) {
					toolTip.setForeground(Color.DARK_GRAY);
					String summary = ((ActivityView) source).getModel()
							.getSummary();
					if (summary != null)
						toolTip.setTipText("<html>"
								+ (summary.length() > 100 ? "<table width=300>"
										: "") + summary);

					return ToolTipProvider.SHOW_TOOLTIP;
				} else {
					return ToolTipProvider.HIDE_TOOLTIP;
				}
			}

			public int getPositionAdjustY() {
				return 23;
			}
		};

		// Paint the first 16 rows with gray
		Color rowColor = new Color(242, 242, 242);
		DefaultDateArea dayDA = dayDateArea.getDateArea();
		AbsRect cells = new AbsRect(AtStart.START0, AtStart.START0, AtEnd.END0,
				new AtStart(16));
		dayDA.addDecorator(new GridCellRangeDecorator(dayDA, 20, cells,
				rowColor, Grid.SIZE_MODE_INSIDE, false));

		// Paint the last 12 rows with gray
		cells = new AbsRect(AtStart.START0, new AtEnd(-12), AtEnd.END0,
				AtEnd.END0);
		dayDA.addDecorator(new GridCellRangeDecorator(dayDA, 20, cells,
				rowColor, Grid.SIZE_MODE_INSIDE, false));

		monthDateArea.getDateArea().setToolTipProvider(myTTP);
		dayDateArea.getDateArea().setToolTipProvider(myTTP);
		topDayArea.getDateArea().setToolTipProvider(myTTP);

		ButtonGroup bg = new ButtonGroup();
		bg.add(weekButton);
		bg.add(dayButton);
		bg.add(monthButton);

		// So that we don't have PM and AM for USA
		ActivityInteractor.setDefaultDateTimeFormat(new SimpleDateFormat(
				"HH.mm"));

		// Specially looking buttons on Mac OS X
		if (MigUtil.isAqua("1.5")) { // If Leopard...
			((FlowLayout) northPanel.getLayout()).setHgap(0);
			weekButton.setForeground(Color.WHITE);
			todayButton.putClientProperty("JButton.buttonType",
					"segmentedTextured");
			todayButton.putClientProperty("JButton.segmentPosition", "only");
			separatedButton.putClientProperty("JButton.buttonType",
					"segmentedTextured");
			separatedButton
					.putClientProperty("JButton.segmentPosition", "only");
			dayButton.putClientProperty("JButton.buttonType",
					"segmentedTextured");
			dayButton.putClientProperty("JButton.segmentPosition", "first");
			weekButton.putClientProperty("JButton.buttonType",
					"segmentedTextured");
			weekButton.putClientProperty("JButton.segmentPosition", "middle");
			monthButton.putClientProperty("JButton.buttonType",
					"segmentedTextured");
			monthButton.putClientProperty("JButton.segmentPosition", "last");
		} else {
			todayButton.setOpaque(false);
			separatedButton.setOpaque(false);
			dayButton.setOpaque(false);
			weekButton.setOpaque(false);
			monthButton.setOpaque(false);
		}

		// DatePickerBean picker = new DatePickerBean();
		// DateFormatList dateFormatList = new DateFormatList("yyyy-MM-dd");
		// picker.setRangeFormat(DateRangeI.RANGE_TYPE_DAY, dateFormatList);
		// JFrame frame = new JFrame();
		// frame.add(picker);
		// frame.setSize(new Dimension(500, 300));
		// DateAreaBean bean = new DateAreaBean();
		// bean.setSelectionType(DateArea.SELECTION_TYPE_NORMAL);
		// bean.setActivitiesSupported(false);
		// picker.setDateAreaContainer(bean);
		// frame.setVisible(true);
	}

	private void setMode(int rangeType) {
		currentDateArea = monthButton.isSelected() ? monthDateArea
				: dayDateArea;
		CardLayout cl = (CardLayout) mainParentPanel.getLayout();
		cl.show(mainParentPanel, monthButton.isSelected() ? "month" : "day");

		overviewDateArea.setSelectionBoundaryType(rangeType);

		MutableDateRange curRange = overviewDateArea.getDateArea()
				.getSelectedRange().getDateRangeClone();
		curRange.setSize(rangeType, 1, MutableDateRange.ALIGN_CENTER_DOWN);

		overviewDateArea.getDateArea().setSelectedRange(curRange);

		if (MigUtil.isAqua("1.5")) {
			weekButton.setForeground(weekButton.isSelected() ? Color.WHITE
					: Color.BLACK);
			dayButton.setForeground(dayButton.isSelected() ? Color.WHITE
					: Color.BLACK);
			monthButton.setForeground(monthButton.isSelected() ? Color.WHITE
					: Color.BLACK);
		}
	}

	// ***********************************
	// Code below is created by NetBeans.
	// ***********************************

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		overviewWestHeader = new com.miginfocom.beans.DateHeaderBean();
		overviewNorthHeader = new com.miginfocom.beans.DateHeaderBean();
		overviewVerticalLayout = new com.miginfocom.beans.GridDimensionLayoutBean();
		activityDayAShape = new com.miginfocom.beans.ActivityAShapeBean();
		activityMonthAShape = new com.miginfocom.beans.ActivityAShapeBean();
		weekLayout = new com.miginfocom.beans.GridDimensionLayoutBean();
		weekDateHeader = new com.miginfocom.beans.DateHeaderBean();
		dayLayout = new com.miginfocom.beans.GridDimensionLayoutBean();
		dayDateHeader = new com.miginfocom.beans.DateHeaderBean();
		yearConnector = new com.miginfocom.beans.DateGroupConnectorBean();
		dayTimeHeader = new com.miginfocom.beans.DateHeaderBean();
		dayCategoryHeader = new com.miginfocom.beans.NorthCategoryHeaderBean();
		topDayAShape = new com.miginfocom.beans.ActivityAShapeBean();
		westPanel = new javax.swing.JPanel();
		spacer2 = new javax.swing.JPanel();
		spacer1 = new javax.swing.JPanel();
		paintPanelBean2 = new com.miginfocom.beans.PaintPanelBean();
		dateSpinnerBean1 = new com.miginfocom.beans.DateSpinnerBean();
		overviewDateArea = new com.miginfocom.beans.DateAreaBean();
		southPanel = new javax.swing.JPanel();
		mainParentPanel = new javax.swing.JPanel();
		dayPanel = new javax.swing.JPanel();
		topDayPanel = new javax.swing.JPanel();
		topDayLeftPanel = new javax.swing.JPanel();
		yearLabel = new javax.swing.JLabel();
		topDayArea = new com.miginfocom.beans.DateAreaBean();
		topEndSpacer = new javax.swing.JPanel();
		topDivider = new com.miginfocom.beans.PaintPanelBean();
		dayDateArea = new com.miginfocom.beans.DateAreaBean();
		monthDateArea = new com.miginfocom.beans.DateAreaBean();
		northPanel = new com.miginfocom.beans.PaintPanelBean();
		spacer4 = new javax.swing.JPanel();
		todayButton = new javax.swing.JButton();
		spacer3 = new javax.swing.JPanel();
		dayButton = new javax.swing.JToggleButton();
		weekButton = new javax.swing.JToggleButton();
		monthButton = new javax.swing.JToggleButton();
		spacer5 = new javax.swing.JPanel();
		separatedButton = new javax.swing.JToggleButton();
		newMeetingButton = new javax.swing.JButton();

		overviewWestHeader
				.setHeaderRows(new com.miginfocom.calendar.header.CellDecorationRow[] { new com.miginfocom.calendar.header.CellDecorationRow(
						com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_MONTH,
						new com.miginfocom.util.dates.DateFormatList("MMM",
								null),
						new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
								17.0f),
						new com.miginfocom.util.gfx.geometry.AbsRect(
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f), null, null, null),
						(java.awt.Paint[]) new java.awt.Paint[] {
							new com.miginfocom.util.gfx.ShapeGradientPaint(
								new Color(161, 161, 161),
								new Color(191, 191, 191),
								0.0f, 1.0f,
								0.5f, false) },
						new java.awt.Paint[] { new java.awt.Color(255, 255, 255) },
						new com.miginfocom.util.repetition.DefaultRepetition(0,
								1, null, null),
						new java.awt.Font[] { new java.awt.Font("Dialog", 1, 11) },
						new java.lang.Integer[] { null },
						new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
								0.5f),
						new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
								0.8f)) });
		overviewWestHeader
				.setBackgroundPaint(new java.awt.Color(160, 160, 160));
		overviewWestHeader
				.setGridLineExceptions(new com.miginfocom.calendar.grid.GridLineException[] { new com.miginfocom.calendar.grid.OffsetException(
						new com.miginfocom.calendar.grid.GridLineRepetition(0,
								2147483647, null, null, 0, null, null, null)) });
		overviewWestHeader
				.setLabelRotation(com.miginfocom.ashape.shapes.TextAShape.TYPE_SINGLE_LINE_ROT_CCW);
		overviewWestHeader
				.setTextAntiAlias(com.miginfocom.util.gfx.GfxUtil.AA_HINT_LCD_HRGB);

		overviewNorthHeader
				.setHeaderRows(new com.miginfocom.calendar.header.CellDecorationRow[] { new com.miginfocom.calendar.header.CellDecorationRow(
						com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_DAY,
						new com.miginfocom.util.dates.DateFormatList("2E", null),
						new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
								25.0f),
						new com.miginfocom.util.gfx.geometry.AbsRect(
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f), null, null, null),
						(java.awt.Paint[]) null,
						new java.awt.Paint[] { new java.awt.Color(0, 0, 0) },
						new com.miginfocom.util.repetition.DefaultRepetition(0,
								1, null, null),
						new java.awt.Font[] { new java.awt.Font("Dialog", 0, 9) },
						new java.lang.Integer[] { null },
						new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
								0.5f),
						new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
								0.90000004f)) });
		overviewNorthHeader
				.setBackgroundPaint(new com.miginfocom.util.gfx.ShapeGradientPaint(
						new Color(218, 226, 233), new Color(
								235, 239, 243), 90.0f, 1.0f, 0.5f, false));
		overviewNorthHeader
				.setExpandToCorner(com.miginfocom.calendar.datearea.DateAreaContainer.CORNER_EXPAND_BOTH);
		overviewNorthHeader
				.setTextAntiAlias(com.miginfocom.util.gfx.GfxUtil.AA_HINT_LCD_HRGB);

		overviewVerticalLayout
				.setRowSizeNormal(new com.miginfocom.util.gfx.geometry.SizeSpec(
						new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
								23.0f), null, null));

		activityDayAShape.setBackground(new java.awt.Color(235, 236, 240));
		activityDayAShape.setCornerRadius(10.0);
		activityDayAShape.setOutlinePaint(new java.awt.Color(191, 197, 204));
		activityDayAShape.setPaintContext("day");
		activityDayAShape.setRepaintPadding(new java.awt.Insets(4, 4, 8, 8));
		activityDayAShape.setShadowCornerRadius(16.0);
		activityDayAShape.setShadowPaint(new java.awt.Color(0, 0, 0, 213));
		activityDayAShape
				.setShadowPlaceRect(new com.miginfocom.util.gfx.geometry.AbsRect(
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						null, null, new java.awt.Insets(0, 1, 2, 1)));
		activityDayAShape.setShadowSliceSize(20);
		activityDayAShape.setTextFont(new java.awt.Font("Dialog", 0, 10));
		activityDayAShape
				.setTextPlaceRect(new com.miginfocom.util.gfx.geometry.AbsRect(
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								3.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								13.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						null, null, null));
		activityDayAShape.setTitleFont(new java.awt.Font("Dialog", 0, 9));
		activityDayAShape
				.setTitlePlaceRect(new com.miginfocom.util.gfx.geometry.AbsRect(
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								3.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								4.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
								-14.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								15.0f), null, null, null));
		activityDayAShape.setTitleTemplate("$startTime$");

		activityMonthAShape
				.setBackgroundPlaceRect(new com.miginfocom.util.gfx.geometry.AbsRect(
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						null, null, null));
		activityMonthAShape.setCornerRadius(16.0);
		activityMonthAShape.setOutlinePaint(new java.awt.Color(191, 194, 204));
		activityMonthAShape.setOutlineStrokeWidth(0.0F);
		activityMonthAShape.setPaintContext("week");
		activityMonthAShape
				.setResizeHandles(javax.swing.SwingConstants.HORIZONTAL);
		activityMonthAShape.setShapeNamePrefix("month_");
		activityMonthAShape.setTextFont(new java.awt.Font("Dialog", 0, 9));
		activityMonthAShape.setTextTemplate("");
		activityMonthAShape
				.setTitleAlignX(new com.miginfocom.util.gfx.geometry.numbers.AtStart(
						5.0f));
		activityMonthAShape
				.setTitleAlignY(new com.miginfocom.util.gfx.geometry.numbers.AtStart(
						0.0f));
		activityMonthAShape.setTitleFont(new java.awt.Font("Dialog", 0, 10));
		activityMonthAShape.setTitleForeground(new java.awt.Color(80, 80, 120));
		activityMonthAShape
				.setTitlePlaceRect(new com.miginfocom.util.gfx.geometry.AbsRect(
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						null, null, null));
		activityMonthAShape.setTitleTemplate("$summary$");
		activityMonthAShape
				.addMouseInteractionListener(new com.miginfocom.ashape.interaction.MouseInteractionListener() {
					public void mouseInteracted(
							com.miginfocom.ashape.interaction.MouseInteractionEvent evt) {
						activityMonthAShapeMouseInteracted(evt);
					}
				});

		weekLayout
				.setRowSizeNormal(new com.miginfocom.util.gfx.geometry.SizeSpec(
						new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
								80.0f), null, null));

		weekDateHeader
				.setHeaderRows(new com.miginfocom.calendar.header.CellDecorationRow[] {
						new com.miginfocom.calendar.header.CellDecorationRow(
								com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_CUSTOM,
								new com.miginfocom.util.dates.DateFormatList(
										"MMMM yyyy", null),
								new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
										25.0f),
								new com.miginfocom.util.gfx.geometry.AbsRect(
										new com.miginfocom.util.gfx.geometry.numbers.AtStart(
												0.0f),
										new com.miginfocom.util.gfx.geometry.numbers.AtStart(
												0.0f),
										new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
												0.0f),
										new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
												0.0f), null, null, null),
								(java.awt.Paint[]) null,
								new java.awt.Paint[] { new java.awt.Color(80,
										80, 80) },
								null,
								new java.awt.Font[] { new java.awt.Font(
										"SansSerif", 1, 16) },
								new java.lang.Integer[] { null },
								new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
										0.5f),
								new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
										0.5f)),
						new com.miginfocom.calendar.header.CellDecorationRow(
								com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_DAY,
								new com.miginfocom.util.dates.DateFormatList(
										"EEEE", null),
								new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
										21.0f),
								new com.miginfocom.util.gfx.geometry.AbsRect(
										new com.miginfocom.util.gfx.geometry.numbers.AtStart(
												0.0f),
										new com.miginfocom.util.gfx.geometry.numbers.AtStart(
												0.0f),
										new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
												0.0f),
										new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
												0.0f), null, null, null),
								(java.awt.Paint[]) null,
								new java.awt.Paint[] { new java.awt.Color(118,
										118, 118) },
								new com.miginfocom.util.repetition.DefaultRepetition(
										0, 1, null, null),
								new java.awt.Font[] { new java.awt.Font(
										"Dialog", 0, 10) },
								new java.lang.Integer[] { null },
								new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
										0.5f),
								new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
										0.5f)) });
		weekDateHeader.setBackgroundPaint(new java.awt.Color(255, 255, 255));
		weekDateHeader
				.setTextAntiAlias(com.miginfocom.util.gfx.GfxUtil.AA_HINT_LCD_HRGB);

		dayLayout
				.setCompressRowsFormat(com.miginfocom.beans.GridDimensionLayoutBean.TIME_OF_DAY);
		dayLayout
				.setRowSizeNormal(new com.miginfocom.util.gfx.geometry.SizeSpec(
						new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
								20.0f), null, null));

		dayDateHeader
				.setHeaderRows(new com.miginfocom.calendar.header.CellDecorationRow[] { new com.miginfocom.calendar.header.CellDecorationRow(
						com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_DAY,
						new com.miginfocom.util.dates.DateFormatList(
								"EEE, MMM d|EEE|1E", null),
						new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
								25.0f),
						new com.miginfocom.util.gfx.geometry.AbsRect(
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f), null, null, null),
						(java.awt.Paint[]) null,
						new java.awt.Paint[] { new java.awt.Color(0, 0, 0) },
						new com.miginfocom.util.repetition.DefaultRepetition(0,
								1, null, null),
						new java.awt.Font[] { new java.awt.Font("Dialog", 0, 10) },
						new java.lang.Integer[] { null },
						new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
								0.5f),
						new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
								0.5f)) });
		dayDateHeader.setBackgroundPaint(new java.awt.Color(255, 255, 255));
		dayDateHeader
				.setTextAntiAlias(com.miginfocom.util.gfx.GfxUtil.AA_HINT_LCD_HRGB);

		yearConnector
				.setBoundaryType(com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_YEAR_MONTHS);
		yearConnector.setConnectedDateArea(overviewDateArea);
		yearConnector.setExpandCount(1);

		dayTimeHeader
				.setHeaderRows(new com.miginfocom.calendar.header.CellDecorationRow[] { new com.miginfocom.calendar.header.CellDecorationRow(
						com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_HOUR,
						new com.miginfocom.util.dates.DateFormatList("H.mm",
								null),
						new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
								46.0f),
						new com.miginfocom.util.gfx.geometry.AbsRect(
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f), null, null, null),
						(java.awt.Paint[]) null,
						new java.awt.Paint[] { new java.awt.Color(161, 161, 161) },
						new com.miginfocom.util.repetition.DefaultRepetition(
								0,
								1,
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										2.0f), null),
						new java.awt.Font[] { new java.awt.Font("Dialog", 0, 9) },
						new java.lang.Integer[] { null },
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
								-3.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								-7.0f)) });
		dayTimeHeader.setBackgroundPaint(new java.awt.Color(255, 255, 255));

		dayCategoryHeader
				.setHeaderLevels(new com.miginfocom.calendar.header.DefaultSubRowLevel[] { new com.miginfocom.calendar.header.DefaultSubRowLevel(
						"$gridRowName$",
						new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
								15.0f),
						new com.miginfocom.util.gfx.geometry.AbsRect(
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f), null, null, null),
						(java.awt.Paint[]) null,
						new java.awt.Paint[] { new java.awt.Color(102, 102, 102) },
						null,
						999,
						new java.awt.Font[] { new java.awt.Font("Dialog", 0, 9) },
						new java.lang.Integer[] { null },
						new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
								0.5f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								2.0f), 1, 0) });
		dayCategoryHeader.setBackgroundPaint(new java.awt.Color(255, 255, 255));
		dayCategoryHeader.setCellBorder(javax.swing.BorderFactory
				.createMatteBorder(0, 1, 1, 0,
						new java.awt.Color(204, 204, 204)));

		topDayAShape.setOutlinePaint(null);
		topDayAShape.setPaintContext("top");
		topDayAShape.setResizeHandles(javax.swing.SwingConstants.HORIZONTAL);
		topDayAShape.setShadowCornerRadius(12.0);
		topDayAShape.setShadowPaint(new java.awt.Color(0, 0, 0, 213));
		topDayAShape
				.setShadowPlaceRect(new com.miginfocom.util.gfx.geometry.AbsRect(
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						null, null, new java.awt.Insets(0, 1, 2, 1)));
		topDayAShape.setShapeNamePrefix("top_");
		topDayAShape
				.setTitleAlignX(new com.miginfocom.util.gfx.geometry.numbers.AtStart(
						2.0f));
		topDayAShape
				.setTitleAlignY(new com.miginfocom.util.gfx.geometry.numbers.AtStart(
						1.0f));
		topDayAShape.setTitleFont(new java.awt.Font("SansSerif", 0, 10));
		topDayAShape.setTitleTemplate("$summary$");

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setBackground(java.awt.Color.white);

		westPanel.setBackground(new java.awt.Color(219, 227, 234));
		westPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0,
				0, 1, new java.awt.Color(170, 170, 170)));
		westPanel.setMinimumSize(new java.awt.Dimension(190, 26));
		westPanel.setPreferredSize(new java.awt.Dimension(190, 100));
		westPanel.setLayout(new javax.swing.BoxLayout(westPanel,
				javax.swing.BoxLayout.Y_AXIS));

		newMeetingButton.setText("  New Meeting  ");
		newMeetingButton.setFocusPainted(false);
		newMeetingButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				newMeetingButtonActionPerformed(evt);
			}
		});
		westPanel.add(newMeetingButton);

		spacer2.setMinimumSize(new java.awt.Dimension(10, 8));
		spacer2.setOpaque(false);
		spacer2.setPreferredSize(new java.awt.Dimension(10, 8));
		westPanel.add(spacer2);

		spacer1.setMinimumSize(new java.awt.Dimension(10, 12));
		spacer1.setOpaque(false);
		spacer1.setPreferredSize(new java.awt.Dimension(10, 12));
		westPanel.add(spacer1);

		paintPanelBean2
				.setBackgroundPaint(new com.miginfocom.util.gfx.ShapeGradientPaint(
						new java.awt.Color(223, 223, 223), new java.awt.Color(
								252, 252, 252), 90.0f, 1.0f, 0.5f, false));
		paintPanelBean2.setBorder(javax.swing.BorderFactory.createMatteBorder(
				1, 0, 1, 0, new java.awt.Color(165, 165, 165)));
		paintPanelBean2.setLayout(new java.awt.FlowLayout(
				java.awt.FlowLayout.CENTER, 0, 0));

		dateSpinnerBean1
				.setAlignment(com.miginfocom.calendar.spinner.SlimDateSpinner.ALIGN_CENTER);
		dateSpinnerBean1.setArrowShadowColor(new java.awt.Color(204, 204, 204));
		dateSpinnerBean1.setArrowSize(10);
		dateSpinnerBean1.setCalendarField(java.util.Calendar.YEAR);
		dateSpinnerBean1.setDateFormatString("yyyy");
		dateSpinnerBean1.setDateGroupConnector(yearConnector);
		dateSpinnerBean1.setEditable(false);
		dateSpinnerBean1.setEditorBorder(javax.swing.BorderFactory
				.createEmptyBorder(0, 5, 0, 5));
		dateSpinnerBean1.setFont(new java.awt.Font("Dialog", 1, 12));
		paintPanelBean2.add(dateSpinnerBean1);

		westPanel.add(paintPanelBean2);

		overviewDateArea.setBackground(new java.awt.Color(219, 227, 234));
		overviewDateArea.setNorthDateHeader(overviewNorthHeader);
		overviewDateArea.setSecondaryDimensionLayout(overviewVerticalLayout);
		overviewDateArea.setWestDateHeader(overviewWestHeader);
		overviewDateArea.setActivitiesSupported(false);
		overviewDateArea
				.setBackgroundPaint(new com.miginfocom.util.gfx.ShapeGradientPaint(
						new java.awt.Color(198, 204, 211), new java.awt.Color(
								218, 226, 233), 90.0f, 1.0f, 0.5f, false));
		overviewDateArea.setDateAreaInnerBorder(javax.swing.BorderFactory
				.createMatteBorder(0, 0, 0, 1,
						new java.awt.Color(160, 160, 160)));
		overviewDateArea.setDateAreaOuterBorder(javax.swing.BorderFactory
				.createMatteBorder(1, 0, 0, 0,
						new java.awt.Color(182, 197, 212)));
		overviewDateArea.setDesignTimeHelp(false);
		overviewDateArea.setDividerPaint(new java.awt.Color(160, 160, 160));
		overviewDateArea.setHorizontalGridLinePaintEven(new java.awt.Color(182,
				197, 212));
		overviewDateArea.setHorizontalGridLinePaintOdd(new java.awt.Color(182,
				197, 212));
		overviewDateArea
				.setLabelAntiAlias(com.miginfocom.util.gfx.GfxUtil.AA_HINT_LCD_HRGB);
		overviewDateArea.setLabelBorder(javax.swing.BorderFactory
				.createMatteBorder(1, 1, 0, 0, new java.awt.Color(255, 255,
						255, 100)));
		overviewDateArea.setLabelDateFormat("d");
		overviewDateArea.setLabelFont(new java.awt.Font("SansSerif", 1, 12));
		overviewDateArea.setLabelForeground(new java.awt.Color(51, 51, 51));
		overviewDateArea
				.setLabelNowBackground(new java.awt.Color(109, 138, 183));
		overviewDateArea.setLabelNowBorder(javax.swing.BorderFactory
				.createCompoundBorder(new javax.swing.border.MatteBorder(2, 0,
						0, 0, new java.awt.Color(0, 0, 0, 150)),
						new javax.swing.border.MatteBorder(1, 1, 1, 1,
								new java.awt.Color(0, 0, 0, 50))));
		overviewDateArea.setLabelNowForeground(java.awt.Color.white);
		overviewDateArea.setLabelNowShadowForeground(java.awt.Color.black);
		overviewDateArea
				.setLabelPlaceRect(new com.miginfocom.util.gfx.geometry.AbsRect(
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						null, null, null));
		overviewDateArea.setLabelShadowForeground(java.awt.Color.white);
		overviewDateArea.setLayerForGridLines(85);
		overviewDateArea.setOpaque(false);
		overviewDateArea
				.setSelectionBoundaryType(com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_MONTH);
		overviewDateArea
				.setSelectionPaint(new com.miginfocom.util.gfx.ShapeGradientPaint(
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								30.0f), new java.awt.Color(110, 130, 150, 70),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f), new java.awt.Color(110, 130, 150, 40),
						false));
		overviewDateArea
				.setSelectionType(com.miginfocom.calendar.datearea.DateArea.SELECTION_TYPE_NORMAL);
		overviewDateArea.setVerticalGridLinePaintEven(new java.awt.Color(182,
				197, 212));
		overviewDateArea.setVerticalGridLinePaintOdd(new java.awt.Color(182,
				197, 212));
		overviewDateArea.setVerticalGridLineShowLast(true);
		overviewDateArea
				.addDateChangeListener(new com.miginfocom.util.dates.DateChangeListener() {
					public void dateRangeChanged(
							com.miginfocom.util.dates.DateChangeEvent evt) {
						overviewDateAreaDateRangeChanged(evt);
					}
				});
		westPanel.add(overviewDateArea);

		getContentPane().add(westPanel, java.awt.BorderLayout.WEST);

		southPanel.setBackground(new java.awt.Color(255, 255, 255));
		southPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT,
				0, 0));
		getContentPane().add(southPanel, java.awt.BorderLayout.SOUTH);

		mainParentPanel.setBackground(new java.awt.Color(255, 255, 255));
		mainParentPanel.setLayout(new java.awt.CardLayout());

		dayPanel.setLayout(new java.awt.BorderLayout());

		topDayPanel.setLayout(new java.awt.BorderLayout());

		topDayLeftPanel.setBackground(java.awt.Color.white);
		topDayLeftPanel.setPreferredSize(new java.awt.Dimension(46, 10));
		topDayLeftPanel.setLayout(new java.awt.FlowLayout(
				java.awt.FlowLayout.CENTER, 5, 7));

		yearLabel.setFont(new java.awt.Font("Dialog", 0, 10));
		yearLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		yearLabel.setText(String.valueOf(new java.util.GregorianCalendar()
				.get(Calendar.YEAR)));
		topDayLeftPanel.add(yearLabel);

		topDayPanel.add(topDayLeftPanel, java.awt.BorderLayout.WEST);

		topDayArea
				.setActivityLayouts(new com.miginfocom.calendar.layout.ActivityLayout[] { new com.miginfocom.calendar.layout.TimeBoundsLayout(
						new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
								1.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								1.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
								-1.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								1.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						2,
						new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
								16.0f),
						null,
						null,
						new String[] { "TimeBounds" },
						new com.miginfocom.util.dates.BoundaryRounder(
								com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_DAY,
								true, true, false, null, null, new Integer(0))) });
		topDayArea.setActivityPaintContext("top");
		topDayArea.setNorthDateHeader(dayDateHeader);
		topDayArea
				.setVisibleDateRangeString("20070916T000000000-20070920T235959999");
		topDayArea.setWrapBoundary(null);
		topDayArea.setCategoryShowRoot(true);
		topDayArea.setDesignTimeHelp(false);
		topDayArea.setHorizontalGridLinePaintEven(new java.awt.Color(204, 204,
				204));
		topDayArea.setHorizontalGridLinePaintOdd(new java.awt.Color(204, 204,
				204));
		topDayArea.setHorizontalGridLineShowFirst(true);
		topDayArea.setVerticalGridLinePaintEven(new java.awt.Color(204, 204,
				204));
		topDayArea
				.setVerticalGridLinePaintOdd(new java.awt.Color(204, 204, 204));
		topDayArea.setVerticalGridLineShowFirst(true);
		topDayArea.setVerticalGridLineShowLast(true);
		topDayPanel.add(topDayArea, java.awt.BorderLayout.CENTER);

		topEndSpacer.setBackground(java.awt.Color.white);
		topEndSpacer.setPreferredSize(new java.awt.Dimension(13, 10));
		topDayPanel.add(topEndSpacer, java.awt.BorderLayout.EAST);

		topDivider
				.setBackgroundPaint(new com.miginfocom.util.gfx.ShapeGradientPaint(
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								1.0f), new java.awt.Color(236, 236, 236),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
								-1.0f), new java.awt.Color(223, 223, 223),
						false));
		topDivider.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0,
				1, 0, new java.awt.Color(200, 200, 200)));
		topDivider.setMinimumSize(new java.awt.Dimension(100, 4));
		topDivider.setPreferredSize(new java.awt.Dimension(100, 4));
		topDivider.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,
				50, 0));
		topDayPanel.add(topDivider, java.awt.BorderLayout.SOUTH);

		dayPanel.add(topDayPanel, java.awt.BorderLayout.NORTH);

		dayDateArea
				.setActivityLayouts(new com.miginfocom.calendar.layout.ActivityLayout[] { new com.miginfocom.calendar.layout.TimeBoundsLayout(
						new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
								-0.5f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								-1.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(1.0f),
						-1, null, null, null, new String[] { "TimeBounds" },
						null) });
		dayDateArea.setActivityPaintContext("day");
		dayDateArea.setPrimaryDimension(javax.swing.SwingConstants.VERTICAL);
		dayDateArea
				.setPrimaryDimensionCellType(com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_MINUTE);
		dayDateArea.setPrimaryDimensionCellTypeCount(30);
		dayDateArea.setPrimaryDimensionLayout(dayLayout);
		dayDateArea
				.setVisibleDateRangeString("20070916T000000000-20070920T235959999");
		dayDateArea.setWestDateHeader(dayTimeHeader);
		dayDateArea.setWrapBoundary(new Integer(
				com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_DAY));
		dayDateArea.setCategoryShowRoot(true);
		dayDateArea.setDesignTimeHelp(false);
		dayDateArea.setHorizontalGridLinePaintEven(new java.awt.Color(204, 204,
				204));
		dayDateArea.setHorizontalGridLinePaintOdd(new java.awt.Color(229, 229,
				229));
		dayDateArea.setHorizontalGridLineShowFirst(true);
		dayDateArea.setHorizontalGridLineShowLast(true);
		dayDateArea.setMouseOverActivitiesOntop(true);
		dayDateArea.setSelectedActivitiesOntop(true);
		dayDateArea
				.setSelectionBoundaryType(com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_MINUTE);
		dayDateArea
				.setSelectionType(com.miginfocom.calendar.datearea.DateArea.SELECTION_TYPE_NORMAL);
		dayDateArea.setShowNoFitIcon(true);
		dayDateArea.setVerticalGridLinePaintEven(new java.awt.Color(204, 204,
				204));
		dayDateArea.setVerticalGridLinePaintOdd(new java.awt.Color(204, 204,
				204));
		dayDateArea.setVerticalGridLineShowFirst(true);
		dayDateArea
				.addDateChangeListener(new com.miginfocom.util.dates.DateChangeListener() {
					public void dateRangeChanged(
							com.miginfocom.util.dates.DateChangeEvent evt) {
						dayDateAreaDateRangeChanged(evt);
					}
				});

		dayPanel.add(dayDateArea, java.awt.BorderLayout.CENTER);

		mainParentPanel.add(dayPanel, "day");

		monthDateArea
				.setActivityLayouts(new com.miginfocom.calendar.layout.ActivityLayout[] {
						new com.miginfocom.calendar.layout.TimeBoundsLayout(
								new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
										1.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										14.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
										0.0f),
								0,
								new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
										12.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtFixed(
										8.0f),
								null,
								new String[] { "TimeBounds" },
								new com.miginfocom.util.dates.BoundaryRounder(
										com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_DAY,
										true, true, false, null, null,
										new Integer(0))),
						new com.miginfocom.calendar.layout.FlexGridLayout(
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										0.0f),
								new com.miginfocom.util.gfx.geometry.numbers.AtStart(
										1.0f),
								true,
								true,
								new Integer(1),
								null,
								new Integer(13),
								1,
								0,
								new com.miginfocom.util.gfx.geometry.AbsRect(
										new com.miginfocom.util.gfx.geometry.numbers.AtStart(
												0.0f),
										new com.miginfocom.util.gfx.geometry.numbers.AtStart(
												14.0f),
										new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
												0.0f),
										new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
												0.0f), null, null, null),
								new String[] { "FlexGrid" }) });
		monthDateArea.setActivityPaintContext("week");
		monthDateArea.setNorthDateHeader(weekDateHeader);
		monthDateArea.setSecondaryDimensionLayout(weekLayout);
		monthDateArea.setDesignTimeHelp(false);
		monthDateArea.setDividerPaint(new java.awt.Color(161, 165, 180));
		monthDateArea.setHorizontalGridLinePaintEven(new java.awt.Color(204,
				204, 204));
		monthDateArea.setHorizontalGridLinePaintOdd(new java.awt.Color(204,
				204, 204));
		monthDateArea.setHorizontalGridLineShowFirst(true);
		monthDateArea
				.setLabelAlignX(new com.miginfocom.util.gfx.geometry.numbers.AtFraction(
						1.0f));
		monthDateArea
				.setLabelAlignY(new com.miginfocom.util.gfx.geometry.numbers.AtStart(
						0.0f));
		monthDateArea
				.setLabelAntiAlias(com.miginfocom.util.gfx.GfxUtil.AA_HINT_LCD_HRGB);
		monthDateArea.setLabelDateFormat("d");
		monthDateArea.setLabelFirstDateFormat("d");
		monthDateArea.setLabelFont(new java.awt.Font("SansSerif", 0, 10));
		monthDateArea.setLabelForeground(new java.awt.Color(48, 48, 48));
		monthDateArea
				.setLabelPlaceRect(new com.miginfocom.util.gfx.geometry.AbsRect(
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								0.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								2.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtEnd(
								-3.0f),
						new com.miginfocom.util.gfx.geometry.numbers.AtStart(
								15.0f), null, null, null));
		monthDateArea.setLayoutOptimizeBoundary(new Integer(
				com.miginfocom.util.dates.DateRangeI.RANGE_TYPE_WEEK));
		monthDateArea.setSelectionMousePressedPaint(new java.awt.Color(251,
				248, 244));
		monthDateArea
				.setSelectionType(com.miginfocom.calendar.datearea.DateArea.SELECTION_TYPE_NORMAL);
		monthDateArea.setVerticalGridLinePaintEven(new java.awt.Color(204, 204,
				204));
		monthDateArea.setVerticalGridLinePaintOdd(new java.awt.Color(204, 204,
				204));
		monthDateArea
				.addDateChangeListener(new com.miginfocom.util.dates.DateChangeListener() {
					public void dateRangeChanged(
							com.miginfocom.util.dates.DateChangeEvent evt) {
						monthDateAreaDateRangeChanged(evt);
					}
				});
		mainParentPanel.add(monthDateArea, "month");

		getContentPane().add(mainParentPanel, java.awt.BorderLayout.CENTER);

		northPanel
				.setBackgroundPaint(new com.miginfocom.util.gfx.ShapeGradientPaint(
						new java.awt.Color(179, 179, 179), new java.awt.Color(
								209, 209, 209), 90.0f, 0.90000004f, 0.5f, false));
		northPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0,
				1, 0, new java.awt.Color(100, 100, 100)));
		northPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,
				7, 7));

		spacer4.setOpaque(false);
		northPanel.add(spacer4);

		todayButton.setText(" Today ");
		todayButton.setDefaultCapable(false);
		todayButton.setFocusPainted(false);
		todayButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				todayButtonActionPerformed(evt);
			}
		});
		northPanel.add(todayButton);

		spacer3.setOpaque(false);
		spacer3.setPreferredSize(new java.awt.Dimension(300, 12));
		northPanel.add(spacer3);

		dayButton.setText("   Day   ");
		dayButton.setFocusPainted(false);
		dayButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				dayButtonActionPerformed(evt);
			}
		});
		northPanel.add(dayButton);

		weekButton.setSelected(true);
		weekButton.setText("  Week  ");
		weekButton.setFocusPainted(false);
		weekButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				weekButtonActionPerformed(evt);
			}
		});
		northPanel.add(weekButton);

		monthButton.setText(" Month ");
		monthButton.setFocusPainted(false);
		monthButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				monthButtonActionPerformed(evt);
			}
		});
		northPanel.add(monthButton);

		spacer5.setOpaque(false);
		spacer5.setPreferredSize(new java.awt.Dimension(50, 12));
		northPanel.add(spacer5);

		separatedButton.setText(" Show Separated Calendars ");
		separatedButton.setEnabled(false);
		separatedButton.setFocusPainted(false);
		separatedButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				separatedButtonActionPerformed(evt);
			}
		});
		northPanel.add(separatedButton);

		getContentPane().add(northPanel, java.awt.BorderLayout.PAGE_START);

		pack();
	}// </editor-fold>//GEN-END:initComponents

	private void dayDateAreaDateRangeChanged(
			com.miginfocom.util.dates.DateChangeEvent evt)// GEN-FIRST:event_dayDateAreaDateRangeChanged
	{// GEN-HEADEREND:event_dayDateAreaDateRangeChanged
		// This is the code that creates an activity by dragging in the day/days
		// date area.
		if (evt.getType() == DateChangeEvent.PRESSED) {

			if (newCreatedAct == null && evt.getNewRange().getMillisSpanned(false, false) > 45*60*1000) {
				newCreatedAct = new DefaultActivity(evt.getNewRange(), new Long(new Random().nextLong()));
				String summary = (String) JOptionPane.showInputDialog(this, "Choose a description for event");
				newCreatedAct.setSummary(summary);
				ActivityDepository.getInstance(dayDateArea.getActivityDepositoryContext()).addBrokedActivity(newCreatedAct, this, TimeSpanListEvent.ADDED_CREATED);
			} else {
				try {
					newCreatedAct.setBaseDateRange(evt.getNewRange());
				} catch (Exception ex) {}
			}
		}

	}

	protected void newMeetingButtonActionPerformed(ActionEvent evt) {
		InitiateMeetingFrame initmeetingFrame = new InitiateMeetingFrame(name,
				kw);
		initmeetingFrame.setVisible(true);
	}

	private void weekButtonActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_weekButtonActionPerformed
	{// GEN-HEADEREND:event_weekButtonActionPerformed
		setMode(DateRangeI.RANGE_TYPE_WEEK);
	}// GEN-LAST:event_weekButtonActionPerformed

	private void monthButtonActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_monthButtonActionPerformed
	{// GEN-HEADEREND:event_monthButtonActionPerformed
		setMode(DateRangeI.RANGE_TYPE_MONTH);
	}// GEN-LAST:event_monthButtonActionPerformed

	private void dayButtonActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_dayButtonActionPerformed
	{// GEN-HEADEREND:event_dayButtonActionPerformed
		setMode(DateRangeI.RANGE_TYPE_DAY);
	}// GEN-LAST:event_dayButtonActionPerformed

	private void monthDateAreaDateRangeChanged(
			com.miginfocom.util.dates.DateChangeEvent evt)// GEN-FIRST:event_monthDateAreaDateRangeChanged
	{// GEN-HEADEREND:event_monthDateAreaDateRangeChanged
		// Code for click on the month date grid

		// if (evt.getType() == DateChangeEvent.SELECTED)
		// JOptionPane.showMessageDialog(this, "You pressed: " +
		// evt.getNewRange());
	}// GEN-LAST:event_monthDateAreaDateRangeChanged

	private void activityMonthAShapeMouseInteracted(
			com.miginfocom.ashape.interaction.MouseInteractionEvent evt)// GEN-FIRST:event_activityMonthAShapeMouseInteracted
	{// GEN-HEADEREND:event_activityMonthAShapeMouseInteracted
		// Code to show a dialog when you click an activity in the month view

		// if (evt.getEventKey() == MouseKeyInteractor.MOUSE_CLICK) {
		// Component comp = evt.getOriginalEvent().getComponent();
		//
		// if (comp == currentDateArea.getDateArea()) {
		// ActivityView view = (ActivityView)
		// evt.getMouseKeyInteractor().getInteracted();
		// JOptionPane.showMessageDialog(comp, "You clicked: " +
		// view.getModel().getSummary());
		// }
		// }
	}// GEN-LAST:event_activityMonthAShapeMouseInteracted

	private void overviewDateAreaDateRangeChanged(
			com.miginfocom.util.dates.DateChangeEvent evt) {// GEN-FIRST:event_overviewDateAreaDateRangeChanged
		if (evt.getType() == DateChangeEvent.SELECTED) {
			currentDateArea.getDateArea()
					.setVisibleDateRange(evt.getNewRange());
			topDayArea.getDateArea().setVisibleDateRange(evt.getNewRange());

			currentDateArea.revalidate();
			topDayArea.revalidate();
		}
	}// GEN-LAST:event_overviewDateAreaDateRangeChanged

	private void todayButtonActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_todayButtonActionPerformed
	{// GEN-HEADEREND:event_todayButtonActionPerformed
		DateRange todayRange = new DateRange();
		int bType = overviewDateArea.getSelectionBoundaryType();
		todayRange.setSize(bType, 1, MutableDateRange.ALIGN_CENTER_DOWN);
		overviewDateArea.getDateArea().setSelectedRange(todayRange);
	}// GEN-LAST:event_todayButtonActionPerformed

	private void separatedButtonActionPerformed(java.awt.event.ActionEvent evt)// GEN-FIRST:event_separatedButtonActionPerformed
	{// GEN-HEADEREND:event_separatedButtonActionPerformed
		if (separatedButton.isSelected() && separatedButton.isEnabled()) {
			separatedButton.setForeground(Color.WHITE);
			dayDateArea.setCategoryHeader(dayCategoryHeader);
		} else {
			separatedButton.setForeground(Color.BLACK);
			dayDateArea.setCategoryHeader(null);
		}

		currentDateArea.revalidate();
	}// GEN-LAST:event_separatedButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private com.miginfocom.beans.ActivityAShapeBean activityDayAShape;
	private com.miginfocom.beans.ActivityAShapeBean activityMonthAShape;
	private com.miginfocom.beans.DateSpinnerBean dateSpinnerBean1;
	private javax.swing.JToggleButton dayButton;
	private com.miginfocom.beans.NorthCategoryHeaderBean dayCategoryHeader;
	private com.miginfocom.beans.DateAreaBean dayDateArea;
	private com.miginfocom.beans.DateHeaderBean dayDateHeader;
	private com.miginfocom.beans.GridDimensionLayoutBean dayLayout;
	private javax.swing.JPanel dayPanel;
	private com.miginfocom.beans.DateHeaderBean dayTimeHeader;
	private javax.swing.JPanel mainParentPanel;
	private javax.swing.JToggleButton monthButton;
	private com.miginfocom.beans.DateAreaBean monthDateArea;
	private com.miginfocom.beans.PaintPanelBean northPanel;
	private com.miginfocom.beans.DateAreaBean overviewDateArea;
	private com.miginfocom.beans.DateHeaderBean overviewNorthHeader;
	private com.miginfocom.beans.GridDimensionLayoutBean overviewVerticalLayout;
	private com.miginfocom.beans.DateHeaderBean overviewWestHeader;
	private com.miginfocom.beans.PaintPanelBean paintPanelBean2;
	private javax.swing.JToggleButton separatedButton;
	private javax.swing.JPanel southPanel;
	private javax.swing.JPanel spacer1;
	private javax.swing.JPanel spacer2;
	private javax.swing.JPanel spacer3;
	private javax.swing.JPanel spacer4;
	private javax.swing.JPanel spacer5;
	private javax.swing.JButton todayButton;
	private com.miginfocom.beans.ActivityAShapeBean topDayAShape;
	private com.miginfocom.beans.DateAreaBean topDayArea;
	private javax.swing.JPanel topDayLeftPanel;
	private javax.swing.JPanel topDayPanel;
	private com.miginfocom.beans.PaintPanelBean topDivider;
	private javax.swing.JPanel topEndSpacer;
	private javax.swing.JToggleButton weekButton;
	private com.miginfocom.beans.DateHeaderBean weekDateHeader;
	private com.miginfocom.beans.GridDimensionLayoutBean weekLayout;
	private javax.swing.JPanel westPanel;
	private com.miginfocom.beans.DateGroupConnectorBean yearConnector;
	private javax.swing.JLabel yearLabel;
	private javax.swing.JButton newMeetingButton;

	// End of variables declaration//GEN-END:variables

	@Override
	public void incomingMeetingProposal(Meeting meeting) {
		MeetingProposalFrame meetingProposal = new MeetingProposalFrame(
				this.name, meeting, this.kw);
		meetingProposal.setVisible(true);
	}

	@Override
	public void chooseMeetingTimeSlot(UUID id,
			Map<ImmutableDateRange, MeetingTimeSlot> slots) {
		ChooseMeetingtimeSlotFrame meetingTimeSlot = new ChooseMeetingtimeSlotFrame(
				this.name, id, slots, this.kw);
		meetingTimeSlot.setVisible(true);

	}

	@Override
	public void createActivity(ImmutableDateRange immutableDateRange,
			String description, UUID id) {
		DefaultActivity activity = new DefaultActivity(immutableDateRange, id);
		activity.setSummary(description);
		ActivityDepository.getInstance(this.name).addBrokedActivity(activity,
				this, TimeSpanListEvent.ADDED_CREATED);
	}

	@Override
	public void activityMoved(ActivityMoveEvent arg0) {
		// No activity could be moved
	}

	@Override
	public void activityDragResized(ActivityDragResizeEvent arg0) {
		// No activity could be resized
	}
}
