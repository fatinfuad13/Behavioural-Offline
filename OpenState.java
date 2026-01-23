import java.util.Scanner;
// Done
public class OpenState implements CourseState {

    private final Course course;

    public OpenState(Course course) {
        this.course = course;
    }

    @Override
    public boolean addToWaitlist(Student s) {
        System.out.println("Course is OPEN; try enrolling instead: " + course.code);
        return false;
    }

    @Override
    public boolean dropStudent(Student s) {
        RegistrarMediator.getInstance().dropStudent(course, s);
        return true;
    }

    @Override
    public void setStatusAdminInteractive(CourseStatus newStatus, Scanner sc) {
        setStatusAdmin(newStatus);
    }
   
@Override
public boolean tryEnroll(Student s) {
    RegistrarMediator.getInstance().enrollStudent(course, s);
    changedCapacity();
    return true;
}


@Override

public void changedCapacity() {
    if (course.getEnrolled().size() >= course.getCapacity()) {
            course.setStatus(CourseStatus.FULL);
        }
}


@Override
public void setStatusAdmin(CourseStatus newStatus) {
    if (newStatus == CourseStatus.CLOSED || newStatus == CourseStatus.DRAFT) {
        course.setStatus(newStatus);
    } else if (newStatus == CourseStatus.CANCELLED) {
        course.setStatus(CourseStatus.CANCELLED);
        RegistrarMediator.getInstance().cancelCourse(course);
    } else {
        
        if(newStatus != CourseStatus.FULL)
            System.out.println("Invalid transition from OPEN to " + newStatus);
    }
}

}