import java.util.Scanner;

public interface CourseState {
    public boolean tryEnroll(Student student);
    public boolean addToWaitlist(Student student);
    public boolean dropStudent(Student student);
    public void setCapacity(int newCapacity);
    public void setStatusAdmin(CourseStatus newStatus);
    public void setStatusAdminInteractive(CourseStatus newStatus, Scanner scanner);
}
