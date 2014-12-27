package mavsClimbers;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class MavsClimbersGUI {
	private Hungarian processor;
	private JFrame startWindow;
	private JPanel buttonPanel;
	private JButton loadInValues;
	private JButton enterValues;
	
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
					launchManualSecondWindow();
				} else {
					errorLabel.setText("Please enter integer values");
				}
			}
			
			private boolean inputValuesAreAppropriate() {
				return false;
			}
		}
		
		nextButton.addActionListener(new NextButtonListener());
		
		manualFirstWindow.setVisible(true);
	}
	
	private void launchManualSecondWindow() {
		
	}
	
	private void loadInValuesFromExcel() {
		
	}
	
}
