
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

    // -------------------
    // Student actions (delegate to mediator via state)
    // -------------------
    public void enrollIn(Course c) {
        if (c == null) return;
        if (!c.isVisibleToStudents()) {
            System.out.println("Course " + c.code + " is not available for student enrollment.");
            return;
        }
        c.getState().tryEnroll(this); // state will use mediator
    }

    public void waitlistFor(Course c) {
        if (c == null) return;
        if (!c.isVisibleToStudents()) {
            System.out.println("Course " + c.code + " is not available.");
            return;
        }
        c.getState().addToWaitlist(this); // state will use mediator
    }

    public void dropCourse(Course c) {
        if (c == null) return;
        c.getState().dropStudent(this); // state will use mediator
    }

    // -------------------
    // Called by mediator to update student's lists
    // -------------------
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

    // -------------------
    // UI
    // -------------------
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
