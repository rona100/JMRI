// TrainsTableFrame.java

package jmri.jmrit.operations.trains;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Frame;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import javax.swing.JButton;
import javax.swing.JTable;
import jmri.jmrit.operations.OperationsFrame;
import jmri.jmrit.operations.OperationsXml;
import jmri.jmrit.operations.locations.Location;
import jmri.jmrit.operations.locations.LocationManager;
import jmri.jmrit.operations.rollingstock.cars.CarManagerXml;
import jmri.jmrit.operations.rollingstock.engines.EngineManagerXml;
import jmri.jmrit.operations.setup.AutoSave;
import jmri.jmrit.operations.setup.BuildReportOptionAction;
import jmri.jmrit.operations.setup.Control;
import jmri.jmrit.operations.setup.OptionAction;
import jmri.jmrit.operations.setup.PrintOptionAction;
import jmri.jmrit.operations.setup.Setup;
import jmri.util.com.sun.TableSorter;

/**
 * Frame for adding and editing the train roster for operations.
 * 
 * @author Bob Jacobsen Copyright (C) 2001
 * @author Daniel Boudreau Copyright (C) 2008, 2009, 2010, 2011, 2012, 2013, 2014
 * @version $Revision$
 */
public class TrainsTableFrame extends OperationsFrame implements java.beans.PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4237149773850338265L;
	public static final String MOVE = Bundle.getMessage("Move");
	public static final String TERMINATE = Bundle.getMessage("Terminate");
	public static final String RESET = Bundle.getMessage("Reset");
	public static final String CONDUCTOR = Bundle.getMessage("Conductor");

	CarManagerXml carManagerXml = CarManagerXml.instance(); // load cars
	EngineManagerXml engineManagerXml = EngineManagerXml.instance(); // load engines
	TrainManager trainManager = TrainManager.instance();
	TrainManagerXml trainManagerXml = TrainManagerXml.instance();
	LocationManager locationManager = LocationManager.instance();

	TrainsTableModel trainsModel;
	TableSorter sorter;
	JTable trainsTable;
	JScrollPane trainsPane;
	
	// labels
	JLabel numTrains = new JLabel();
	JLabel textTrains = new JLabel(Bundle.getMessage("Trains").toLowerCase());
	JLabel textSep1 = new JLabel("      ");

	// radio buttons
	JRadioButton showTime = new JRadioButton(Bundle.getMessage("Time"));
	JRadioButton showId = new JRadioButton(Bundle.getMessage("Id"));

	JRadioButton moveRB = new JRadioButton(MOVE);
	JRadioButton terminateRB = new JRadioButton(TERMINATE);
	JRadioButton resetRB = new JRadioButton(RESET);
	JRadioButton conductorRB = new JRadioButton(CONDUCTOR);

	// major buttons
	JButton addButton = new JButton(Bundle.getMessage("Add"));
	JButton buildButton = new JButton(Bundle.getMessage("Build"));
	JButton printButton = new JButton(Bundle.getMessage("Print"));
	JButton openFileButton = new JButton(Bundle.getMessage("OpenFile"));
	JButton runFileButton = new JButton(Bundle.getMessage("RunFile"));
	JButton switchListsButton = new JButton(Bundle.getMessage("SwitchLists"));
	JButton terminateButton = new JButton(Bundle.getMessage("Terminate"));
	JButton saveButton = new JButton(Bundle.getMessage("SaveBuilds"));

	// check boxes
	JCheckBox buildMsgBox = new JCheckBox(Bundle.getMessage("BuildMessages"));
	JCheckBox buildReportBox = new JCheckBox(Bundle.getMessage("BuildReport"));
	JCheckBox printPreviewBox = new JCheckBox(Bundle.getMessage("Preview"));
	JCheckBox openFileBox = new JCheckBox(Bundle.getMessage("OpenFile"));
	JCheckBox runFileBox = new JCheckBox(Bundle.getMessage("RunFile"));
	JCheckBox showAllBox = new JCheckBox(Bundle.getMessage("ShowAllTrains"));

	public TrainsTableFrame() {
		super();

		updateTitle();

		// create ShutDownTasks
		createShutDownTask();
		// always check for dirty operations files
		setModifiedFlag(true);

		// general GUI configuration
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		// Set up the jtable in a Scroll Pane..
		trainsModel = new TrainsTableModel();
		sorter = new TableSorter(trainsModel);
		trainsTable = new JTable(sorter);
		sorter.setTableHeader(trainsTable.getTableHeader());
		trainsPane = new JScrollPane(trainsTable);
		trainsModel.initTable(trainsTable, this);

		// Set up the control panel
		// row 1
		JPanel cp1 = new JPanel();
		cp1.setLayout(new BoxLayout(cp1, BoxLayout.X_AXIS));

		JPanel show = new JPanel();
		show.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("ShowClickToSort")));
		show.add(showTime);
		show.add(showId);

		JPanel options = new JPanel();
		options.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Options")));
		options.add(showAllBox);
		options.add(buildMsgBox);
		options.add(buildReportBox);
		options.add(printPreviewBox);
		options.add(openFileBox);
		options.add(runFileBox);

		JPanel action = new JPanel();
		action.setBorder(BorderFactory.createTitledBorder(Bundle.getMessage("Action")));
		action.add(moveRB);
		action.add(conductorRB);
		action.add(terminateRB);
		action.add(resetRB);

		cp1.add(show);
		cp1.add(options);
		cp1.add(action);

		// tool tips, see setPrintButtonText() for more tool tips
		addButton.setToolTipText(Bundle.getMessage("AddTrain"));
		buildButton.setToolTipText(Bundle.getMessage("BuildSelectedTip"));
		switchListsButton.setToolTipText(Bundle.getMessage("PreviewPrintSwitchLists"));

		terminateButton.setToolTipText(Bundle.getMessage("TerminateSelectedTip"));
		saveButton.setToolTipText(Bundle.getMessage("SaveBuildsTip"));
		openFileButton.setToolTipText(Bundle.getMessage("OpenFileButtonTip"));
		runFileButton.setToolTipText(Bundle.getMessage("RunFileButtonTip"));
		buildMsgBox.setToolTipText(Bundle.getMessage("BuildMessagesTip"));
		printPreviewBox.setToolTipText(Bundle.getMessage("PreviewTip"));
		openFileBox.setToolTipText(Bundle.getMessage("OpenFileTip"));
		runFileBox.setToolTipText(Bundle.getMessage("RunFileTip"));
		showAllBox.setToolTipText(Bundle.getMessage("ShowAllTrainsTip"));

		moveRB.setToolTipText(Bundle.getMessage("MoveTip"));
		terminateRB.setToolTipText(Bundle.getMessage("TerminateTip"));
		resetRB.setToolTipText(Bundle.getMessage("ResetTip"));

		// row 2
		JPanel addTrain = new JPanel();
		addTrain.setBorder(BorderFactory.createTitledBorder(""));
		addTrain.add(numTrains);
		addTrain.add(textTrains);
		addTrain.add(textSep1);
		addTrain.add(addButton);
		
		numTrains.setText(Integer.toString(trainManager.getNumEntries()));

		JPanel select = new JPanel();
		select.setBorder(BorderFactory.createTitledBorder(""));
		select.add(buildButton);
		select.add(printButton);
		select.add(openFileButton);
		select.add(runFileButton);
		select.add(switchListsButton);
		select.add(terminateButton);

		JPanel save = new JPanel();
		save.setBorder(BorderFactory.createTitledBorder(""));
		save.add(saveButton);

		JPanel cp2 = new JPanel();
		cp2.setLayout(new BoxLayout(cp2, BoxLayout.X_AXIS));
		cp2.add(addTrain);
		cp2.add(select);
		cp2.add(save);

		// place controls in scroll pane
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		controlPanel.add(cp1);
		controlPanel.add(cp2);

		JScrollPane controlPane = new JScrollPane(controlPanel);

		getContentPane().add(trainsPane);
		getContentPane().add(controlPane);

		// setup buttons
		addButtonAction(addButton);
		addButtonAction(buildButton);
		addButtonAction(printButton);
		addButtonAction(openFileButton);
		addButtonAction(runFileButton);
		addButtonAction(switchListsButton);
		addButtonAction(terminateButton);
		addButtonAction(saveButton);

		ButtonGroup showGroup = new ButtonGroup();
		showGroup.add(showTime);
		showGroup.add(showId);
		showTime.setSelected(true);

		ButtonGroup actionGroup = new ButtonGroup();
		actionGroup.add(moveRB);
		actionGroup.add(conductorRB);
		actionGroup.add(terminateRB);
		actionGroup.add(resetRB);

		addRadioButtonAction(showTime);
		addRadioButtonAction(showId);

		addRadioButtonAction(moveRB);
		addRadioButtonAction(terminateRB);
		addRadioButtonAction(resetRB);
		addRadioButtonAction(conductorRB);

		buildMsgBox.setSelected(trainManager.isBuildMessagesEnabled());
		buildReportBox.setSelected(trainManager.isBuildReportEnabled());
		printPreviewBox.setSelected(trainManager.isPrintPreviewEnabled());
		openFileBox.setSelected(trainManager.isOpenFileEnabled());
		runFileBox.setSelected(trainManager.isRunFileEnabled());
		showAllBox.setSelected(trainsModel.isShowAll());

		// show open files only if create csv is enabled
		updateRunAndOpenButtons();

		addCheckBoxAction(buildMsgBox);
		addCheckBoxAction(buildReportBox);
		addCheckBoxAction(printPreviewBox);
		addCheckBoxAction(showAllBox);
		addCheckBoxAction(openFileBox);
		addCheckBoxAction(runFileBox);

		// Set the button text to Print or Preview
		setPrintButtonText();
		// Set the train action button text to Move or Terminate
		setTrainActionButton();

		// build menu
		JMenuBar menuBar = new JMenuBar();
		JMenu toolMenu = new JMenu(Bundle.getMessage("Tools"));
		toolMenu.add(new OptionAction(Bundle.getMessage("TitleOptions")));
		toolMenu.add(new PrintOptionAction());
		toolMenu.add(new BuildReportOptionAction());
		toolMenu.add(new TrainsByCarTypeAction(Bundle.getMessage("TitleModifyTrains")));
		toolMenu.add(new TrainByCarTypeAction(Bundle.getMessage("MenuItemShowCarTypes"), null));
		toolMenu.add(new ChangeDepartureTimesAction(Bundle.getMessage("TitleChangeDepartureTime")));
		toolMenu.add(new TrainsTableSetColorAction());
		toolMenu.add(new TrainsScheduleAction(Bundle.getMessage("TitleTimeTableTrains")));
		toolMenu.add(new TrainCopyAction(Bundle.getMessage("TitleTrainCopy")));
		toolMenu.add(new TrainsScriptAction(Bundle.getMessage("MenuItemScripts"), this));
		toolMenu.add(new SetupExcelProgramFrameAction(Bundle.getMessage("MenuItemSetupExcelProgram")));
		toolMenu.add(new PrintTrainsAction(Bundle.getMessage("MenuItemPrint"), new Frame(), false, this));
		toolMenu.add(new PrintTrainsAction(Bundle.getMessage("MenuItemPreview"), new Frame(), true, this));

		menuBar.add(toolMenu);
		menuBar.add(new jmri.jmrit.operations.OperationsMenu());
		setJMenuBar(menuBar);

		// add help menu to window
		addHelpMenu("package.jmri.jmrit.operations.Operations_Trains", true); // NOI18N

		initMinimumSize();
		
		addHorizontalScrollBarKludgeFix(controlPane, controlPanel);

		// listen for timetable changes
		trainManager.addPropertyChangeListener(this);
		Setup.addPropertyChangeListener(this);
		// listen for location switch list changes
		addPropertyChangeLocations();

		// auto save
		new AutoSave();
	}

	public void radioButtonActionPerformed(java.awt.event.ActionEvent ae) {
		log.debug("radio button activated");
		if (ae.getSource() == showId) {
			trainsModel.setSort(trainsModel.SORTBYID);
		}
		if (ae.getSource() == showTime) {
			trainsModel.setSort(trainsModel.SORTBYTIME);
		}
		if (ae.getSource() == moveRB) {
			trainManager.setTrainsFrameTrainAction(MOVE);
		}
		if (ae.getSource() == terminateRB) {
			trainManager.setTrainsFrameTrainAction(TERMINATE);
		}
		if (ae.getSource() == resetRB) {
			trainManager.setTrainsFrameTrainAction(RESET);
		}
		if (ae.getSource() == conductorRB) {
			trainManager.setTrainsFrameTrainAction(CONDUCTOR);
		}
	}

	TrainSwitchListEditFrame tslef;

	// add, build, print, switch lists, terminate, and save buttons
	public void buttonActionPerformed(java.awt.event.ActionEvent ae) {
		// log.debug("train button activated");
		if (ae.getSource() == addButton) {
			TrainEditFrame f = new TrainEditFrame();
			f.initComponents(null);
		}
		if (ae.getSource() == buildButton) {
			// uses a thread which allows table updates during build
			trainManager.buildSelectedTrains(getSortByList());
		}
		if (ae.getSource() == printButton) {
			trainManager.printSelectedTrains(getSortByList());
		}
		if (ae.getSource() == openFileButton) {
			// open the csv files
			List<Train> trains = getSortByList();
			for (Train train : trains) {
				if (train.isBuildEnabled()) {
					if (!train.isBuilt() && trainManager.isBuildMessagesEnabled()) {
						JOptionPane.showMessageDialog(this, MessageFormat.format(Bundle
								.getMessage("NeedToBuildBeforeOpenFile"), new Object[] {
								train.getName(),
								(trainManager.isPrintPreviewEnabled() ? Bundle.getMessage("preview") : Bundle
										.getMessage("print")) }), MessageFormat.format(Bundle
								.getMessage("CanNotPrintManifest"),
								new Object[] { trainManager.isPrintPreviewEnabled() ? Bundle.getMessage("preview")
										: Bundle.getMessage("print") }), JOptionPane.ERROR_MESSAGE);
					} else if (train.isBuilt()) {
						train.openFile();
					}
				}
			}
		}
		if (ae.getSource() == runFileButton) {
			// Processes the CSV Manifest files using an external custom program.
			if (!TrainCustomManifest.manifestCreatorFileExists()) {
				log.warn("Manifest creator file not found!, directory name: " + TrainCustomManifest.getDirectoryName()
						+ ", file name: " + TrainCustomManifest.getFileName()); // NOI18N
				JOptionPane.showMessageDialog(this, MessageFormat.format(
						Bundle.getMessage("LoadDirectoryNameFileName"), new Object[] {
								TrainCustomManifest.getDirectoryName(), TrainCustomManifest.getFileName() }), Bundle
						.getMessage("ManifestCreatorNotFound"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			List<Train> trains = getSortByList();
			for (Train train : trains) {
				if (train.isBuildEnabled()) {
					if (!train.isBuilt() && trainManager.isBuildMessagesEnabled()) {
						JOptionPane.showMessageDialog(this, MessageFormat.format(Bundle
								.getMessage("NeedToBuildBeforeRunFile"), new Object[] {
								train.getName(),
								(trainManager.isPrintPreviewEnabled() ? Bundle.getMessage("preview") : Bundle
										.getMessage("print")) }), MessageFormat.format(Bundle
								.getMessage("CanNotPrintManifest"),
								new Object[] { trainManager.isPrintPreviewEnabled() ? Bundle.getMessage("preview")
										: Bundle.getMessage("print") }), JOptionPane.ERROR_MESSAGE);
					} else if (train.isBuilt()) {
						// Make sure our csv manifest file exists for this Train.
						File csvFile = train.createCSVManifestFile();
						// Add it to our collection to be processed.
						TrainCustomManifest.addCVSFile(csvFile);
					}
				}
			}

			// Now run the user specified custom Manifest processor program
			TrainCustomManifest.process();
		}
		if (ae.getSource() == switchListsButton) {
			if (tslef != null)
				tslef.dispose();
			tslef = new TrainSwitchListEditFrame();
			tslef.initComponents();
		}
		if (ae.getSource() == terminateButton) {
			trainManager.terminateSelectedTrains(getSortByList());
		}
		if (ae.getSource() == saveButton) {
			storeValues();
		}
	}

	int _status = TableSorter.ASCENDING;

	protected String getSortBy() {
		// set the defaults
		String sortBy = TrainsTableModel.TIMECOLUMNNAME;
		_status = TableSorter.ASCENDING;
		// now look to see if a sort is active
		for (int i = 0; i < sorter.getColumnCount(); i++) {
			String name = sorter.getColumnName(i);
			int status = sorter.getSortingStatus(i);
			// log.debug("Column " + name + " status " + status);
			if (status != TableSorter.NOT_SORTED && !name.equals("")) {
				sortBy = name;
				_status = status;
				break;
			}
		}
		return sortBy;
	}

	public List<Train> getSortByList() {
		List<Train> sysList;
		String sortBy = getSortBy();
		if (sortBy.equals(TrainsTableModel.IDCOLUMNNAME))
			sysList = trainManager.getTrainsByIdList();
		else if (sortBy.equals(TrainsTableModel.TIMECOLUMNNAME))
			sysList = trainManager.getTrainsByTimeList();
		else if (sortBy.equals(TrainsTableModel.DEPARTSCOLUMNNAME))
			sysList = trainManager.getTrainsByDepartureList();
		else if (sortBy.equals(TrainsTableModel.TERMINATESCOLUMNNAME))
			sysList = trainManager.getTrainsByTerminatesList();
		else if (sortBy.equals(TrainsTableModel.ROUTECOLUMNNAME))
			sysList = trainManager.getTrainsByRouteList();
		else if (sortBy.equals(TrainsTableModel.STATUSCOLUMNNAME))
			sysList = trainManager.getTrainsByStatusList();
		else
			sysList = trainManager.getTrainsByNameList();
		return sysList;
	}

	// Modifies button text and tool tips
	private void setPrintButtonText() {
		if (printPreviewBox.isSelected()) {
			printButton.setText(Bundle.getMessage("Preview"));
			printButton.setToolTipText(Bundle.getMessage("PreviewSelectedTip"));
			buildReportBox.setToolTipText(Bundle.getMessage("BuildReportPreviewTip"));
		} else {
			printButton.setText(Bundle.getMessage("Print"));
			printButton.setToolTipText(Bundle.getMessage("PrintSelectedTip"));
			buildReportBox.setToolTipText(Bundle.getMessage("BuildReportPrintTip"));
		}
	}

	private void setTrainActionButton() {
		moveRB.setSelected(trainManager.getTrainsFrameTrainAction().equals(TrainsTableFrame.MOVE));
		terminateRB.setSelected(trainManager.getTrainsFrameTrainAction().equals(TrainsTableFrame.TERMINATE));
		resetRB.setSelected(trainManager.getTrainsFrameTrainAction().equals(TrainsTableFrame.RESET));
		conductorRB.setSelected(trainManager.getTrainsFrameTrainAction().equals(TrainsTableFrame.CONDUCTOR));
	}

	public void checkBoxActionPerformed(java.awt.event.ActionEvent ae) {
		if (ae.getSource() == buildMsgBox) {
			trainManager.setBuildMessagesEnabled(buildMsgBox.isSelected());
		}
		if (ae.getSource() == buildReportBox) {
			trainManager.setBuildReportEnabled(buildReportBox.isSelected());
		}
		if (ae.getSource() == printPreviewBox) {
			trainManager.setPrintPreviewEnabled(printPreviewBox.isSelected());
			setPrintButtonText(); // set the button text for Print or Preview
		}
		if (ae.getSource() == openFileBox) {
			trainManager.setOpenFileEnabled(openFileBox.isSelected());
			runFileBox.setSelected(false);
			trainManager.setRunFileEnabled(false);
		}
		if (ae.getSource() == runFileBox) {
			trainManager.setRunFileEnabled(runFileBox.isSelected());
			openFileBox.setSelected(false);
			trainManager.setOpenFileEnabled(false);
		}
		if (ae.getSource() == showAllBox) {
			trainsModel.setShowAll(showAllBox.isSelected());
		}
	}

	private void updateTitle() {
		String title = Bundle.getMessage("TitleTrainsTable");
		TrainSchedule sch = TrainScheduleManager.instance().getScheduleById(trainManager.getTrainScheduleActiveId());
		if (sch != null)
			title = title + " (" + sch.getName() + ")";
		setTitle(title);
	}

	private void updateSwitchListButton() {
		log.debug("update switch list button");
		List<Location> locations = locationManager.getList();
		for (Location location : locations) {
			if (location != null && location.isSwitchListEnabled() && location.getStatus().equals(Location.MODIFIED)) {
				switchListsButton.setBackground(Color.RED);
				return;
			}
		}
		switchListsButton.setBackground(Color.GREEN);
	}

	// show open files only if create csv is enabled
	private void updateRunAndOpenButtons() {
		openFileBox.setVisible(Setup.isGenerateCsvManifestEnabled());
		openFileButton.setVisible(Setup.isGenerateCsvManifestEnabled());
		runFileBox.setVisible(Setup.isGenerateCsvManifestEnabled());
		runFileButton.setVisible(Setup.isGenerateCsvManifestEnabled());
	}

	private synchronized void addPropertyChangeLocations() {
		List<Location> locations = locationManager.getList();
		for (Location location : locations) {
			location.addPropertyChangeListener(this);
		}
	}

	private synchronized void removePropertyChangeLocations() {
		List<Location> locations = locationManager.getList();
		for (Location location : locations) {
			location.removePropertyChangeListener(this);
		}
	}

	public void dispose() {
		/*
		 * all JMRI window position and size are now saved in user preference file
		 * trainManager.setTrainsFrameTableColumnWidths(getCurrentTableColumnWidths()); // save column widths
		 * trainManager.setTrainsFrame(null);
		 */
		trainsModel.dispose();
		trainManager.runShutDownScripts();
		trainManager.removePropertyChangeListener(this);
		Setup.removePropertyChangeListener(this);
		removePropertyChangeLocations();
		super.dispose();
	}

	protected void handleModified() {
		if (OperationsXml.areFilesDirty()) {
			int result = javax.swing.JOptionPane.showOptionDialog(this,
					Bundle.getMessage("PromptQuitWindowNotWritten"), Bundle.getMessage("PromptSaveQuit"),
					javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE, null, // icon
					new String[] { ResourceBundle.getBundle("jmri.util.UtilBundle").getString("WarnYesSave"), // NOI18N
							ResourceBundle.getBundle("jmri.util.UtilBundle").getString("WarnNoClose") }, // NOI18N
					ResourceBundle.getBundle("jmri.util.UtilBundle").getString("WarnYesSave"));
			if (result == javax.swing.JOptionPane.NO_OPTION) {
				return;
			}
			// user wants to save
			storeValues();
		}
	}

	protected void storeValues() {
		super.storeValues();
		saveTableDetails(trainsTable);
	}

	public void propertyChange(java.beans.PropertyChangeEvent e) {
		if (Control.showProperty && log.isDebugEnabled())
			log.debug("Property change: ({}) old: ({}) new: ({})", e.getPropertyName(), e.getOldValue(), e
					.getNewValue());
		if (e.getPropertyName().equals(TrainManager.ACTIVE_TRAIN_SCHEDULE_ID))
			updateTitle();
		if (e.getPropertyName().equals(Location.STATUS_CHANGED_PROPERTY)
				|| e.getPropertyName().equals(Location.SWITCHLIST_CHANGED_PROPERTY))
			updateSwitchListButton();
		if (e.getPropertyName().equals(Setup.MANIFEST_CSV_PROPERTY_CHANGE))
			updateRunAndOpenButtons();
		if (e.getPropertyName().equals(TrainManager.LISTLENGTH_CHANGED_PROPERTY)) {
			numTrains.setText(Integer.toString(trainManager.getNumEntries()));
		}
	}

	static Logger log = LoggerFactory.getLogger(TrainsTableFrame.class.getName());
}