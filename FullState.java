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

    if (course.getEnrolled().contains(s)) {
        System.out.println(s.name + " is already enrolled in " + course.code + "; cannot waitlist.");
        return false;
    }

    if (course.getWaitlist().contains(s)) {
        System.out.println(s.name + " is already waitlisted for " + course.code);
        return false;
    }

    
    course.getWaitlist().add(s);
    System.out.println("Waitlisted: " + s.name + " for " + course.code);

   
    RegistrarMediator.getInstance().waitlistStudent(s, course);

    return true;
}


@Override
public boolean dropStudent(Student s) {
    if (s == null) return false;

    boolean removed = false;

    
    if (course.getEnrolled().remove(s)) {
        System.out.println("Dropped from enrolled: " + s.name + " from " + course.code);
        removed = true;

       
        RegistrarMediator.getInstance().dropStudent(course, s);

        // Promote from waitlist if space (FIFO)
        while (!course.getWaitlist().isEmpty() && course.getEnrolledCount() < course.getCapacity()) {
            Student promoted = course.getWaitlist().removeFirst();

            
            course.getEnrolled().add(promoted);

            
            RegistrarMediator.getInstance().enrollStudent(course, promoted);

            System.out.println("Promoted from waitlist: " + promoted.name + " into " + course.code);
        }

        changedCapacity();
    }

    // Remove from waitlist if present
    if (course.getWaitlist().remove(s)) {
        System.out.println("Removed from waitlist: " + s.name + " for " + course.code);
        removed = true;

        
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
        course.setState(new OpenState(course));
        //course.setStatus(CourseStatus.OPEN);
    } else if (course.getEnrolledCount() >= course.getCapacity()) {
        course.setState(new FullState(course));
        //course.setStatus(CourseStatus.FULL);
    }
}



   @Override
public void setStatusAdmin(CourseStatus newStatus) {
    switch (newStatus) {
        case CLOSED:
            course.setState(new ClosedState(course));
            course.closeWithRandomWaitlistSelection(course.getCapacity());
            break;
        case CANCELLED:
            course.setState(new CancelledState(course));
            RegistrarMediator.getInstance().cancelCourse(course);
            break;
        default:
            if (newStatus != CourseStatus.FULL)
            {
                if(newStatus == CourseStatus.OPEN) 
                    System.out.println("Invalid transition from FULL to " + newStatus +  " (FULL->OPEN is automatic on drop)");
                else
                    System.out.println("Invalid transition from FULL to " + newStatus);
            }
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