import java.util.Scanner;

public class FullState implements CourseState {

    private final Course course;

    public FullState(Course course) {
        this.course = course;
    }

    @Override
    public boolean tryEnroll(Student s) {
        System.out.println("Cannot enroll; course is FULL. You may waitlist: " + course.code);
        return false;
    }

    @Override
public boolean addToWaitlist(Student s) {
    if (s == null) return false;

    // Already enrolled?
    if (course.getEnrolled().contains(s)) {
        System.out.println(s.name + " is already enrolled in " + course.code + "; cannot waitlist.");
        return false;
    }

    // Already waitlisted?
    if (course.getWaitlist().contains(s)) {
        System.out.println(s.name + " is already waitlisted for " + course.code);
        return false;
    }

    // Add to course waitlist
    course.getWaitlist().add(s);
    System.out.println("Waitlisted: " + s.name + " for " + course.code);

    // Delegate cross-object updates to Mediator
    RegistrarMediator.getInstance().waitlistStudent(s, course);

    return true;
}


 /*@Override
public boolean dropStudent(Student s) {
    if (s == null) return false;

    boolean removed = false;

    // Remove from enrolled
    if (course.getEnrolled().remove(s)) {
        System.out.println("Dropped from enrolled: " + s.name + " from " + course.code);
        removed = true;

        // Promote one from waitlist if space
        if (!course.getWaitlist().isEmpty() && course.getEnrolledCount() < course.getCapacity()) {
            Student promoted = course.getWaitlist().removeFirst();
            course.getEnrolled().add(promoted);
            System.out.println("Promoted from waitlist: " + promoted.name + " into " + course.code);

            // Cross-object update via Mediator
            RegistrarMediator.getInstance().enrollStudent(course,promoted);
        }

        // Adjust course status
        changedCapacity();
    }

    // Remove from waitlist if present
    if (course.getWaitlist().remove(s)) {
        System.out.println("Removed from waitlist: " + s.name + " for " + course.code);
        removed = true;
    }

    if (!removed) {
        System.out.println(s.name + " is neither enrolled nor waitlisted for " + course.code);
        return false;
    }

    // Cross-object update for the student who dropped
    RegistrarMediator.getInstance().dropStudent(course,s);

    return true;
}

@Override
public void changedCapacity() {
    if (course.getEnrolledCount() < course.getCapacity()) {
        course.setStatus(CourseStatus.OPEN);
    } else if (course.getEnrolledCount() >= course.getCapacity()) {
        course.setStatus(CourseStatus.FULL);
    }
}*/

 
@Override
public boolean dropStudent(Student s) {
    if (s == null) return false;

    boolean removed = false;

    // Remove from enrolled
    if (course.getEnrolled().remove(s)) {
        System.out.println("Dropped from enrolled: " + s.name + " from " + course.code);
        removed = true;

        // Update student only via mediator
        RegistrarMediator.getInstance().dropStudent(course, s);

        // Promote from waitlist if space (FIFO)
        while (!course.getWaitlist().isEmpty() && course.getEnrolledCount() < course.getCapacity()) {
            Student promoted = course.getWaitlist().removeFirst();

            // Add to course enrolled list (state handles course logic)
            course.getEnrolled().add(promoted);

            // Update student object via mediator
            RegistrarMediator.getInstance().enrollStudent(course, promoted);

            System.out.println("Promoted from waitlist: " + promoted.name + " into " + course.code);
        }

        // Adjust course status
        changedCapacity();
    }

    // Remove from waitlist if present
    if (course.getWaitlist().remove(s)) {
        System.out.println("Removed from waitlist: " + s.name + " for " + course.code);
        removed = true;

        // Update student object only
        RegistrarMediator.getInstance().dropStudent(course, s);
    }

    if (!removed) {
        System.out.println(s.name + " is neither enrolled nor waitlisted for " + course.code);
        return false;
    }

    return true;
}


@Override
public void changedCapacity() {
    if (course.getEnrolledCount() < course.getCapacity()) {
        course.setStatus(CourseStatus.OPEN);
    } else if (course.getEnrolledCount() >= course.getCapacity()) {
        course.setStatus(CourseStatus.FULL);
    }
}



   @Override
public void setStatusAdmin(CourseStatus newStatus) {
    switch (newStatus) {
        case CLOSED:
            // FULL → CLOSED: preserve waitlist logic
            course.setState(new ClosedState(course));
            course.setStatus(CourseStatus.CLOSED);
            course.closeWithRandomWaitlistSelection(course.getCapacity());
            break;
        case CANCELLED:
            course.setState(new CancelledState(course));
            course.setStatus(CourseStatus.CANCELLED);
            RegistrarMediator.getInstance().cancelCourse(course);
            break;
        default:
            if (newStatus != CourseStatus.FULL)
                System.out.println("Invalid transition from FULL to " + newStatus);
            break;
    }
}

@Override
public void setStatusAdminInteractive(CourseStatus newStatus, Scanner sc) {
    if (newStatus == CourseStatus.CLOSED) {
        System.out.println(course.code + " has " + course.getWaitlist().size() + " student(s) on waitlist.");
        System.out.print("Enter new capacity, or 0 to not increase: ");
        try {
            int newCapacity = Integer.parseInt(sc.nextLine().trim());
            if (newCapacity > course.getCapacity()) {
                course.setCapacity(newCapacity); // triggers state logic
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input; using current capacity.");
        }
        course.setState(new ClosedState(course));
        course.setStatus(CourseStatus.CLOSED);
        course.closeWithRandomWaitlistSelection(course.getCapacity());
    } else {
        setStatusAdmin(newStatus);
    }
}

    @Override
    public CourseStatus getStatus()
    {
        return CourseStatus.FULL;
    }
}