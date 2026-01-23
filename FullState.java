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
        if (course.getEnrolled().contains(s) || course.getWaitlist().contains(s)) {
            System.out.println("Already enrolled or waitlisted: " + s.name + " for " + course.code);
            return false;
        }
        RegistrarMediator.getInstance().waitlistStudent(course, s);
        return true;
    }

  @Override
public boolean dropStudent(Student s) {
    RegistrarMediator.getInstance().dropStudent(course, s);

    // Automatic FULL -> OPEN
    if (course.hasSpace() && course.getStatus() != CourseStatus.CANCELLED) {
        course.status = CourseStatus.OPEN;          // direct update
        course.setState(new OpenState(course));
        System.out.println(course.code + " status changed to OPEN (capacity allows enrollment).");
    }
    return true; // removal handled by mediator
}

@Override
public void setCapacity(int newCapacity) {
    if (course.getEnrolled().size() < course.getCapacity()) {
            course.setStatus(CourseStatus.OPEN);
        } else {
            System.out.println(course.code + " over capacity; remains FULL.");
        }
}

    @Override
    public void setStatusAdmin(CourseStatus newStatus) {
        switch (newStatus) {
            case CLOSED:
                // random promotion handled in course via mediator
                course.closeWithRandomWaitlistSelection(course.getCapacity());
                break;
            case CANCELLED:
                course.setStatus(CourseStatus.CANCELLED);
                RegistrarMediator.getInstance().cancelCourse(course);
                break;
            default:
                System.out.println("Invalid transition from FULL to " + newStatus);
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
                    course.setCapacity(newCapacity);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input; using current capacity.");
            }
            // Always trigger the mediator to randomly promote waitlisted students
            course.closeWithRandomWaitlistSelection(course.getCapacity());
        } else {
            setStatusAdmin(newStatus);
        }
    }
}