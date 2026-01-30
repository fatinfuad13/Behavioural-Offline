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
    if (s == null) return false;

    boolean removed = false;

    // Remove from enrolled
    if (course.getEnrolled().remove(s)) {
        System.out.println("Dropped from enrolled: " + s.name + " from " + course.code);
        removed = true;

        // Adjust course status
        changedCapacity();
    }

    // Remove from waitlist (should rarely happen in OPEN, but just in case)
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
    int enrolledCount = course.getEnrolledCount();
    int capacity = course.getCapacity();

    if (enrolledCount >= capacity) {
        // Transition from OPEN → FULL
        course.setState(new FullState(course));
        course.setStatus(CourseStatus.FULL);
        System.out.println(course.code + " status changed to FULL (at capacity).");
    }
    // If enrollment drops below capacity, stays OPEN — no change needed
}



    @Override
    public void setStatusAdminInteractive(CourseStatus newStatus, Scanner sc) {
        setStatusAdmin(newStatus);
    }
   
@Override
public boolean tryEnroll(Student s) {

    if (course.getEnrolled().contains(s)) {
        System.out.println(s.name + " is already enrolled in " + course.code);
        return false;
    }
    
    course.getEnrolled().add(s);
    // Enroll first
    RegistrarMediator.getInstance().enrollStudent(course, s);

    System.out.println("Enrolled: " + s.name + " in " + course.code);

    // NOW check if full
    if (course.getEnrolledCount() == course.getCapacity()) {
        course.setState(new FullState(course));
        course.setStatus(CourseStatus.FULL);
        System.out.println(course.code + " is now FULL.");
    }

    return true;
}




@Override
public void setStatusAdmin(CourseStatus newStatus) {
    switch (newStatus) {
        case CLOSED:
            course.setState(new ClosedState(course));
            course.setStatus(CourseStatus.CLOSED);
            break;
        case DRAFT:
            course.setState(new DraftState(course));
            course.setStatus(CourseStatus.DRAFT);
            break;
        case CANCELLED:
            course.setState(new CancelledState(course));
            course.setStatus(CourseStatus.CANCELLED);
            RegistrarMediator.getInstance().cancelCourse(course);
            break;
        default:
            //if (newStatus != CourseStatus.FULL)
                System.out.println("Invalid transition from OPEN to " + newStatus);
            break;
    }
}


@Override
public CourseStatus getStatus()
{
    return CourseStatus.OPEN;
}


}