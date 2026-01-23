
import java.util.Scanner;
// Done
public class ClosedState implements CourseState {

    private final Course course;

    public ClosedState(Course course) {
        this.course = course;
    }

    @Override
    public boolean tryEnroll(Student s) {
        System.out.println("Cannot enroll; course is CLOSED: " + course.code);
        return false;
    }

    @Override
    public boolean addToWaitlist(Student s) {
        System.out.println("Cannot waitlist; course not accepting waitlist: " + course.code);
        return false;
    }

    @Override
    public boolean dropStudent(Student s) {
        // Delegate drop to mediator
        RegistrarMediator.getInstance().dropStudent(course, s);
        return true;
    }

    @Override
    public void setCapacity(int newCapacity) {
        
    }

    @Override
    public void setStatusAdmin(CourseStatus newStatus) {
        switch (newStatus) {
            case OPEN:
            case DRAFT:
                course.setStatus(newStatus);
                break;
            case CANCELLED:
                course.setStatus(CourseStatus.CANCELLED);
                RegistrarMediator.getInstance().cancelCourse(course);
                break;
            default:
                System.out.println("Invalid transition from CLOSED to " + newStatus);
        }
    }

    @Override
    public void setStatusAdminInteractive(CourseStatus s, Scanner sc) {
        setStatusAdmin(s);
    }
}

