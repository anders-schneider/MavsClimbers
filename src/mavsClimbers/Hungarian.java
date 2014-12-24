package mavsClimbers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Hungarian implements MavClimberFinder {

	public Teacher[] teacherList;
	ArrayList<Student> studentList;
	private ArrayList<HashMap> finalAssignments;
	private int numNoms;
	private HashMap trial;
	private int scoreToBeat;
	private int numStudents;
	private int numTeachers;
	private int[][] matrix;
	private int[][] adjacencyMatrix;
	private int[][] flowNetwork;
	private int[][] residualGraph;
	private boolean augPathExists;
	private boolean isAugPath;
	ArrayList<Integer> currentPath = new ArrayList<Integer>();
	boolean [] visited;
	
	public static void main(String[] args) {
		Hungarian hungarian = new Hungarian();
		hungarian.readInNoms();
		hungarian.runTrials();
		hungarian.reportResults();
    }

	@Override
	public void readInNoms() {
		teacherList = new Teacher[2];
		Teacher schneider = new Teacher("Schneider", "Physics");
		Student a = new Student("A", schneider, 1);
		Student b = new Student("B", schneider, 2);
		Student c = new Student("C", schneider, 3);
		Student[] sMavNoms = {a, b, c};
		schneider.addNoms(sMavNoms);
		Teacher yu = new Teacher("Yu", "Algebra");
		Student d = new Student("C", yu, 1);
		Student e = new Student("A", yu, 2);
		Student f = new Student("B", yu, 3);
		Student[] yMavNoms = {d, e, f};
		yu.addNoms(yMavNoms);
		Teacher rodd = new Teacher("Rodd", "English");
		Student g = new Student("B", rodd, 1);
		Student h = new Student("C", rodd, 2);
		Student i = new Student("A", rodd, 3);
		Student[] rMavNoms = {g, h, i};
		rodd.addNoms(rMavNoms);
		teacherList[0] = schneider;
		teacherList[1] = yu;
		//teacherList[2] = rodd;
		numNoms = 3;
	}
	
	@Override
	public void setTeacherList(Teacher[] teacherList) {
		this.teacherList = teacherList;
	}

	@Override
	public void runTrials() {
		
		finalAssignments = new ArrayList<HashMap>();
		
		numTeachers = teacherList.length;
		numNoms = teacherList[0].howManyNoms();
		
		createStudentList();
		matrix = new int[numStudents][numStudents];
		adjacencyMatrix = new int[2 * numStudents + 2][2 * numStudents + 2];
		flowNetwork = new int[2 * numStudents + 2][2 * numStudents + 2];
		visited = new boolean[2 * numStudents + 2];
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
		System.out.println("");
		
		findMaximumMatching();
	}
				
	private void createStudentList() {
		studentList = new ArrayList<Student>();
		for (int i = 0; i < numTeachers; i++) {
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
		for (int teacherIndex = 0; teacherIndex < numTeachers; teacherIndex++) {
			Teacher teacher = teacherList[teacherIndex];
			for (int j = 0; j < numNoms; j++) {
				Student student = teacher.getMavNom(j);
				int studentIndex = studentList.indexOf(student);
				matrix[teacherIndex][studentIndex] = j + 1;
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

	private void findMaximumMatching() {
		clearAdjacencyMatrix();
		populateAdjacencyMatrix();
		
		printAdjacencyMatrix();
				
		residualGraph = adjacencyMatrix;
		
		augPathExists = true;
		int currentNode, nextNode, i;
		
		while (augPathExists) {
			
			/* Best way to set this up would be:
			 * If (just starting)
			 * Else (at teacher level)
			 * Else (at student level)
			 */
			
			if (currentPath.get(currentPath.size() - 1) == 2 * numStudents + 1) {
				setUpForNewPath();
			}
			
			if (currentPath.size() % 2 == 0) {
				
				i = 1;
				while (residualGraph[0][i] == 0 && (i < numStudents + 1)) {i++;}
				
				/* If there are no viable outgoing edges, break out of the loop */
				if (i == numStudents + 1) {
					break;
				}
	
				/* Otherwise, add the found node to the current path */
				currentNode = i;
				currentPath.add(currentNode);
				visited[currentNode] = true;
			} else {
				currentNode = currentPath.get(currentPath.size() - 1);
			}
				
			while (true) {
				nextNode = searchFromTeacherLevel(currentNode);
				
				/* If there are no edges to students, break out of the loop */
				if (nextNode == 2 * numStudents + 1) {
					break;
				}
				
				/* Arrived at the student level */
				currentNode = nextNode;
				currentPath.add(currentNode);
				visited[currentNode] = true;
				
				/* Check if there is a path to the sink */
				if (residualGraph[currentNode][2 * numStudents + 1] == 1) {
					currentPath.add(2 * numStudents + 1);
					visited[2 * numStudents + 1] = true;
					isAugPath = true;
					break;
				}
				
				/* Otherwise go back and search for an edge to a teacher to go back to */
				i = 1;
				while ((i < numStudents + 1) && ((visited[i]) || (residualGraph[currentNode][i] == 0))) {i++;}
				
				/* If no teachers to go back to, break out of the loop */
				if (i == numStudents + 1) {
					break;
				}
				
				/* Found a teacher, at the teacher level */
				currentNode = i;
				currentPath.add(currentNode);
				visited[currentNode] = true;
			}
			
			if (isAugPath) {
				// update Flow network and residual graph
				int pairIndex = 0;
				int firstNode, secondNode;
				while (pairIndex < currentPath.size() - 1) {
					firstNode = currentPath.get(pairIndex);
					secondNode = currentPath.get(pairIndex + 1);
					if (flowNetwork[secondNode][firstNode] == 1) {
						flowNetwork[secondNode][firstNode] = 0;
					} else {
						flowNetwork[firstNode][secondNode] = 1;
					}
					
					residualGraph[firstNode][secondNode] = 0;
					residualGraph[secondNode][firstNode] = 1;
					
					pairIndex++;
				}
			} else {
				int lastNodeVisitedIndex = currentPath.size() - 1;
				int lastNodeVisited = currentPath.get(lastNodeVisitedIndex);
				visited[lastNodeVisited] = true;
				currentPath.remove(lastNodeVisitedIndex);
			}
		}
		printFlowNetwork();
	}
	
	private void setUpForNewPath() {
		currentPath.clear();
		
		isAugPath = false;
		Arrays.fill(visited, false);
		
		/* Start at source, look for any outgoing edge */
		currentPath.add(0);
		visited[0] = true;		
	}

	private int searchFromTeacherLevel(int currentNode) {
		/* At the teacher level, search for an edge to a student */
		int i = numStudents + 1;
		while ((i < 2 * numStudents + 1) && ((visited[i]) || (residualGraph[currentNode][i] == 0))) {i++;}
		
		return i;
	}

	private void printFlowNetwork() {
		for (int i = 0; i < 2 * numStudents + 2; i++) {
			for (int j = 0; j < 2 * numStudents + 2; j ++) {
				System.out.print(flowNetwork[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("");
	}

	private void printAdjacencyMatrix() {
		for (int i = 0; i < 2 * numStudents + 2; i++) {
			for (int j = 0; j < 2 * numStudents + 2; j ++) {
				System.out.print(adjacencyMatrix[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("");
	}

	private void clearAdjacencyMatrix() {
		for (int i = 0; i < 2 * numStudents + 2; i++) {
			Arrays.fill(adjacencyMatrix[i], 0);
		}
	}
	
	private void populateAdjacencyMatrix() {
		int i = 1;
		while(i < numStudents + 1) {
			adjacencyMatrix[0][i] = 1;
			i++;
		}
		
		for (int teacherIndex = 0; teacherIndex < numStudents; teacherIndex++) {
			for (int studentIndex = 0; studentIndex < numStudents; studentIndex++) {
				if (matrix[teacherIndex][studentIndex] == 0) {
					adjacencyMatrix[teacherIndex + 1][studentIndex + numStudents + 1] = 1;
				}
			}
		}
		
		int j = 2 * numStudents + 1;
		i = 1 + numStudents;
		while (i < 2 * numStudents + 1) {
			adjacencyMatrix[i][j] = 1;
			i++;
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
