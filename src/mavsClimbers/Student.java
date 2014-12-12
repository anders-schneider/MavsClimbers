package mavsClimbers;

import java.util.HashMap;

public class Student {
	
	private String name;
	private HashMap mavNoms;
	
	public Student(String name) {
		this.name = name;
		mavNoms = new HashMap();
	}
	
	public Student(String name, Teacher teacher, int rank) {
		this.name = name;
		mavNoms = new HashMap();
		mavNoms.put(teacher, rank);
	}
	
	public void addAsMavNom(Teacher teacher, int rank) {
		mavNoms.put(teacher, rank);
	}
	
	public int getScore(Teacher teacher) {
		return (int) mavNoms.get(teacher);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Student)) {
			return false;
		}
		
		Student otherStudent = (Student) o;
		
		if (name.equals(otherStudent.toString())) {
			return true;
		} else {
			return false;
		}
	}
}
