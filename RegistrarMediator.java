import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RegistrarMediator {

    private static RegistrarMediator instance;

    private RegistrarMediator() {}

    public static RegistrarMediator getInstance() {
        if (instance == null) instance = new RegistrarMediator();
        return instance;
    }

   
    public void enrollStudent(Course c, Student s) {
        if (c == null || s == null) return;

        if (c.getState() instanceof CancelledState || c.getState() instanceof DraftState) {
            System.out.println("Cannot enroll; course " + c.code + " is not open for enrollment.");
            return;
        }

        if (c.getEnrolled().contains(s)) {
            System.out.println(s.name + " is already enrolled in " + c.code);
            return;
        }

        if (c.getEnrolledCount() < c.getCapacity()) {
            c.getEnrolled().add(s);
            s.addEnrolledCourseDirect(c);
            System.out.println("Enrolled: " + s.name + " in " + c.code);

            // Check if course is now full
            if (c.getEnrolledCount() >= c.getCapacity()) {
                c.setStatusAdmin(CourseStatus.FULL);
            }
        } else {
            System.out.println("Course " + c.code + " is full. Cannot enroll.");
        }
    }

    public void waitlistStudent(Course c, Student s) {
        if (c == null || s == null) return;

        if (c.getState() instanceof CancelledState || c.getState() instanceof DraftState) {
            System.out.println("Cannot waitlist; course " + c.code + " is not open for waitlisting.");
            return;
        }

        if (c.getWaitlist().contains(s)) {
            System.out.println(s.name + " is already waitlisted for " + c.code);
            return;
        }

        if (c.getEnrolled().contains(s)) {
            System.out.println(s.name + " is already enrolled in " + c.code+ "; cannot waitlist.");
            return;
        }

        c.getWaitlist().add(s);
        s.addWaitlistCourseDirect(c);
        System.out.println("Waitlisted: " + s.name + " for " + c.code);
    }

    
    public void dropStudent(Course c, Student s) {
        if (c == null || s == null) return;

        boolean removed = false;

        // Remove from enrolled
        if (c.getEnrolled().remove(s)) {
            s.removeCourseDirect(c);
            System.out.println("Dropped from enrolled: " + s.name + " from " + c.code);
            removed = true;

            // Promote one from waitlist if space
            if (!c.getWaitlist().isEmpty() && c.getEnrolledCount() < c.getCapacity()) {
                Student promoted = c.getWaitlist().removeFirst();
                c.getEnrolled().add(promoted);
                promoted.addEnrolledCourseDirect(c);
                System.out.println("Promoted from waitlist: " + promoted.name + " into " + c.code);
            }

            // Adjust course status
            if (c.getEnrolledCount() >= c.getCapacity()) {
                c.setStatusAdmin(CourseStatus.FULL);
            } else if (c.getEnrolledCount() < c.getCapacity()
                    && !(c.getState() instanceof DraftState)
                    && !(c.getState() instanceof CancelledState)) {
                c.setStatusAdmin(CourseStatus.OPEN);
            }
        }

        // Remove from waitlist if present
        if (c.getWaitlist().remove(s)) {
            s.removeCourseDirect(c);
            System.out.println("Removed from waitlist: " + s.name + " for " + c.code);
            removed = true;
        }

        if (!removed) {
            System.out.println(s.name + " is neither enrolled nor waitlisted for " + c.code);
        }
    }

    
    public void cancelCourse(Course c) {
        if (c == null) return;

        // Drop all enrolled and waitlisted students
        for (Student s : new ArrayList<>(c.getEnrolled())) {
            s.removeCourseDirect(c);
        }
        for (Student s : new ArrayList<>(c.getWaitlist())) {
            s.removeCourseDirect(c);
        }
        c.getEnrolled().clear();
        c.getWaitlist().clear();
        c.setStatusAdmin(CourseStatus.CANCELLED);

        System.out.println(c.code + " has been CANCELLED. All students dropped and waitlist cleared.");
    }

    
    public void promoteRandomFromWaitlist(Course c, int slots) {
        if (c == null || c.getWaitlist().isEmpty()) return;

        Random random = new Random();
        List<Student> copy = new ArrayList<>(c.getWaitlist());
        int promotionCount = Math.min(slots, copy.size());

        System.out.println("Randomly promoting " + promotionCount + " student(s) from waitlist for " + c.code);

        for (int i = 0; i < promotionCount; i++) {
            int idx = random.nextInt(copy.size());
            Student s = copy.remove(idx);
            c.getWaitlist().remove(s);
            c.getEnrolled().add(s);
            s.addEnrolledCourseDirect(c);
            System.out.println("  Promoted: " + s.name);
        }
    }

    public void promoteFromWaitlistRandomly(Course c, int targetCapacity) {
    if (c.getWaitlist().isEmpty()) return;

    int availableSlots = targetCapacity - c.getEnrolledCount();
    if (availableSlots <= 0) return;

    Random random = new Random();
    List<Student> copy = new ArrayList<>(c.getWaitlist());
    int promoteCount = Math.min(availableSlots, copy.size());

    System.out.println("Randomly selecting " + promoteCount + " student(s) from waitlist:");
    for (int i = 0; i < promoteCount; i++) {
        int idx = random.nextInt(copy.size());
        Student s = copy.remove(idx);
        c.getWaitlist().remove(s);
        c.getEnrolled().add(s);
        s.addEnrolledCourseDirect(c);
        System.out.println("  Randomly selected: " + s.name + " for " + c.code);
    }
}



}



