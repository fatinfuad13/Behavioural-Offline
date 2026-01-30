
import java.util.Scanner;

// DONE
public class CancelledState implements CourseState {

    private final Course course;

    public CancelledState(Course course) {
        this.course = course;
    }

    @Override
    public boolean tryEnroll(Student s) {
        System.out.println("Cannot enroll; course is CANCELLED: " + course.code);
        return false;
    }

    @Override
    public boolean addToWaitlist(Student s) {
        System.out.println("Cannot waitlist; course not accepting waitlist: " + course.code);
        return false;
    }

    @Override
    public boolean dropStudent(Student s) {
        System.out.println("Course is CANCELLED; no drops possible.");
        return false;
    }

    @Override
    public void changedCapacity() {
        System.out.println("Course is CANCELLED; capacity change has no effect.");
    }

    @Override
    public void setStatusAdmin(CourseStatus newStatus) {
        if (newStatus == CourseStatus.DRAFT) {
            course.setState(new DraftState(course));
            
        } else {
            System.out.println("Invalid: CANCELLED can only transition to DRAFT for " + course.code);
        }
    }

    @Override
    public void setStatusAdminInteractive(CourseStatus s, Scanner sc) {
        setStatusAdmin(s);
    }

    @Override
    public CourseStatus getStatus() {
        return CourseStatus.CANCELLED;
    }
}
