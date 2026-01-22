import java.util.Scanner;

public class CancelledState implements CourseState {

    private final Course course;

    public CancelledState(Course course) {
        this.course = course;
    }

    public boolean tryEnroll(Student s) {
        System.out.println("Cannot enroll; course is CANCELLED: " + course.code);
        return false;
    }

    public boolean addToWaitlist(Student s) {
        System.out.println("Cannot waitlist; course not accepting waitlist: " + course.code);
        return false;
    }

    public boolean dropStudent(Student s) {
        System.out.println("Course is CANCELLED; no drops possible.");
        return false;
    }

    public void setCapacity(int newCapacity) {
        System.out.println("Course is CANCELLED; capacity change has no effect.");
    }

    public void setStatusAdmin(CourseStatus newStatus) {
        if (newStatus == CourseStatus.DRAFT) {
            course.transitionTo(CourseStatus.DRAFT);
        } else {
            System.out.println("Invalid: CANCELLED can only transition to DRAFT for " + course.code);
        }
    }

    public void setStatusAdminInteractive(CourseStatus s, Scanner sc) {
        setStatusAdmin(s);
    }
}
