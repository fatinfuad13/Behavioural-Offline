
import java.util.Scanner;
// Done
public class DraftState implements CourseState {

    private final Course course;

    public DraftState(Course course) {
        this.course = course;
    }

    @Override
    public boolean tryEnroll(Student s) {
        System.out.println("Cannot enroll; course is DRAFT (not visible): " + course.code);
        return false;
    }

    @Override
    public boolean addToWaitlist(Student s) {
        System.out.println("Cannot waitlist; course not accepting waitlist: " + course.code);
        return false;
    }

    @Override
    public boolean dropStudent(Student s) {
        System.out.println("Drop ignored; course is DRAFT: " + course.code);
        return false;
    }

    @Override
    public void setCapacity(int newCapacity) {

    }

    @Override
    public void setStatusAdmin(CourseStatus newStatus) {
        switch (newStatus) {
            case OPEN:
            case CLOSED:
                course.setStatus(newStatus);
                break;
            case CANCELLED:
                course.setStatus(CourseStatus.CANCELLED);
                RegistrarMediator.getInstance().cancelCourse(course);
                break;
            default:
                System.out.println("Invalid transition from DRAFT to " + newStatus);
        }
    }

    @Override
    public void setStatusAdminInteractive(CourseStatus s, Scanner sc) {
        setStatusAdmin(s);
    }
}

