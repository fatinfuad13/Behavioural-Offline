import java.util.ArrayList;

public class RegistrarMediator {

    private static RegistrarMediator instance;

    private RegistrarMediator() {

    }

    public static RegistrarMediator getInstance() {
        if (instance == null)
            instance = new RegistrarMediator();
        return instance;
    }

    public void enrollStudent(Course c, Student s) {
        if (c == null || s == null)
            return;

        s.addEnrolledCourseDirect(c);
    }

    public void waitlistStudent(Student s, Course c) {
        if (s == null || c == null)
            return;

        if (!s.getWaitlistedCourses().contains(c)) {
            s.addWaitlistCourseDirect(c);
        }
    }

    public void dropStudent(Course c, Student s) {
        if (s == null || c == null)
            return;
        s.removeCourseDirect(c);
    }

    public void cancelCourse(Course c) {
        if (c == null)
            return;

        for (Student s : new ArrayList<>(c.getEnrolled())) {
            s.removeCourseDirect(c);
        }
        for (Student s : new ArrayList<>(c.getWaitlist())) {
            s.removeCourseDirect(c);
        }
        c.getEnrolled().clear();
        c.getWaitlist().clear();

        System.out.println(c.code + " has been CANCELLED. All students dropped and waitlist cleared.");
    }

   

public void enrollRequest(Student s, Course c) {
    if (s == null || c == null) return;
    c.getState().tryEnroll(s);
}

public void waitlistRequest(Student s, Course c) {
    if (s == null || c == null) return;
    c.getState().addToWaitlist(s);
}

public void dropRequest(Student s, Course c) {
    if (s == null || c == null) return;
    c.getState().dropStudent(s);
}


}
