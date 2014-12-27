package mavsClimbers;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

public class MavsClimbersGUI {
	private Hungarian processor;
	private JFrame startWindow;
	private JPanel buttonPanel;
	private JButton loadInValues;
	private JButton enterValues;
	private int numAwards;
	private int numNoms;
	
	public static void main(String[] args) {
		new MavsClimbersGUI().run();
	}

	private void run() {
		processor = new Hungarian();
		
		launchStartWindow();
	}
	
	private void launchStartWindow() {
		startWindow = new JFrame("Start");
		startWindow.setSize(450, 150);
		startWindow.setLayout(new BorderLayout());
		startWindow.setDefaultCloseOperation(startWindow.EXIT_ON_CLOSE);
		
		JLabel presentOptions = new JLabel("What would you like to do?");
		startWindow.add(presentOptions, BorderLayout.CENTER);
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		loadInValues = new JButton("Load in nominations from Excel");
		enterValues = new JButton("Enter nominations manually");
		buttonPanel.add(loadInValues);
		buttonPanel.add(enterValues);
		startWindow.add(buttonPanel, BorderLayout.SOUTH);
		
		enterValues.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				enterValuesManually();
			}
		});
		
		loadInValues.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadInValuesFromExcel();
			}
		});
		
		startWindow.setVisible(true);
	}
	
	private void enterValuesManually() {
		startWindow.dispose();
		launchManualFirstWindow();
	}
	
	private void launchManualFirstWindow() {
		JFrame manualFirstWindow = new JFrame();
		manualFirstWindow.setSize(500, 200);
		manualFirstWindow.setLayout(new GridLayout(3, 2, 40, 40));
		manualFirstWindow.setDefaultCloseOperation(manualFirstWindow.EXIT_ON_CLOSE);
		
		JLabel howManyAwards = new JLabel("How many awards?");
		JLabel howManyNoms = new JLabel("How many nominations per award?");
		
		JTextField numAwardsField = new JTextField();
		JTextField numNomsField = new JTextField();
		
		JButton nextButton = new JButton("Next");
		
		JLabel errorLabel = new JLabel("");
		
		manualFirstWindow.add(howManyAwards);
		manualFirstWindow.add(numAwardsField);
		manualFirstWindow.add(howManyNoms);
		manualFirstWindow.add(numNomsField);
		manualFirstWindow.add(errorLabel);
		manualFirstWindow.add(nextButton);
		
		class NextButtonListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (inputValuesAreAppropriate()) {
					manualFirstWindow.dispose();
					startNominationEntry();
				} else {
					errorLabel.setText("Please enter positive integer values");
				}
			}
			
			private boolean inputValuesAreAppropriate() {
				String numAwardsString = numAwardsField.getText();
				String numNomsString = numNomsField.getText();
				
				try {
					numAwards = Integer.parseInt(numAwardsString);
					numNoms = Integer.parseInt(numNomsString);
				} catch (NumberFormatException e) {
					return false;
				}
				
				if ((numAwards < 1) || (numNoms < 1)) {
					return false;
				}
				return true;
			}
		}
		
		nextButton.addActionListener(new NextButtonListener());
		
		manualFirstWindow.setVisible(true);
	}
	
	private void startNominationEntry() {
		
		newNominationEntry(1);
	
	}
	
	private void newNominationEntry(int awardNumber) {
		JFrame nomEntryWindow = new JFrame("Award #" + awardNumber);
		nomEntryWindow.setSize(600, 300);
		nomEntryWindow.setLayout(new BorderLayout());
		
		JPanel specsPanel = new JPanel();
		JPanel nomsPanel = new JPanel();
		
		JLabel classNameLabel = new JLabel("Class Name:");
		JLabel teacherNameLabel = new JLabel("Teacher Name:");
		JLabel awardNameLabel = new JLabel("Award Name (optional):");
		
		JLabel errorReport = new JLabel("");
		
		JTextField classNameField = new JTextField("Physics");
		JTextField teacherNameField = new JTextField("Schneider");
		JTextField awardNameField = new JTextField("Maverick");
		
		specsPanel.setLayout(new GridLayout(4, 2, 50, 70));
		specsPanel.add(classNameLabel);
		specsPanel.add(classNameField);
		specsPanel.add(teacherNameLabel);
		specsPanel.add(teacherNameField);
		specsPanel.add(awardNameLabel);
		specsPanel.add(awardNameField);
		specsPanel.add(new JPanel());
		specsPanel.add(errorReport);
		
		JTextField [] nomFieldList = new JTextField [numNoms];
		JButton backButton = new JButton("Back");
		JButton nextButton = new JButton("Next");
		
		nomsPanel.setLayout(new GridLayout(numNoms + 1, 2, 20, 20));
		
		for (int j = 0; j < numNoms; j++) {
			nomsPanel.add(new JLabel("Nomination #" + (j + 1)));
			JTextField nomsField = new JTextField("Student X");
			nomFieldList[j] = nomsField;
			nomsPanel.add(nomsField);
		}
		
		nomsPanel.add(backButton);
		nomsPanel.add(nextButton);
		
		backButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Go back, populate fields with what they had when last saved
				// When they click next again, remove the old teacher/class
				// from the list and add the new one with the new details
			}
		});
		
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (inputValuesAreAppropriate() /* && input values are not already in list */) {
					nomEntryWindow.dispose();
					newNominationEntry(awardNumber + 1);
				}
			}
			
			private boolean inputValuesAreAppropriate() {
				String className = classNameField.getText();
				String teacherName = teacherNameField.getText();
			
				if ("".equals(className.trim()) || "".equals(teacherName.trim())) {
					return false;
				}
				return true;
			}
		});
		
		nomEntryWindow.add(specsPanel, BorderLayout.WEST);
		nomEntryWindow.add(nomsPanel, BorderLayout.EAST);
		
		nomEntryWindow.setVisible(true);
	}
	
	private void loadInValuesFromExcel() {
		
	}
	
}
