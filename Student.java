import java.util.ArrayList;
import java.util.List;

public class Student {
    public final String id;
    public final String name;
    private final List<Course> enrolledCourses = new ArrayList<>();
    private final List<Course> waitlistedCourses = new ArrayList<>();

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void enrollIn(Course c) {
        if (c == null) return;
        if (!c.isVisibleToStudents()) {
            System.out.println("Course " + c.code + " is not available for student enrollment.");
            return;
        }
        if (enrolledCourses.contains(c)) {
            System.out.println(name + " is already enrolled in " + c.code);
            return;
        }
        c.tryEnroll(this);
    }

    public void waitlistFor(Course c) {
        if (c == null) return;
        if (!c.isVisibleToStudents()) {
            System.out.println("Course " + c.code + " is not available.");
            return;
        }
        if (waitlistedCourses.contains(c)) {
            System.out.println(name + " is already waitlisted for " + c.code);
            return;
        }
        if (enrolledCourses.contains(c)) {
            System.out.println(name + " is already enrolled in " + c.code + "; cannot waitlist.");
            return;
        }
        c.addToWaitlist(this);
    }

    public void dropCourse(Course c) {
        if (c == null) return;
        c.dropStudent(this); 
    }

    // For Course to call directly:
    public void addEnrolledCourseDirect(Course c) {
        if (!enrolledCourses.contains(c)) {
            enrolledCourses.add(c);
        }
        waitlistedCourses.remove(c);
    }

    public void addWaitlistCourseDirect(Course c) {
        if (!waitlistedCourses.contains(c)) {
            waitlistedCourses.add(c);
        }
    }

    public void removeCourseDirect(Course c) {
        enrolledCourses.remove(c);
        waitlistedCourses.remove(c);
    }

    public void printSchedule() {
        System.out.println("Schedule for " + name + " (" + id + "):");
        System.out.println("  Enrolled:");
        if (enrolledCourses.isEmpty()) {
            System.out.println("    [none]");
        } else {
            for (Course c : enrolledCourses) {
                System.out.println("    " + c.code + " - " + c.title + " (" + c.status + ")");
            }
        }
        System.out.println("  Waitlisted:");
        if (waitlistedCourses.isEmpty()) {
            System.out.println("    [none]");
        } else {
            for (Course c : waitlistedCourses) {
                System.out.println("    " + c.code + " - " + c.title + " (" + c.status + ")");
            }
        }
    }
}
