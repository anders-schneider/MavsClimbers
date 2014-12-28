package mavsClimbers;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.*;

public class MavsClimbersGUI {
	private Hungarian processor;
	private JFrame startWindow;
	private JPanel buttonPanel;
	private JButton loadInValues;
	private JButton enterValues;
	private int numAwards;
	private int numNoms;
	private Teacher[] teacherList;
	private ArrayList<HashMap> finalAssignments;
	
	public static void main(String[] args) {
		new MavsClimbersGUI().launchStartWindow();
	}
	
	private void launchStartWindow() {
		processor = new Hungarian();
		
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
					teacherList = new Teacher [numAwards];
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
		
		specsPanel.setLayout(new GridLayout(4, 2, 40, 40));
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
				if (inputValuesAreAppropriate()) {
					nomEntryWindow.dispose();
					saveAwardData();
					if (awardNumber < numAwards) {
						newNominationEntry(awardNumber + 1);
					} else {
						determineOptimalDistribution();
					}
				} else {
					errorReport.setText("You have not filled in all the required fields.");
				}
			}
			
			private boolean inputValuesAreAppropriate() {
				String className = classNameField.getText().trim();
				String teacherName = teacherNameField.getText().trim();
			
				if ("".equals(className) || "".equals(teacherName)) {
					return false;
				}
				
				return true;
			}
			
			private void saveAwardData() {
				String className = classNameField.getText().trim();
				String awardName = awardNameField.getText().trim();
				String teacherName = teacherNameField.getText().trim();
				
				Teacher newTeacher = new Teacher(teacherName, className);
				
				Student[] studentList = new Student[numNoms];
				
				for (int i = 0; i < numNoms; i++) {
					String studentName = nomFieldList[i].getText();
					Student newStudent = new Student(studentName);
					studentList[i] = newStudent;
				}
				
				newTeacher.addNoms(studentList);
				
				teacherList[awardNumber - 1] = newTeacher;
			}
		});
		
		nomEntryWindow.add(specsPanel, BorderLayout.WEST);
		nomEntryWindow.add(nomsPanel, BorderLayout.EAST);
		
		nomEntryWindow.setVisible(true);
	}
	
	private void determineOptimalDistribution() {
		processor.setTeacherList(teacherList);
		processor.runTrials();
		finalAssignments = processor.returnResults();
		displayResults();
	}
	
	private void displayResults() {
		JFrame resultsWindow = new JFrame("Optimal Results");
		resultsWindow.setSize(400, 400);
		resultsWindow.setDefaultCloseOperation(resultsWindow.EXIT_ON_CLOSE);
		
		resultsWindow.setLayout(new GridLayout(numAwards, 2, 20, 20));
		
		HashMap results = finalAssignments.get(0);
		
		for(int i = 0; i < numAwards; i++) {
			Teacher teacher = teacherList[i];
			JLabel teacherLabel = new JLabel(teacher.toString() + " (" + teacher.getCourseName() + "):");
			resultsWindow.add(teacherLabel);
			Student student = (Student) results.get(teacher);
			JLabel studentLabel = new JLabel(student.toString());
			resultsWindow.add(studentLabel);
		}
		
		resultsWindow.setVisible(true);
	}
	
	private void loadInValuesFromExcel() {
		
	}
	
}
