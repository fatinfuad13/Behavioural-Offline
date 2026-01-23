import java.util.Scanner;

public class OpenState implements CourseState {

    private final Course course;

    public OpenState(Course course) {
        this.course = course;
    }

   /*  @Override
    public boolean tryEnroll(Student s) {
    if (course.getEnrolled().contains(s)) {
        System.out.println("Already enrolled: " + s.name + " in " + course.code);
        return true;
    }

    if (course.hasSpace()) {
        RegistrarMediator.getInstance().enrollStudent(course, s);

        // Only print if course becomes full now
        if (!course.hasSpace() && course.status != CourseStatus.FULL) {
            course.setStatus(CourseStatus.FULL);
            System.out.println(course.code + " is now FULL.");
        }

        return true;
    }

    // No need to set FULL again if course is already full
    if (course.status != CourseStatus.FULL) {
        course.setStatus(CourseStatus.FULL);
        System.out.println(course.code + " reached capacity; status set to FULL. Try waitlisting.");
    }
    return false;
}*/


    @Override
    public boolean addToWaitlist(Student s) {
        System.out.println("Course is OPEN; try enrolling instead: " + course.code);
        return false;
    }

    @Override
    public boolean dropStudent(Student s) {
        RegistrarMediator.getInstance().dropStudent(course, s);
        return true;
    }

    /*@Override
    public void setCapacity(int newCapacity) {
        if (newCapacity < 0) newCapacity = 0;
        course.setCapacity(newCapacity);

        // If capacity reached, switch to FULL
        if (!course.hasSpace()) {
            course.setStatus(CourseStatus.FULL);
            System.out.println(course.code + " status changed to FULL (at capacity).");
        }
    }*/

    /*@Override
    public void setStatusAdmin(CourseStatus newStatus) {
        if (newStatus == CourseStatus.CLOSED || newStatus == CourseStatus.DRAFT) {
            course.setStatus(newStatus);
        } else if (newStatus == CourseStatus.CANCELLED) {
            course.setStatus(CourseStatus.CANCELLED);
            RegistrarMediator.getInstance().cancelCourse(course);
        } else {
            if(newStatus != CourseStatus.FULL) System.out.println("Invalid transition from OPEN to " + newStatus);
        }
    }*/

    @Override
    public void setStatusAdminInteractive(CourseStatus newStatus, Scanner sc) {
        setStatusAdmin(newStatus);
    }
    @Override
public boolean tryEnroll(Student s) {
    if (course.getEnrolled().contains(s)) {
        System.out.println("Already enrolled: " + s.name + " in " + course.code);
        return true;
    }

    if (course.hasSpace()) {
        RegistrarMediator.getInstance().enrollStudent(course, s);

        // Only print if course becomes full now (automatic)
        if (!course.hasSpace() && course.status != CourseStatus.FULL) {
            course.status = CourseStatus.FULL;   // direct update, bypass setStatus
            course.setState(new FullState(course));
            System.out.println(course.code + " is now FULL.");
        }

        return true;
    }

    // No need to set FULL again if course is already full
    if (course.status != CourseStatus.FULL) {
        course.status = CourseStatus.FULL;       // direct update
        course.setState(new FullState(course));
        System.out.println(course.code + " reached capacity; status set to FULL. Try waitlisting.");
    }
    return false;
}

@Override
/*public void setCapacity(int newCapacity) {
    if (newCapacity < 0) newCapacity = 0;
    course.setCapacity(newCapacity);

    // Automatic FULL update
    if (!course.hasSpace() && course.status != CourseStatus.FULL) {
        course.status = CourseStatus.FULL;       // direct update
        course.setState(new FullState(course));
        System.out.println(course.code + " status changed to FULL (at capacity).");
        course.setStatus(CourseStatus.FULL);
            System.out.println(course.code + " is now FULL.")
    }
}*/


public void setCapacity(int newCapacity) {
    /*if (newCapacity < 0) newCapacity = 0;

    // Update capacity via mediator (may auto-promote)
    course.setCapacity(newCapacity);

    // Recompute status **after mediator updates**
    if (!course.hasSpace() && course.getStatus() != CourseStatus.FULL) {
        course.status = CourseStatus.FULL;       // direct update
        course.setState(new FullState(course));
        System.out.println(course.code + " status changed to FULL (at capacity).");
    } else if (course.hasSpace() && course.getStatus() == CourseStatus.FULL) {
        // course now has space again (optional)
        course.status = CourseStatus.OPEN;
        course.setState(new OpenState(course));
        System.out.println(course.code + " status changed to OPEN (capacity allows enrollment).");
    }*/
    if (course.getEnrolled().size() >= course.getCapacity()) {
            course.setStatus(CourseStatus.FULL);
        }
}


@Override
public void setStatusAdmin(CourseStatus newStatus) {
    if (newStatus == CourseStatus.CLOSED || newStatus == CourseStatus.DRAFT) {
        course.setStatus(newStatus);
    } else if (newStatus == CourseStatus.CANCELLED) {
        course.setStatus(CourseStatus.CANCELLED);
        RegistrarMediator.getInstance().cancelCourse(course);
    } else {
        // suppress print for automatic FULL updates
        if(newStatus != CourseStatus.FULL)
            System.out.println("Invalid transition from OPEN to " + newStatus);
    }
}

}