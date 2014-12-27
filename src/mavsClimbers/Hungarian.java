package mavsClimbers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
	boolean [] visited;
	int currentNode, nextNode;
	String status;
	
	public static void main(String[] args) {
		Hungarian hungarian = new Hungarian();
		hungarian.readInNoms();
		hungarian.runTrials();
		hungarian.reportResults();
	}

	@Override
	
	public void readInNoms() {
		teacherList = new Teacher[6];
		Teacher one = new Teacher("1", "One");
		Teacher two = new Teacher("2", "Two");
		Teacher three = new Teacher("3", "Three");
		Teacher four = new Teacher("4", "Four");
		Teacher five = new Teacher("5", "Five");
		Teacher six = new Teacher("6", "Six");
		Student a = new Student("A");
		Student b = new Student("B");
		Student c = new Student("C");
		Student d = new Student("D");
		Student e = new Student("E");
		Student f = new Student("F");
		Student g = new Student("G");
		Student h = new Student("H");
		Student i = new Student("I");
		Student[] oneMavNoms = {f, b, h};
		Student[] twoMavNoms = {a, b, g};
		Student[] threeMavNoms = {e, c, f};
		Student[] fourMavNoms = {c, f, b};
		Student[] fiveMavNoms = {d, c, i};
		Student[] sixMavNoms = {e, d, h};
		one.addNoms(oneMavNoms);
		two.addNoms(twoMavNoms);
		three.addNoms(threeMavNoms);
		four.addNoms(fourMavNoms);
		five.addNoms(fiveMavNoms);
		six.addNoms(sixMavNoms);
		teacherList[0] = one;
		teacherList[1] = two;
		teacherList[2] = three;
		teacherList[3] = four;
		teacherList[4] = five;
		teacherList[5] = six;
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
		
		//int[][] setMatrix = {{1, 4, 5}, {5, 7, 6}, {5, 8, 8}};
		//matrix = setMatrix;
		
		//printMatrix();
		
		while (true) {
		
			subtractMinFrom("row");
			subtractMinFrom("column");
		
			//printMatrix();
			
			findMaximumMatching();
			
			//printFlowNetwork();
		
			if (isPerfectMatching()) {
				populateFinalAssignments();
				break;
			}
			
			adjustMatrix();
			
			//printMatrix();
		}
	}
				
	private boolean isPerfectMatching() {
		for (int i = 1; i < numStudents + 1; i++) {
			boolean hasEdge = false;
			for (int j = numStudents + 1; j < 2 * numStudents + 1; j++) {
				if (flowNetwork[i][j] == 1) {
					hasEdge = true;
				}
			}
			if (!hasEdge) {
				return false;
			}
		}
		return true;
	}

	private void createStudentList() {
		studentList = new ArrayList<Student>();
		numStudents = 0;
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
		studentList.sort(new Comparator<Student>() {
			@Override
			public int compare(Student first, Student second) {
				return first.toString().compareTo(second.toString());
			}
		});
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

	private void adjustMatrix() {
		ArrayList<Integer> connectedVertices = findConnectedVertices();
		
		int minOfMatrix = findMinOfMatrixNoZeroes();
		
		for (int i = 1; i < numStudents + 1; i++) {
			for (int j = numStudents + 1; j < 2 * numStudents + 1; j++) {
				if (!connectedVertices.contains(i) && !connectedVertices.contains(j)) {
					matrix[i - 1][j - (numStudents + 1)] -= minOfMatrix;
				} else if (connectedVertices.contains(i) && connectedVertices.contains(j)) {
					matrix[i - 1][j - (numStudents + 1)] += minOfMatrix;
				}
			}
		}
	}
	
	private ArrayList<Integer> findConnectedVertices() {
		ArrayList<Integer> connectedVertices = new ArrayList<Integer>();
		
		for (int i = 1; i < numStudents + 1; i++) {
			for (int j = numStudents + 1; j < 2 * numStudents + 1; j++) {
				if (flowNetwork[i][j] == 1) {
					connectedVertices.add(i);
					connectedVertices.add(j);
					break;
				}
			}
		}
		return connectedVertices;
	}
	
	private int findMinOfMatrixNoZeroes() {
		int min = numNoms;
		for (int i = 0; i < numStudents; i++) {
			for (int j = 0; j < numStudents; j++) {
				if ((matrix[i][j] < min) && (matrix[i][j] != 0)) {
					min = matrix[i][j];
					if (min == 1) {
						return min;
					}
				}
			}
		}
		return min;
	}
	
	private void findMaximumMatching() {
		clearFlowNetwork();
		clearAdjacencyMatrix();
		populateAdjacencyMatrix();
		
		residualGraph = adjacencyMatrix.clone();
		
		//printResidualGraph();
		
		status = "Beginning new path";
		
		augPathExists = true;
		ArrayList<Integer> currentPath = new ArrayList<Integer>();
		
		while (augPathExists) {	
			
			if ("Beginning new path".equals(status)) {
				
				currentPath = setUpForNewPath(currentPath);
				currentPath = searchForTeacherNode(currentPath);
				
				if ("Beginning new path".equals(status)) {
					/* No teacher nodes to travel to; no more augmenting paths */
					augPathExists = false;
					break;
				}
				
			} else if ("At source node".equals(status)) {
				
				currentPath = searchForTeacherNode(currentPath);
				
				if ("At source node".equals(status)) {
					/* No teacher nodes to travel to; no more augmenting paths */
					augPathExists = false;
					break;
				}
				
			} else if ("At teacher level".equals(status)) {
				
				currentPath = searchForStudentNode(currentPath);
				
				if ("At teacher level".equals(status)) {
					// Couldn't find a student node to travel to
					
					// Mark this node as visited
					visited[currentNode] = true;
					
					// Remove this node from the path
					int lengthOfPath = currentPath.size();
					currentPath.remove(lengthOfPath - 1);
					
					// Go back to the last node
					currentNode = currentPath.get(lengthOfPath - 2);
					
					// Update the status
					if (currentNode == 0) {
						status = "At source node";
					} else {
						status = "At student level";
					}
				}
			
			} else if ("At student level".equals(status)) {
				
				currentPath = searchForEdgeToSink(currentPath);
				
				if ("Completed augmenting path".equals(status)) {
					
					updateFlowNetworkAndResidualGraph(currentPath);
					status = "Beginning new path";
					
				} else {
					currentPath = searchForTeacherNode(currentPath);
					
					if ("At student level".equals(status)) {
						// Couldn't find edge back to teacher level
						
						// Mark this node as visited
						visited[currentNode] = true;
						
						// Remove this node from the path
						int lengthOfPath = currentPath.size();
						currentPath.remove(lengthOfPath - 1);
						
						// Go back to the last node
						currentNode = currentPath.get(lengthOfPath - 2);
						
						// Update the status
						status = "At teacher level";
					}
				}
			}
		}	
		//printFlowNetwork();
	}
	
	private ArrayList<Integer> setUpForNewPath(ArrayList<Integer> currentPath) {
		currentPath.clear();
		
		isAugPath = false;
		Arrays.fill(visited, false);
		
		/* Start at source, look for any outgoing edge */
		currentPath.add(0);
		currentNode = 0;
		visited[0] = true;
		
		return currentPath;
	}
	
	private ArrayList<Integer> searchForTeacherNode(ArrayList<Integer> currentPath) {
		/* Look for a teacher node to travel to */
		int i = 1;
		while ((i < numStudents + 1) && ((visited[i]) || (residualGraph[currentNode][i] == 0))) {i++;}
		
		/* If there are no edges to teachers, break out of the loop */
		if (i == numStudents + 1) {
			return currentPath;
		}

		/* Otherwise, add the found node to the current path */
		currentNode = i;
		currentPath.add(currentNode);
		visited[currentNode] = true;
		status = "At teacher level";

		return currentPath;
	}

	private ArrayList<Integer> searchForStudentNode(ArrayList<Integer> currentPath) {
		/* At the teacher level, search for an edge to a student */
		int i = numStudents + 1;
		while ((i < 2 * numStudents + 1) && ((visited[i]) || (residualGraph[currentNode][i] == 0))) {i++;}
		
		/* If there are no edges to students, break out of the loop */
		if (i == 2 * numStudents + 1) {
			status = "At teacher level";
			return currentPath;
		}
		
		/* Arrived at the student level */
		currentNode = i;
		currentPath.add(currentNode);
		visited[currentNode] = true;
		status = "At student level";

		return currentPath;
	}

	private ArrayList<Integer> searchForEdgeToSink(ArrayList<Integer> currentPath) {
		/* Check if there is a path to the sink */
		if (residualGraph[currentNode][2 * numStudents + 1] == 1) {
			currentPath.add(2 * numStudents + 1);
			visited[2 * numStudents + 1] = true;
			currentNode = 2 * numStudents + 1;
			isAugPath = true;
			status = "Completed augmenting path";
		}

		return currentPath;
	}
	
	private void updateFlowNetworkAndResidualGraph(ArrayList<Integer> currentPath) {
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
	}
	
	private void printMatrix() {
		for (int row = 0; row < numStudents; row++) {
			for (int col = 0; col < numStudents; col++) {
				System.out.print(matrix[row][col] + " ");
			}
			System.out.println("");
		}
		System.out.println("");
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

	private void printResidualGraph() {
		for (int i = 0; i < 2 * numStudents + 2; i++) {
			for (int j = 0; j < 2 * numStudents + 2; j ++) {
				System.out.print(residualGraph[i][j] + " ");
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

	private void clearFlowNetwork() {
		for (int i = 0; i < 2 * numStudents + 2; i++) {
			Arrays.fill(flowNetwork[i], 0);
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

	private void populateFinalAssignments () {
		trial = new HashMap();
		for (int i = 1; i <= numTeachers; i++) {
			int teacherIndex = i - 1;
			int studentIndex = -1;
			int j = numStudents + 1;
			while (j < 2 * numStudents + 1) {
				if (flowNetwork[i][j] == 1) {
					studentIndex = j - (numStudents + 1);
					break;
				}
				j++;
			}
			Teacher teacher = teacherList[teacherIndex];
			Student student = studentList.get(studentIndex);
			trial.put(teacher, student);
		}
		finalAssignments.add(trial);
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
