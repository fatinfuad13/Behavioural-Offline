import java.util.Scanner;

public class ClosedState implements CourseState {

    private final Course course;

    public ClosedState(Course course) {
        this.course = course;
    }

    public boolean tryEnroll(Student s) {
        System.out.println("Cannot enroll; course is CLOSED: " + course.code);
        return false;
    }

    public boolean addToWaitlist(Student s) {
        System.out.println("Cannot waitlist; course not accepting waitlist: " + course.code);
        return false;
    }

    public boolean dropStudent(Student s) {
        return course.dropStudentInternal(s);
    }

    public void setCapacity(int newCapacity) {
        course.setCapacityInternal(newCapacity);
    }

    public void setStatusAdmin(CourseStatus newStatus) {
        if (newStatus == CourseStatus.OPEN || newStatus == CourseStatus.DRAFT) {
            course.transitionTo(newStatus);
        } else if (newStatus == CourseStatus.CANCELLED) {
            course.transitionTo(newStatus);
            course.cancelCourseInternal();
        }else {
            System.out.println("Invalid transition from CLOSED to " + newStatus);
        }
    }

    public void setStatusAdminInteractive(CourseStatus s, Scanner sc) {
        setStatusAdmin(s);
    }
}
