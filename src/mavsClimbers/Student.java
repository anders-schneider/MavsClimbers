package mavsClimbers;

public class Student {
	
	private String name;
	
	public Student(String name) {
		this.name = name;
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
