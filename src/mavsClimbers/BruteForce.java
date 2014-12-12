package mavsClimbers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class pairs teachers with their award-winners by assigning each student a 
 * "ranking" based on if they are the first choice of the teacher (1 point), second
 * choice of the teacher (2 points), and so on. Then, this class does the brute force
 * method of creating an array list with all possible combination-sets of teachers
 * and students, excluding sets in which more than one teacher are assigned the same
 * student. (No student may win more than 1 award.) Finally, this class evaluates
 * all of the sets of possible award configurations by adding up all of the points
 * and determining which set has the minimum total sum.
 * 
 * @author Anders Schneider
 *
 */

public class BruteForce implements MavClimberFinder {
	
	public Teacher[] teacherList;
	private ArrayList<HashMap> finalAssignments;
	private int numNoms;
	private ArrayList<HashMap> allTrials;
	private HashMap trial;
	private int scoreToBeat;
	
	public static void main(String[] args) {
		BruteForce bruteForce = new BruteForce();
		bruteForce.readInNoms();
		bruteForce.runTrials();
		bruteForce.reportResults();
    }

	@Override
	public void readInNoms() {
		teacherList = new Teacher[3];
		Teacher schneider = new Teacher("Schneider", "Physics");
		Student a = new Student("A", schneider, 1);
		Student b = new Student("B", schneider, 2);
		Student c = new Student("C", schneider, 3);
		Student[] sMavNoms = {a, b, c};
		schneider.addNoms(sMavNoms);
		Teacher yu = new Teacher("Yu", "Algebra");
		Student d = new Student("A", yu, 1);
		Student e = new Student("C", yu, 2);
		Student f = new Student("B", yu, 3);
		Student[] yMavNoms = {d, e, f};
		yu.addNoms(yMavNoms);
		Teacher rodd = new Teacher("Rodd", "English");
		Student g = new Student("C", rodd, 1);
		Student h = new Student("A", rodd, 2);
		Student i = new Student("B", rodd, 3);
		Student[] rMavNoms = {g, h, i};
		rodd.addNoms(rMavNoms);
		teacherList[0] = schneider;
		teacherList[1] = yu;
		teacherList[2] = rodd;
		numNoms = 3;
	}

	@Override
	public void setTeacherList(Teacher[] teacherList) {
		this.teacherList = teacherList;
	}
	
	@Override
	public void runTrials() {
		allTrials = new ArrayList<HashMap>();
		trial = new HashMap();
		
		//long startTime = System.currentTimeMillis();
		populateTrialHashMaps();
		//long midTime = System.currentTimeMillis();
		//System.out.println("\n" + ((midTime - startTime)/1000.) + " seconds to populate trial hashmaps");
		findBestAssignmentArray();
		//long endTime = System.currentTimeMillis();
		//System.out.println("" + ((endTime - midTime)/1000.) + " seconds to find best assignment array");
	}

	private void populateTrialHashMaps() {
		allTrials.clear();
		
		Teacher teacher = teacherList[0];
		numNoms = teacher.howManyNoms();
		
		for (int i = 0; i < numNoms; i++) {
			trial.clear();
			Student mavNom = teacher.getMavNom(i);
			trial.put(teacher, mavNom);
			addRestOfTeacherNoms(1, teacher);
		}
	}
	
	private void addRestOfTeacherNoms(int indexNextTeacher, Teacher lastTeacher) {
		if (indexNextTeacher >= teacherList.length) {
			allTrials.add((HashMap) trial.clone());
			trial.remove(lastTeacher);
			return;
		}
		
		Teacher teacher = teacherList[indexNextTeacher];
		for (int i = 0; i < numNoms; i++) {
			Student mavNom = teacher.getMavNom(i);
			if (!(trial.containsValue(mavNom))) {
				trial.put(teacher, mavNom);
				addRestOfTeacherNoms(indexNextTeacher + 1, teacher);
			}
		}
		trial.remove(teacher);
	}
		
	private void findBestAssignmentArray() {
		scoreToBeat = 10000000;
		finalAssignments = new ArrayList<HashMap>();
		int numTeachers = teacherList.length;
		int numTrials = allTrials.size();
				
		//System.out.println("" + numTrials + " candidates");
		
		for (int i = 0; i < numTrials; i++) {
			HashMap trialToEval = allTrials.get(i);
			int trialSum = 0;
			for (int j = 0; j < numTeachers; j++) {
				Teacher teacher = teacherList[j];
				Student student = (Student) trialToEval.get(teacher);
				trialSum = trialSum + student.getScore(teacher);
			}
			
			//System.out.println(trialToEval.entrySet());
			//System.out.println(trialSum + "\n");
			
			if (trialSum == scoreToBeat) {
				finalAssignments.add(trialToEval);
			}
			
			if (trialSum < scoreToBeat) {
				scoreToBeat = trialSum;
				finalAssignments.clear();
				finalAssignments.add(trialToEval);
			}
		}
	}
		
	public void reportResults() {
		if (finalAssignments.isEmpty()){
			System.out.println("No possible solutions.");
		} else {
			System.out.println("" + finalAssignments.size() + " best solutions.");
			for (int i = 0; i < finalAssignments.size(); i++) {
				System.out.println(finalAssignments.get(i));
			}
		}
	}
	
	@Override
	public ArrayList<HashMap> returnResults() {
		return finalAssignments;
	}
}