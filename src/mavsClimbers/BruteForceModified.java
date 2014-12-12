package mavsClimbers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is very similar to the Brute Force class, except that it is more efficient
 * with memory. Instead of creating all of the teacher-student pair sets in one step and
 * then evaluating them in another step, it creates a set and immediately evaluates it.
 * 
 * @author Anders Schneider
 *
 */

public class BruteForceModified implements MavClimberFinder {
	
	public Teacher[] teacherList;
	private ArrayList<HashMap> finalAssignments;
	private int numNoms;
	private HashMap trial;
	private int scoreToBeat;
	
	public static void main(String[] args) {
		BruteForceModified bruteForceModified = new BruteForceModified();
		bruteForceModified.readInNoms();
		bruteForceModified.runTrials();
		bruteForceModified.reportResults();
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
		trial = new HashMap();
		scoreToBeat = 100000;
		finalAssignments = new ArrayList<HashMap>();
		
		Teacher teacher = teacherList[0];
		numNoms = teacher.howManyNoms();
		
		for (int i = 0; i < numNoms; i++) {
			Student mavNom = teacher.getMavNom(i);
			trial.put(teacher, mavNom);
			addRestOfTeacherNoms(1, teacher);
		}
	}
	
	private void addRestOfTeacherNoms(int indexNextTeacher, Teacher lastTeacher) {
		if (indexNextTeacher >= teacherList.length) {
			evaluateTrial();
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
		
	private void evaluateTrial() {

		int numTeachers = teacherList.length;
		
		//System.out.println(trial.entrySet());
		
		int trialSum = 0;
		for (int j = 0; j < numTeachers; j++) {
			Teacher teacher = teacherList[j];
			Student student = (Student) trial.get(teacher);
			trialSum = trialSum + student.getScore(teacher);
		}
		
		//System.out.println(trialSum);
		
		if (trialSum == scoreToBeat) {
			finalAssignments.add((HashMap) trial.clone());
		}
		
		if (trialSum < scoreToBeat) {
			scoreToBeat = trialSum;
			finalAssignments.clear();
			finalAssignments.add((HashMap) trial.clone());
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