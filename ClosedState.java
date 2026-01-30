
import java.util.Scanner;

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
    if (s == null) return false;

    boolean removed = false;

    // Only allow removing from enrolled or waitlist
    if (course.getEnrolled().remove(s)) {
        System.out.println("Dropped from enrolled: " + s.name + " from " + course.code);
        removed = true;
    }

    if (course.getWaitlist().remove(s)) {
        System.out.println("Removed from waitlist: " + s.name + " for " + course.code);
        removed = true;
    }

    if (!removed) {
        System.out.println(s.name + " is neither enrolled nor waitlisted for " + course.code);
        return false;
    }

    // Cross-object update
    RegistrarMediator.getInstance().dropStudent(course,s);

    return true;
}

@Override
public void changedCapacity() {
    // capacity changes do not affect status
}


    @Override
public void setStatusAdmin(CourseStatus newStatus) {

    switch (newStatus) {

        case OPEN:
            course.setState(new OpenState(course));
            break;

        case DRAFT:
            course.setState(new DraftState(course));
            break;

        case CANCELLED:
            course.setState(new CancelledState(course));
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

    @Override
    public CourseStatus getStatus()
    {
        return CourseStatus.CLOSED;
    }
}

