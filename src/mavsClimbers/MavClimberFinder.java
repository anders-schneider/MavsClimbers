package mavsClimbers;

import java.util.ArrayList;
import java.util.HashMap;

public interface MavClimberFinder {
	
	void readInNoms();
	
	void runTrials();
	
	ArrayList<HashMap> returnResults();

	void setTeacherList(Teacher[] teacherList);

}
