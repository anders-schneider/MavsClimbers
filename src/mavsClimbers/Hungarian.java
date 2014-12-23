package mavsClimbers;

import java.util.ArrayList;
import java.util.HashMap;

public class Hungarian implements MavClimberFinder {

	public Teacher[] teacherList;
	ArrayList<Student> studentList;
	private ArrayList<HashMap> finalAssignments;
	private int numNoms;
	private HashMap trial;
	private int scoreToBeat;
	private int numStudents;
	private int[][] matrix;
	
	public static void main(String[] args) {
		Hungarian hungarian = new Hungarian();
		hungarian.readInNoms();
		hungarian.runTrials();
		hungarian.reportResults();
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
		
		finalAssignments = new ArrayList<HashMap>();
		
		numNoms = teacherList[0].howManyNoms();
		
		createStudentList();
		matrix = new int[numStudents][numStudents];
		populateMatrix();
		
		for (int row = 0; row < numStudents; row++) {
			for (int col = 0; col < numStudents; col++) {
				System.out.print(matrix[row][col] + " ");
			}
			System.out.println("");
		}
		System.out.println("");
		
		subtractMinFrom("row");
		subtractMinFrom("column");
		
		for (int row = 0; row < numStudents; row++) {
			for (int col = 0; col < numStudents; col++) {
				System.out.print(matrix[row][col] + " ");
			}
			System.out.println("");
		}
	}
				
	private void createStudentList() {
		studentList = new ArrayList<Student>();
		for (int i = 0; i < teacherList.length; i++) {
			Teacher teacher = teacherList[i];
			for (int j = 0; j < numNoms; j++) {
				Student student = teacher.getMavNom(j);
				if (!(studentList.contains(student))) {
					studentList.add(student);
					numStudents++;
				}
			}
		}
	}
	
	private void populateMatrix() {
		for (int teacherIndex = 0; teacherIndex < teacherList.length; teacherIndex++) {
			Teacher teacher = teacherList[teacherIndex];
			for (int j = 0; j < numNoms; j++) {
				Student student = teacher.getMavNom(j);
				int studentIndex = studentList.indexOf(student);
				matrix[studentIndex][teacherIndex] = j + 1;
			}
		}
		
		for (int row = 0; row < numStudents; row++) {
			for (int col = 0; col < numStudents; col++) {
				if (matrix[row][col] == 0) {
					matrix[row][col] = numNoms + 10;
				}
			}
		}
	}
	
	private void subtractMinFrom(String unit) {
		if ("row".equals(unit)) {
			for (int row = 0; row < numStudents; row++) {
				int min = findMin(matrix[row]);
				for (int col = 0; col < numStudents; col++) {
					matrix[row][col] -= min;
				}
			}
		} else if ("column".equals(unit)) {
			for (int col = 0; col < numStudents; col++) {
				int[] column = new int[numStudents];
				for (int row = 0; row < numStudents; row++) {
					column[row] = matrix[row][col];
				}
				int min = findMin(column);
				for (int row = 0; row < numStudents; row++) {
					matrix[row][col] -= min;
				}
			}
		} else if ("all".equals(unit)) {
			int min = findMinOfMatrix();
			for (int row = 0; row < numStudents; row++) {
				for (int col = 0; col < numStudents; col++) {
					matrix[row][col] -= min;
				}
			}
		}
	}


	private int findMin(int[] group) {
		int min = group[0];
		for (int i = 1; i < numStudents; i++) {
			if (group[i] < min) {
				min = group[i];
			}
		}
		return min;
	}
	
	private int findMinOfMatrix() {
		int min = matrix[0][0];
		for (int i = 0; i < numStudents; i++) {
			for (int j = 0; j < numStudents; j++) {
				if (matrix[i][j] < min) {
					min = matrix[i][j];
				}
			}
		}
		return min;
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
