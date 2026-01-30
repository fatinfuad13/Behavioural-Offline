
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
public class Course {
    public final String code;
    public final String title;
    private int capacity; ///////
    public CourseStatus status;
    private CourseState state;

    
    private final List<Student> enrolled = new ArrayList<>();
    private final LinkedList<Student> waitlist = new LinkedList<>();

    public Course(String code, String title, int capacity, CourseStatus status) {
    this.code = code;
    this.title = title;
    this.capacity = Math.max(0, capacity);

    this.status = status;

   switch (status) {
    case DRAFT:
        this.state = new DraftState(this);
        break;
    case OPEN:
        this.state = new OpenState(this);
        break;
    case FULL:
        this.state = new FullState(this);
        break;
    case CLOSED:
        this.state = new ClosedState(this);
        break;
    case CANCELLED:
        this.state = new CancelledState(this);
        break;
}

}

    public void setState(CourseState state) {
    this.state = state;
   
    this.setStatus(state.getStatus());
}

    public CourseState getState() {
        return state;
    }

   public void setStatus(CourseStatus newStatus) {

   
   

    CourseStatus oldStatus = this.status;

    if (oldStatus == newStatus) return;

    this.status = newStatus;   // mirror only

    if (newStatus == CourseStatus.CANCELLED)
        return;

    // Special case: FULL -> OPEN via capacity change
    if (oldStatus == CourseStatus.FULL && newStatus == CourseStatus.OPEN) {
        System.out.println(code + " status changed to OPEN (capacity allows enrollment).");
        return;
    }

    // Special case: CANCELLED -> DRAFT (reinstating)
    if (oldStatus == CourseStatus.CANCELLED && newStatus == CourseStatus.DRAFT) {
        System.out.println(code + " transitioned CANCELLED -> DRAFT (reinstating course)");
        return;
    }

    // Print meaningful transitions (same logic you had)
    if (!(oldStatus == CourseStatus.OPEN && newStatus == CourseStatus.FULL)
        && !(oldStatus == CourseStatus.FULL && newStatus == CourseStatus.FULL)) {

        System.out.println(code + " transitioned " + oldStatus + " -> " + newStatus);
    }
}


    public CourseStatus getStatus() {
        return status;
    }

    public void setCapacity(int newCapacity) {
   
    if (newCapacity < 0) newCapacity = 0;
        System.out.println("Setting capacity of " + code + " to " + newCapacity);
        this.capacity = newCapacity;
        state.changedCapacity();
}


    public int getCapacity() { return capacity; }
    public boolean hasSpace() { return enrolled.size() < capacity; }
    public boolean isVisibleToStudents() {
        return status != CourseStatus.DRAFT && status != CourseStatus.CANCELLED;
    }

    public List<Student> getEnrolled() { return enrolled; }
    public LinkedList<Student> getWaitlist() { return waitlist; }
    public int getEnrolledCount() { return enrolled.size(); }
    public int getWaitlistCount() { return waitlist.size(); }

 
    public void setStatusAdmin(CourseStatus newStatus) { state.setStatusAdmin(newStatus); }
    public void setStatusAdminInteractive(CourseStatus newStatus, Scanner sc) {
        state.setStatusAdminInteractive(newStatus, sc);
    }

    
        public void closeWithRandomWaitlistSelection(int targetCapacity) {
    // First, transition the course status
    setStatus(CourseStatus.CLOSED);

    // If there are waitlisted students and space available, promote random students
    if (!waitlist.isEmpty()) {
        int availableSlots = targetCapacity - enrolled.size();
        if (availableSlots > 0) {
            Random random = new Random();
            List<Student> waitlistCopy = new ArrayList<>(waitlist);
            int promotionCount = Math.min(availableSlots, waitlistCopy.size());

            System.out.println("Randomly selecting " + promotionCount + " student(s) from waitlist:");

            for (int i = 0; i < promotionCount; i++) {
                int randomIndex = random.nextInt(waitlistCopy.size());
                Student promoted = waitlistCopy.remove(randomIndex);

                // Remove from original waitlist
                waitlist.remove(promoted);

                // Add to enrolled list
                enrolled.add(promoted);

                // Update the student object via mediator
                RegistrarMediator.getInstance().enrollStudent(this, promoted);

                System.out.println("  Randomly selected: " + promoted.name + " for " + code);
            }
        }
    }
}

    

    public void printRoster() {
        System.out.println("Roster for " + code + " - " + title + " (" + status + ", cap=" + capacity + "):");
        if (enrolled.isEmpty()) System.out.println("  [no enrolled]");
        else for (Student s : enrolled) System.out.println("  " + s.id + " - " + s.name);
    }

    public void printWaitlist() {
        System.out.println("Waitlist for " + code + ":");
        if (waitlist.isEmpty()) System.out.println("  [no waitlisted]");
        else for (Student s : waitlist) System.out.println("  " + s.id + " - " + s.name);
    }
}


