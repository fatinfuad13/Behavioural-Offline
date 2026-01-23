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
    RegistrarMediator.getInstance().waitlistStudent(course, s);
    return true;
}


  @Override
public boolean dropStudent(Student s) {
    RegistrarMediator.getInstance().dropStudent(course, s);
    changedCapacity();
    return true;
}


@Override
public void changedCapacity() {
    if (course.getEnrolled().size() < course.getCapacity()) {
            course.setStatus(CourseStatus.OPEN);
        } else {
            //System.out.println(course.code + " over capacity; remains FULL.");
        }
}

    @Override
    public void setStatusAdmin(CourseStatus newStatus) {
        switch (newStatus) {
            case CLOSED:
                
                course.closeWithRandomWaitlistSelection(course.getCapacity());
                break;
            case CANCELLED:
                course.setStatus(CourseStatus.CANCELLED);
                RegistrarMediator.getInstance().cancelCourse(course);
                break;
            default:
                if(newStatus != CourseStatus.FULL) System.out.println("Invalid transition from FULL to " + newStatus);
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
            
            course.closeWithRandomWaitlistSelection(course.getCapacity());
        } else {
            setStatusAdmin(newStatus);
        }
    }
}