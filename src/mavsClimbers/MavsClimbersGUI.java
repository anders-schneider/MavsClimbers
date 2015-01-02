package mavsClimbers;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
		loadInValues = new JButton("Load in nominations from text file");
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
				loadInValuesFromTextFile();
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
					newNominationEntry(1);
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
		System.out.println(finalAssignments);
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
	
	private void loadInValuesFromTextFile() {
		startWindow.dispose();
		launchFormatWarningWindow();
	}
	
	private void launchFormatWarningWindow() {
		JFrame formatWarningWindow = new JFrame("Warning!");
		formatWarningWindow.setSize(300, 250);
		
		formatWarningWindow.setLayout(new BorderLayout());
		
		JButton continueButton = new JButton("Continue");
		JPanel formatExamplePanel = new JPanel();
		formatExamplePanel.setLayout(new GridLayout(10, 1));
		formatExamplePanel.add(new JLabel(""));
		formatExamplePanel.add(new JLabel("Teacher 1 - Class Name"));
		formatExamplePanel.add(new JLabel("Nomination #1"));
		formatExamplePanel.add(new JLabel("Nomination #2"));
		formatExamplePanel.add(new JLabel("     ...     "));
		formatExamplePanel.add(new JLabel("Nomination #n"));
		formatExamplePanel.add(new JLabel(""));
		formatExamplePanel.add(new JLabel("Teacher 2 - Class Name"));
		formatExamplePanel.add(new JLabel("Nomination #1"));
		formatExamplePanel.add(new JLabel("     ...     "));
		
		formatWarningWindow.add(new JLabel("Text files must have the following format:"), BorderLayout.NORTH);
		formatWarningWindow.add(formatExamplePanel, BorderLayout.CENTER);
		formatWarningWindow.add(continueButton, BorderLayout.SOUTH);
		
		continueButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				formatWarningWindow.dispose();
				promptUserForTextFile();
			}
		});
		
		formatWarningWindow.setVisible(true);
	}
	
	private void promptUserForTextFile() {
		ArrayList<String> fileLines = null;
		
		try {
			fileLines = loadTextFile();
			if (fileLines == null) {
				throw new IOException("file was null");
			}
			parseFileContents(fileLines);
			determineOptimalDistribution();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Oops! The following error ocurred: " + e.getMessage());
			//promptUserForTextFile();
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(null, "Oops! The following error ocurred: " + e.getMessage());
		}
	}
	
	private void parseFileContents(ArrayList<String> fileLines) {

		findNumNoms(fileLines);
		int teacherIndex = 0;
		ArrayList<Teacher> teacherHolder = new ArrayList<Teacher>();
		
		for (int i = 0; i < fileLines.size(); i++) {
			String teacherClassLine = fileLines.get(i);
			i++;
			
			String[] contents = teacherClassLine.split(" - ");
			Teacher newTeacher = new Teacher(contents[0].trim(), contents[1].trim());
			
			int limit = i + numNoms;
			
			String line;
			Student[] mavNoms = new Student[numNoms];
			
			for (; i < limit; i++) {
				line = fileLines.get(i).trim();
				if ("".equals(line)) {
					throw new IllegalArgumentException("Encountered unexpected blank line");
				}
				Student newStudent = new Student(line);
				mavNoms[numNoms - (limit - i)] = newStudent;
			}
			
			newTeacher.addNoms(mavNoms);
			teacherHolder.add(newTeacher);
			teacherIndex++;
		}
		
		numAwards = teacherIndex;
		
		teacherList = new Teacher[teacherIndex];
		for (int j = 0; j < teacherList.length; j++) {
			teacherList[j] = teacherHolder.get(j);
		}
	}
	
	private void findNumNoms(ArrayList<String> fileLines) {
		int j = 1;
		while(!("".equals(fileLines.get(j).trim()))) {j++;}
		numNoms = j - 1;
	}
	
	private ArrayList<String> loadTextFile() throws IOException {
		ArrayList<String> lines = null;
        BufferedReader reader;
        String fileName;

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load which file?");
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file != null) {
                fileName = file.getCanonicalPath();
                reader = new BufferedReader(new FileReader(fileName));
                lines = new ArrayList<String>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();
                return lines;
            }
        }
        return lines;
	}
	
}
