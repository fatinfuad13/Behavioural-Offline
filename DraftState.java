import java.util.Scanner;

public class DraftState implements CourseState {

    private final Course course;

    public DraftState(Course course) {
        this.course = course;
    }

    public boolean tryEnroll(Student s) {
        System.out.println("Cannot enroll; course is DRAFT (not visible): " + course.code);
        return false;
    }

    public boolean addToWaitlist(Student s) {
        System.out.println("Cannot waitlist; course not accepting waitlist: " + course.code);
        return false;
    }

    public boolean dropStudent(Student s) {
        System.out.println("Drop ignored; course is DRAFT: " + course.code);
        return false;
    }

    public void setCapacity(int newCapacity) {
        course.setCapacityInternal(newCapacity);
    }

    public void setStatusAdmin(CourseStatus newStatus) {
        if (newStatus == CourseStatus.OPEN || newStatus == CourseStatus.CLOSED) {
            course.transitionTo(newStatus);
        } else if (newStatus == CourseStatus.CANCELLED) {
            course.cancelCourseInternal();
        } else {
            System.out.println("Invalid transition from DRAFT to " + newStatus);
        }
    }

    public void setStatusAdminInteractive(CourseStatus s, Scanner sc) {
        setStatusAdmin(s);
    }
}
