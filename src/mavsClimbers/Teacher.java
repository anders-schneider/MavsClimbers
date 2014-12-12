package mavsClimbers;

public class Teacher {
	
	private String teacherName;
	private String courseName;
	private Student[] mavNoms;
	private Student mav;
	
	public Teacher(String teacherName, String courseName) {
		this.teacherName = teacherName;
		this.courseName = courseName;
	}
	
	public void addNoms(Student[] mavNoms) {
		this.mavNoms = mavNoms;
	}
	
	public Student getMavNom(int index) {
		return mavNoms[index];
	}
	
	public Student[] getMavNomArray() {
		return mavNoms;
	}
	
	public void setMav(Student mav) {
		this.mav = mav;
	}
	
	public Student getMav() {
		return mav;
	}
	
	public int howManyNoms() {
		return mavNoms.length;
	}
	
	public String getCourseName() {
		return courseName;
	}
	
	@Override
	public String toString() {
		return this.teacherName;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Teacher)) {
			return false;
		}
		
		Teacher otherTeacher = (Teacher) o;
		
		if (teacherName.equals(otherTeacher.toString())) {
			return true;
		} else {
			return false;
		}
	}
}
