import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RegistrarMediator {

    private static RegistrarMediator instance;

    private RegistrarMediator() 
    {

    }

    public static RegistrarMediator getInstance() {
        if (instance == null) 
            instance = new RegistrarMediator();
        return instance;
    }

   
    public void enrollStudent(Course c, Student s) {
        if (c == null || s == null)
            return;

        //c.getEnrolled().add(s);
        s.addEnrolledCourseDirect(c);
    }


    public void waitlistStudent(Student s, Course c) {
    if (s == null || c == null) return;

    // Mediator handles the cross-object update
    if (!s.getWaitlistedCourses().contains(c)) {
        s.addWaitlistCourseDirect(c);
    }
}


    
    public void dropStudent(Course c,Student s) {
    if (s == null || c == null) return;
    s.removeCourseDirect(c);
}
    
    public void cancelCourse(Course c) {
        if (c == null) return;

        for (Student s : new ArrayList<>(c.getEnrolled())) {
            s.removeCourseDirect(c);
        }
        for (Student s : new ArrayList<>(c.getWaitlist())) {
            s.removeCourseDirect(c);
        }
        c.getEnrolled().clear();
        c.getWaitlist().clear();
        //c.setStatusAdmin(CourseStatus.CANCELLED);

        System.out.println(c.code + " has been CANCELLED. All students dropped and waitlist cleared.");
    }

    
    /*public void promoteRandomFromWaitlist(Course c, int slots) {
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
}*/



}



