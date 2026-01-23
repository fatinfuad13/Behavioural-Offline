

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
public class Course {
    // Immutable info
    public final String code;
    public final String title;

    // Mutable data
    public int capacity; ///////
    public CourseStatus status; ///////
    private CourseState state;

    // Student tracking (lists managed via mediator)
    private final List<Student> enrolled = new ArrayList<>();
    private final LinkedList<Student> waitlist = new LinkedList<>();

    public Course(String code, String title, int capacity, CourseStatus status) {
        this.code = code;
        this.title = title;
        this.capacity = Math.max(0, capacity);
        this.status = status;
        syncStateWithStatus();
    }

    // ---------------------------
    // STATE MANAGEMENT
    // ---------------------------
    private void syncStateWithStatus() {
    switch (status) {
        case DRAFT:
            setState(new DraftState(this));
            break;
        case OPEN:
            setState(new OpenState(this));
            break;
        case FULL:
            setState(new FullState(this));
            break;
        case CLOSED:
            setState(new ClosedState(this));
            break;
        case CANCELLED:
            setState(new CancelledState(this));
            break;
    }
}

    public void setState(CourseState state) {
        this.state = state;
    }

    public CourseState getState() {
        return state;
    }

    public void setStatus(CourseStatus newStatus) {
    CourseStatus oldStatus = this.status;
    
    // skip if status isn't actually changing
    if (oldStatus == newStatus) return;

    this.status = newStatus;
    syncStateWithStatus();

    // Only print manual/admin transitions
    // Skip automatic OPEN->FULL and any redundant FULL->FULL
    if (!(oldStatus == CourseStatus.OPEN && newStatus == CourseStatus.FULL)  // auto full
        && !(oldStatus == CourseStatus.FULL && newStatus == CourseStatus.FULL)) { // redundant
        System.out.println(code + " transitioned " + oldStatus + " -> " + newStatus);
    }
}


    public CourseStatus getStatus() {
        return status;
    }

    // ---------------------------
    // CAPACITY & VISIBILITY
    // ---------------------------
    public void setCapacity(int newCapacity) {
   
    if (newCapacity < 0) newCapacity = 0;
        System.out.println("Setting capacity of " + code + " to " + newCapacity);
        this.capacity = newCapacity;
        state.setCapacity(newCapacity);
}


    public int getCapacity() { return capacity; }
    public boolean hasSpace() { return enrolled.size() < capacity; }
    public boolean isVisibleToStudents() {
        return status != CourseStatus.DRAFT && status != CourseStatus.CANCELLED;
    }

    // ---------------------------
    // ENROLLED / WAITLIST GETTERS
    // ---------------------------
    public List<Student> getEnrolled() { return enrolled; }
    public LinkedList<Student> getWaitlist() { return waitlist; }
    public int getEnrolledCount() { return enrolled.size(); }
    public int getWaitlistCount() { return waitlist.size(); }

    // ---------------------------
    // PUBLIC API (delegate to state)
    // ---------------------------
    public boolean tryEnroll(Student s) { return state.tryEnroll(s); }
    public boolean addToWaitlist(Student s) { return state.addToWaitlist(s); }
    public boolean dropStudent(Student s) { return state.dropStudent(s); }

    public void setStatusAdmin(CourseStatus newStatus) { state.setStatusAdmin(newStatus); }
    public void setStatusAdminInteractive(CourseStatus newStatus, Scanner sc) {
        state.setStatusAdminInteractive(newStatus, sc);
    }

    // ---------------------------
    // RANDOM PROMOTION (mediator)
    // ---------------------------
    public void closeWithRandomWaitlistSelection(int targetCapacity) {
        setStatus(CourseStatus.CLOSED);
        System.out.println(code + " transitioned to CLOSED");
        RegistrarMediator.getInstance().promoteFromWaitlistRandomly(this, targetCapacity);
    }

    // ---------------------------
    // REPORTING
    // ---------------------------
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
