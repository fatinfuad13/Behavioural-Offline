import java.util.Scanner;

public class FullState implements CourseState {
    private final Course course;

    public FullState(Course course) {
        this.course = course;
    }

    public boolean tryEnroll(Student s) {
        System.out.println("Cannot enroll; course is FULL. You may waitlist: " + course.code);
        return false;
    }

    public boolean addToWaitlist(Student s) {
        if (course.hasStudentEnrolled(s) || course.hasStudentWaitlisted(s)) {
            System.out.println("Already enrolled or waitlisted: " + s.name + " for " + course.code);
            return false;
        }
        course.waitlistStudentInternal(s);
        return true;
    }

    public boolean dropStudent(Student s) {
        boolean changed = course.dropStudentInternal(s);
        if (course.hasSeatAvailable()) {
            course.transitionTo(CourseStatus.OPEN);
        }
        return changed;
    }

    public void setCapacity(int newCapacity) {
        course.setCapacityInternal(newCapacity);
        if (course.hasSeatAvailable()) {
            course.transitionTo(CourseStatus.OPEN);
        }
    }

    public void setStatusAdmin(CourseStatus newStatus) {
        if (newStatus == CourseStatus.CLOSED) {
            course.closeWithRandomWaitlistSelectionInternal(course.getCapacity());
        } else if (newStatus == CourseStatus.CANCELLED) {
            course.cancelCourseInternal();
        } else {
            System.out.println("Invalid transition from FULL to " + newStatus);
        }
    }

    public void setStatusAdminInteractive(CourseStatus newStatus, Scanner sc) {
        course.handleFullToClosedInteractive(newStatus, sc);
    }
}
