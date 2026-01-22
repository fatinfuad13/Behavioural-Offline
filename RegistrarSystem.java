import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
// basically maps student and course IDs to their respective objects
// can add new student and new course through this and also retrieve them if given their ID 
// but this does not handle the core logic of this app
public class RegistrarSystem {
    private final Map<String, Student> students = new HashMap<>(); // 101 -> {101,Fatin,{enrolledCourses},{waitlistedCourses}}
    private final Map<String, Course> courses = new HashMap<>(); // 214 -> {code,title,capacity,{enrolled},{waitlisted},CourseStatus}

    public void addStudent(Student s) {
        if (s != null) students.put(s.id, s);
    }

    public void addCourse(Course c) {
        if (c != null) courses.put(c.code, c);
    }

    public Student getStudent(String id) {
        return students.get(id);
    }

    public Course getCourse(String code) {
        return courses.get(code);
    }

    public Collection<Student> getAllStudents() {
        return students.values();
    }

    public Collection<Course> getAllCourses() {
        return courses.values();
    }
}
