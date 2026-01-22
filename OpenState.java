import java.util.Scanner;

public class OpenState implements CourseState {

    private final Course course;

    public OpenState(Course course) {
        this.course = course;
    }

    public boolean tryEnroll(Student s) {
        if (course.hasStudentEnrolled(s)) {
            System.out.println("Already enrolled: " + s.name + " in " + course.code);
            return true;
        }

        if (course.hasSeatAvailable()) {
            course.enrollStudentInternal(s);
            if (!course.hasSeatAvailable()) {
                course.transitionTo(CourseStatus.FULL);
                System.out.println(course.code + " is now FULL.");
            }
            return true;
        }

        course.transitionTo(CourseStatus.FULL);
        System.out.println(course.code + " reached capacity; status set to FULL. Try waitlisting.");
        return false;
    }

    public boolean addToWaitlist(Student s) {
        System.out.println("Course is OPEN; try enrolling instead: " + course.code);
        return false;
    }

    public boolean dropStudent(Student s) {
        return course.dropStudentInternal(s);
    }

    public void setCapacity(int newCapacity) {
        course.setCapacityInternal(newCapacity);
        if (!course.hasSeatAvailable()) {
            course.transitionTo(CourseStatus.FULL);
        }
    }

    public void setStatusAdmin(CourseStatus newStatus) {
        if (newStatus == CourseStatus.CLOSED || newStatus == CourseStatus.DRAFT) {
            course.transitionTo(newStatus);
        } else if (newStatus == CourseStatus.CANCELLED) {
            course.cancelCourseInternal();
        } else {
            System.out.println("Invalid transition from OPEN to " + newStatus);
        }
    }

    public void setStatusAdminInteractive(CourseStatus s, Scanner sc) {
        setStatusAdmin(s);
    }
}
