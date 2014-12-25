package mavsClimbers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This speed assessment tests the existing methods for determining the best
 * teacher-student pairs by giving them a certain number of trials of varying sizes and
 * measuring their completion time. At the end, the results are regenerated so that they 
 * may be compared for any discrepancies. Should there exist any discrepancies between
 * different methods, they are reported in depth.
 * 
 * In the future, this class may also include the ability to view a histogram of
 * completion times, to give users a sense of the spread in completion times for different
 * sized inputs.
 * 
 * @author Anders Schneider
 *
 */

public class SpeedAssessment {

	private int numTrials = 2;
	private int numNoms = 3;
	private int[][] inputSizes = {{3, 3}};
	//private int[][] inputSizes = {{15, 30}};
	//private int[][] inputSizes = {{15, 30}, {14, 28}, {13, 26}, {12, 24}, {11, 22}, {10, 20}, {9, 18}, {8, 16}, {7, 14}, {6, 12}, {5, 10}, {4, 8}, {3, 6}, {2, 4}};
	//private int[][] inputSizes = {{2, 4}, {3, 6}, {4, 8}, {5, 10}, {6, 12}, {7, 14}, {8, 16}, {9, 18}, {10, 20}, {11, 22}, {12, 24}, {13, 26}, {14, 28}, {15, 30}};
	//private int[][] inputSizes = {{10, 20}, {20, 40}, {30, 60}, {40, 80}, {50, 100}, {60, 120}, {70, 140}, {80, 160}, {90, 180}, {100, 200}};
	private int numOfDifferentSizedInputs = inputSizes.length;
	private int maxNumTeachers = inputSizes[numOfDifferentSizedInputs - 1][0];
	private Teacher[][][] samplesList = new Teacher [numOfDifferentSizedInputs][numTrials][maxNumTeachers];
	private int lengthTeacherName = 5;
	private int lengthTeacherSubject = 7;
	private int lengthStudentID = 8;
	
	public static void main(String[] args) {
		SpeedAssessment speedTester = new SpeedAssessment();
		speedTester.generateRandomSamples();
		speedTester.runTrials();
		speedTester.checkForDiscrepancies();
	}
	
	private void generateRandomSamples() {
		for (int i = 0; i < numOfDifferentSizedInputs; i++) {
			int numTeachers = inputSizes[i][0];
			int numStudents = inputSizes[i][1];
			populateSchoolSamplesList(i, numStudents, numTeachers);
		}
	}
	
	private void populateSchoolSamplesList(int samplesListIndex, int numStudents, int numTeachers) {
		
		for (int k = 0; k < numTrials; k++) {
			Teacher[] teacherList = generateRandomTeacherList(numTeachers);
			Student[] studentPool = generateRandomStudentPool(numStudents);
			for (int i = 0; i < numTeachers; i++) {
				Teacher teacher = teacherList[i];
				Student[] teacherNomList = new Student[numNoms];
				Student[] studentPoolCopy = studentPool.clone();
				for (int j = 0; j < numNoms; j++) {
					Student student;
					int studentIndex;
					do {
						studentIndex = new Random().nextInt(numStudents);
						student = studentPoolCopy[studentIndex];
					} while (student == null);
					student.addAsMavNom(teacher, j);
					studentPoolCopy[studentIndex] = null;
					teacherNomList[j] = student;
				}
				teacher.addNoms(teacherNomList);
			}
			samplesList[samplesListIndex][k] = teacherList;
		}
	}
	
	private Student[] generateRandomStudentPool(int numStudents) {
		Student[] studentPool = new Student[numStudents];
		
		for (int i = 0; i < numStudents; i++) {
			String studentID = generateRandomStudentID();
			Student student = new Student(studentID);
			studentPool[i] = student;
		}
		return studentPool;
	}
	
	private String generateRandomStudentID() {
		String studentID = "";
		for (int j = 0; j < lengthStudentID; j ++) {
			studentID += Integer.toString(new Random().nextInt(10));
		}
		return studentID;
	}
	
	private Teacher[] generateRandomTeacherList(int numTeachers) {
		Teacher[] teacherList = new Teacher[numTeachers];
		
		for (int i = 0; i < numTeachers; i++) {
			String teacherName = generateRandomTeacherName();
			String teacherSubject = generateRandomTeacherSubject();
			Teacher teacher = new Teacher(teacherName, teacherSubject);
			teacherList[i] = teacher;
		}
		return teacherList;
	}
	
	private String generateRandomTeacherName() {
		String teacherName = "";
		int asciiForUpperA = 65;
		for (int j = 0; j < lengthTeacherName; j++) {
			teacherName += Character.toString((char) (asciiForUpperA + new Random().nextInt(26)));
		}
		return teacherName;
	}
	
	private String generateRandomTeacherSubject() {
		String teacherSubject = "";
		int asciiForLowerA = 97;
		for (int j = 0; j < lengthTeacherSubject; j++) {
			teacherSubject += Character.toString((char) (asciiForLowerA + new Random().nextInt(26)));
		}
		return teacherSubject;
	}
	
	private void runTrials() {
		
		System.out.println("Running trials");
		
		BruteForce bruteForce = new BruteForce();
		BruteForceModified bruteForceModified = new BruteForceModified();
		//BruteForceModified2 bruteForceModified2 = new BruteForceModified2();
		BruteForceUpfrontWork bruteForceUpfrontWork = new BruteForceUpfrontWork();
		Hungarian hungarian = new Hungarian();
		
		for (int k = 0; k < numOfDifferentSizedInputs; k++) {
			System.out.println("\n\nBRUTE FORCE");
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < numTrials; i++) {
				bruteForce.setTeacherList(samplesList[k][i]);
				bruteForce.runTrials();
				//bruteForce.reportResults();
			}
			long endTime = System.currentTimeMillis();
			System.out.println("" + ((endTime - startTime) / 1000.) + " seconds for " + numTrials + " trials with " + inputSizes[k][0] + " teachers and " + inputSizes[k][1] + " students");
			
			System.out.println("\nBRUTE FORCE MODIFIED");
			startTime = System.currentTimeMillis();
			for (int i = 0; i < numTrials; i++) {
				bruteForceModified.setTeacherList(samplesList[k][i]);
				bruteForceModified.runTrials();
				//bruteForceModified.reportResults();
			}
			endTime = System.currentTimeMillis();
			System.out.println("" + ((endTime - startTime) / 1000.) + " seconds for " + numTrials + " trials with " + inputSizes[k][0] + " teachers and " + inputSizes[k][1] + " students");
			
//			System.out.println("\nBRUTE FORCE MODIFIED 2");
//			startTime = System.currentTimeMillis();
//			for (int i = 0; i < numTrials; i++) {
//				bruteForceModified2.setTeacherList(samplesList[k][i]);
//				bruteForceModified2.runTrials();
//				//bruteForceModified.reportResults();
//			}
//			endTime = System.currentTimeMillis();
//			System.out.println("" + ((endTime - startTime) / 1000.) + " seconds for " + numTrials + " trials with " + inputSizes[k][0] + " teachers and " + inputSizes[k][1] + " students\n\n");
			
			System.out.println("\nBRUTE FORCE UPFRONT WORK");
			startTime = System.currentTimeMillis();
			for (int i = 0; i < numTrials; i++) {
				bruteForceUpfrontWork.setTeacherList(samplesList[k][i]);
				bruteForceUpfrontWork.runTrials();
				//bruteForceModified.reportResults();
			}
			endTime = System.currentTimeMillis();
			System.out.println("" + ((endTime - startTime) / 1000.) + " seconds for " + numTrials + " trials with " + inputSizes[k][0] + " teachers and " + inputSizes[k][1] + " students");
			
			System.out.println("\nHUNGARIAN ALGORITHM");
			startTime = System.currentTimeMillis();
			for (int i = 0; i < numTrials; i++) {
				hungarian.setTeacherList(samplesList[k][i]);
				hungarian.runTrials();
				//hungarian.reportResults();
			}
			endTime = System.currentTimeMillis();
			System.out.println("" + ((endTime - startTime) / 1000.) + " seconds for " + numTrials + " trials with " + inputSizes[k][0] + " teachers and " + inputSizes[k][1] + " students");
			
		}
	}
	
	private void checkForDiscrepancies() {
		
		System.out.println("\nChecking for discrepancies");
		
		BruteForce bruteForce = new BruteForce();
		BruteForceModified bruteForceModified = new BruteForceModified();
		//BruteForceModified2 bruteForceModified2 = new BruteForceModified2();
		BruteForceUpfrontWork bruteForceUpfrontWork = new BruteForceUpfrontWork();
		Hungarian hungarian = new Hungarian();
		
		int discrepancyCounter = 0;
		
		for (int k = 0; k < numOfDifferentSizedInputs; k++) {
			for (int i = 0; i < numTrials; i++) {
				Teacher[] sampleToRun = samplesList[k][i];
				
				//System.out.println("\n\n" + sampleToRun.length + " teachers");
				
				bruteForce.setTeacherList(sampleToRun);
				bruteForceModified.setTeacherList(sampleToRun);
				//bruteForceModified2.setTeacherList(sampleToRun);
				bruteForceUpfrontWork.setTeacherList(sampleToRun);
				hungarian.setTeacherList(sampleToRun);
				
				long startTime = System.currentTimeMillis();
				bruteForce.runTrials();
				ArrayList<HashMap> bruteForceResults = bruteForce.returnResults();
				long stopTime = System.currentTimeMillis();
				//System.out.println("Brute Force took " + (stopTime - startTime) + " msec");
				
				startTime = System.currentTimeMillis();
				bruteForceModified.runTrials();
				ArrayList<HashMap> bruteForceModifiedResults = bruteForceModified.returnResults();
				stopTime = System.currentTimeMillis();
				//System.out.println("Brute Force Modified took " + (stopTime - startTime) + " msec");
				
//				startTime = System.currentTimeMillis();
//				bruteForceModified2.runTrials();
//				ArrayList<HashMap> bruteForceModified2Results = bruteForceModified2.returnResults();
//				stopTime = System.currentTimeMillis();
//				//System.out.println("Brute Force Modified 2 took " + (stopTime - startTime) + " msec");
				
				startTime = System.currentTimeMillis();
				bruteForceUpfrontWork.runTrials();
				ArrayList<HashMap> bruteForceUpfrontWorkResults = bruteForceUpfrontWork.returnResults();
				stopTime = System.currentTimeMillis();
				//System.out.println("Brute Force Upfront Work took " + (stopTime - startTime) + " msec");

				startTime = System.currentTimeMillis();
				hungarian.runTrials();
				ArrayList<HashMap> hungarianResults = hungarian.returnResults();
				stopTime = System.currentTimeMillis();
				//System.out.println("Hungarian Algorithm took " + (stopTime - startTime) + " msec");				
				
				if (!(bruteForceResults.equals(bruteForceModifiedResults)) || !(bruteForceResults.equals(bruteForceUpfrontWorkResults)) || !(bruteForceResults.contains(hungarianResults.get(0)))) {
					discrepancyCounter++;
					System.out.println("DISCREPANCY");
					for (int j = 0; j < sampleToRun.length; j++) {
						System.out.println(sampleToRun[j]);
						Student[] mavNoms = sampleToRun[j].getMavNomArray(); 
						for (int index = 0; index < numNoms; index++) {
							System.out.println(mavNoms[index]);
						}
					}
					System.out.println("Brute Force Results:");
					System.out.println(bruteForceResults);
					System.out.println("Brute Force Modified Results:");
					System.out.println(bruteForceModifiedResults);
//					System.out.println("Brute Force Modified 2 Results:");
//					System.out.println(bruteForceModified2Results);
					System.out.println("Brute Force Upfront Work Results:");
					System.out.println(bruteForceUpfrontWorkResults);
					System.out.println("Hungarian Algorithm Results:");
					System.out.println(hungarianResults);
					System.out.println("\n");
				}
			}
		}
		System.out.println("\n" + discrepancyCounter + " discrepancies");
	}
	
	private void reportResults() {
		
	}

}
