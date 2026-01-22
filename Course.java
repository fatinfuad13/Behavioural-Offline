/*import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Course {
    public final String code;
    public final String title;
    private int capacity; // Probably use a setter to change instead of changing visibility
    public CourseStatus status;
    private final List<Student> enrolled = new ArrayList<>();
    private final LinkedList<Student> waitlist = new LinkedList<>();

    public Course(String code, String title, int capacity, CourseStatus status) {
        this.code = code;
        this.title = title;
        this.capacity = Math.max(0, capacity);
        this.status = status;
    }

    public boolean isVisibleToStudents() {
        return status != CourseStatus.DRAFT && status != CourseStatus.CANCELLED;
    }

    public boolean tryEnroll(Student s) {
        if (s == null)
            return false;
        switch (status) {
            case OPEN:
                if (enrolled.contains(s)) {
                    System.out.println("Already enrolled: " + s.name + " in " + code);
                    return true; // no-op
                }
                if (enrolled.size() < capacity) {
                    enrolled.add(s);
                    s.addEnrolledCourseDirect(this);
                    System.out.println("Enrolled: " + s.name + " in " + code);
                    if (enrolled.size() >= capacity) {
                        status = CourseStatus.FULL;
                        System.out.println(code + " is now FULL.");
                    }
                    return true;
                } else {
                    // capacity reached while OPEN -> FULL and suggest waitlist
                    status = CourseStatus.FULL;
                    System.out.println(code + " reached capacity; status set to FULL. Try waitlisting.");
                    return false;
                }
            case FULL:
                System.out.println("Cannot enroll; course is FULL. You may waitlist: " + code);
                return false;
            case CLOSED:
                System.out.println("Cannot enroll; course is CLOSED: " + code);
                return false;
            case DRAFT:
                System.out.println("Cannot enroll; course is DRAFT (not visible): " + code);
                return false;
            case CANCELLED:
                System.out.println("Cannot enroll; course is CANCELLED: " + code);
                return false;
            default:
                return false;
        }
    }

    public boolean addToWaitlist(Student s) {
        if (s == null)
            return false;
        switch (status) {
            case FULL:
                if (enrolled.contains(s)) {
                    System.out.println("Already enrolled; no need to waitlist: " + s.name + " for " + code);
                    return false;
                }
                if (waitlist.contains(s)) {
                    System.out.println("Already waitlisted: " + s.name + " for " + code);
                    return false;
                }
                waitlist.add(s);
                s.addWaitlistCourseDirect(this);
                System.out.println("Waitlisted: " + s.name + " for " + code);
                return true;
            case OPEN:
                System.out.println("Course is OPEN; try enrolling instead: " + code);
                return false;
            case CLOSED:
            case DRAFT:
            case CANCELLED:
                System.out.println("Cannot waitlist; course not accepting waitlist: " + code);
                return false;
            default:
                return false;
        }
    }

    public boolean dropStudent(Student s) {
        if (s == null)
            return false;
        boolean changed = false;
        if (enrolled.contains(s)) {
            enrolled.remove(s);
            s.removeCourseDirect(this);
            System.out.println("Dropped from enrolled: " + s.name + " from " + code);
            changed = true;

            // After drop: maybe promote one from waitlist if space
            if (status != CourseStatus.CANCELLED && status != CourseStatus.DRAFT) {
                if (enrolled.size() < capacity) {
                    if (!waitlist.isEmpty()) {
                        Student promoted = waitlist.poll();
                        if (promoted != null) {
                            enrolled.add(promoted);
                            promoted.addEnrolledCourseDirect(this);
                            promoted.removeCourseDirect(this); // ensure waitlist removed too
                            System.out.println("Promoted from waitlist: " + promoted.name + " into " + code);
                        }
                    }
                    // Recalculate status: FULL -> OPEN if space
                    if (status == CourseStatus.FULL && enrolled.size() < capacity) {
                        status = CourseStatus.OPEN;
                        System.out.println(code + " status changed to OPEN due to available capacity.");
                    }
                    if (enrolled.size() >= capacity) {
                        status = CourseStatus.FULL;
                    }
                }
            }
        } else if (waitlist.contains(s)) {
            waitlist.remove(s);
            s.removeCourseDirect(this);
            System.out.println("Removed from waitlist: " + s.name + " for " + code);
            changed = true;
        } else {
            System.out.println(s.name + " is neither enrolled nor waitlisted for " + code);
        }
        return changed;
    }

    public void setCapacity(int newCapacity) {
        if (newCapacity < 0)
            newCapacity = 0;
        System.out.println("Setting capacity of " + code + " to " + newCapacity);
        this.capacity = newCapacity;
        if (status == CourseStatus.CANCELLED) {
            System.out.println("Course is CANCELLED; capacity change has no effect.");
            return;
        }
        // Adjust status based on capacity and current enrolled
        if (enrolled.size() < capacity) {
            if (status != CourseStatus.DRAFT) {
                status = CourseStatus.OPEN;
                System.out.println(code + " status changed to OPEN (capacity allows enrollment).");
            }
        } else if (enrolled.size() == capacity) {
            status = CourseStatus.FULL;
            System.out.println(code + " status changed to FULL (at capacity).");
        } else {
            // more enrolled than capacity: keep FULL, admin must resolve
            status = CourseStatus.FULL;
            System.out.println(code + " over capacity; remains FULL.");
        }
    }

    public void setStatusAdmin(CourseStatus newStatus) {
        if (newStatus == null)
            return;
        if (newStatus == status) {
            System.out.println("No change: " + code + " already " + status);
            return;
        }

        // Enforce transition rules with enhanced flexibility
        switch (status) {
            case DRAFT:
                if (newStatus == CourseStatus.OPEN) {
                    status = CourseStatus.OPEN;
                    System.out.println(code + " transitioned DRAFT -> OPEN");
                } else if (newStatus == CourseStatus.CLOSED) {
                    status = CourseStatus.CLOSED;
                    System.out.println(code + " transitioned DRAFT -> CLOSED");
                } else if (newStatus == CourseStatus.CANCELLED) {
                    cancelCourse();
                } else {
                    System.out.println("Invalid transition from DRAFT to " + newStatus);
                }
                break;
            case OPEN:
                if (newStatus == CourseStatus.CLOSED) {
                    status = CourseStatus.CLOSED;
                    System.out.println(code + " transitioned OPEN -> CLOSED");
                } else if (newStatus == CourseStatus.DRAFT) {
                    status = CourseStatus.DRAFT;
                    System.out.println(code + " transitioned OPEN -> DRAFT");
                } else if (newStatus == CourseStatus.CANCELLED) {
                    cancelCourse();
                } else {
                    System.out.println("Invalid transition from OPEN to " + newStatus);
                }
                break;
            case FULL:
                if (newStatus == CourseStatus.CLOSED) {
                    // When closing from FULL, close without prompting (for non-interactive
                    // scenarios)
                    closeWithRandomWaitlistSelection(capacity);
                } else if (newStatus == CourseStatus.CANCELLED) {
                    cancelCourse();
                } else {
                    System.out.println(
                            "Invalid transition from FULL to " + newStatus + " (FULL->OPEN is automatic on drop)");
                }
                break;
            case CLOSED:
                if (newStatus == CourseStatus.OPEN) {
                    status = CourseStatus.OPEN;
                    System.out.println(code + " transitioned CLOSED -> OPEN");
                } else if (newStatus == CourseStatus.DRAFT) {
                    status = CourseStatus.DRAFT;
                    System.out.println(code + " transitioned CLOSED -> DRAFT");
                } else if (newStatus == CourseStatus.CANCELLED) {
                    cancelCourse();
                } else {
                    System.out.println("Invalid transition from CLOSED to " + newStatus);
                }
                break;
            case CANCELLED:
                // CANCELLED can now transition back to DRAFT
                if (newStatus == CourseStatus.DRAFT) {
                    status = CourseStatus.DRAFT;
                    System.out.println(code + " transitioned CANCELLED -> DRAFT (reinstating course)");
                } else {
                    System.out.println("Invalid: CANCELLED can only transition to DRAFT for " + code);
                }
                break;
            default:
                System.out.println("Unknown current status.");
        }
    }

    // Interactive version for admin with Scanner (prompts for capacity increase)
    public void setStatusAdminInteractive(CourseStatus newStatus, Scanner scanner) {
        if (newStatus == null)
            return;
        if (newStatus == status) {
            System.out.println("No change: " + code + " already " + status);
            return;
        }

        // Special handling for FULL -> CLOSED with interactive prompt
        if (status == CourseStatus.FULL && newStatus == CourseStatus.CLOSED) {
            if (!waitlist.isEmpty()) {
                System.out.println(code + " has " + waitlist.size() + " student(s) on waitlist.");
                System.out.print(
                        "Do you want to increase capacity before closing? (Enter new capacity, or 0 to not increase): ");
                try {
                    int newCapacity = Integer.parseInt(scanner.nextLine().trim());
                    if (newCapacity > 0) {
                        if (newCapacity > capacity) {
                            capacity = newCapacity;
                            System.out.println("Capacity increased to " + newCapacity);
                            closeWithRandomWaitlistSelection(newCapacity);
                        } else {
                            System.out.println("New capacity must be greater than current capacity (" + capacity
                                    + "). No change.");
                            closeWithRandomWaitlistSelection(capacity);
                        }
                    } else {
                        System.out.println("No capacity increase.");
                        closeWithRandomWaitlistSelection(capacity);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Closing without capacity increase.");
                    closeWithRandomWaitlistSelection(capacity);
                }
            } else {
                // No waitlist, just close
                closeWithRandomWaitlistSelection(capacity);
            }
            return;
        }

        // For all other transitions, use the standard method
        setStatusAdmin(newStatus);
    }

    private void cancelCourse() {
        status = CourseStatus.CANCELLED;
        // clear enrolled and waitlist, update students
        for (Student s : new ArrayList<>(enrolled)) {
            s.removeCourseDirect(this);
        }
        for (Student s : new ArrayList<>(waitlist)) {
            s.removeCourseDirect(this);
        }
        enrolled.clear();
        waitlist.clear();
        System.out.println(code + " has been CANCELLED. All students dropped and waitlist cleared.");
    }

    private void closeWithRandomWaitlistSelection(int targetCapacity) {
        status = CourseStatus.CLOSED;
        System.out.println(code + " transitioned FULL -> CLOSED");

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
                    waitlist.remove(promoted);
                    enrolled.add(promoted);
                    promoted.addEnrolledCourseDirect(this);
                    System.out.println("  Randomly selected: " + promoted.name + " for " + code);
                }
            }
        }
    }

    public void printRoster() {
        System.out.println("Roster for " + code + " - " + title + " (" + status + ", cap=" + capacity + "):");
        if (enrolled.isEmpty()) {
            System.out.println("  [no enrolled]");
        } else {
            for (Student s : enrolled) {
                System.out.println("  " + s.id + " - " + s.name);
            }
        }
    }

    public void printWaitlist() {
        System.out.println("Waitlist for " + code + ":");
        if (waitlist.isEmpty()) {
            System.out.println("  [no waitlisted]");
        } else {
            for (Student s : waitlist) {
                System.out.println("  " + s.id + " - " + s.name);
            }
        }
    }

    // Exposed getters for UI/reporting
    public int getCapacity() {
        return capacity;
    }

    public int getEnrolledCount() {
        return enrolled.size();
    }

    public int getWaitlistCount() {
        return waitlist.size();
    }

    boolean hasStudentEnrolled(Student s) {
        return enrolled.contains(s);
    }

    boolean hasStudentWaitlisted(Student s) {
        return waitlist.contains(s);
    }

    boolean hasSeatAvailable() {
        return enrolled.size() < capacity;
    }

}*/

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Course {
    // Immutable info - safe to keep public
    public final String code;
    public final String title;

    // Mutable data
    private int capacity;
    public CourseStatus status;

    // Internal lists
    private final List<Student> enrolled = new ArrayList<>();
    private final LinkedList<Student> waitlist = new LinkedList<>();

    // Current state
    private CourseState state;

    public Course(String code, String title, int capacity, CourseStatus status) {
        this.code = code;
        this.title = title;
        this.capacity = Math.max(0, capacity);
        this.status = status;
        this.state = createStateFromStatus(status);
    }
    // ---------------------------
// VISIBILITY QUERY
// ---------------------------
public boolean isVisibleToStudents() {
    return status != CourseStatus.DRAFT && status != CourseStatus.CANCELLED;
}


    // ---------------------------
    // Delegate public API to state
    // ---------------------------
    public boolean tryEnroll(Student s) {
        return state.tryEnroll(s);
    }

    public boolean addToWaitlist(Student s) {
        return state.addToWaitlist(s);
    }

    public boolean dropStudent(Student s) {
        return state.dropStudent(s);
    }

    public void setCapacity(int newCapacity) {
        state.setCapacity(newCapacity);
    }

    public void setStatusAdmin(CourseStatus newStatus) {
        state.setStatusAdmin(newStatus);
    }

    public void setStatusAdminInteractive(CourseStatus newStatus, Scanner scanner) {
        state.setStatusAdminInteractive(newStatus, scanner);
    }

    // ---------------------------
    // State transition helper
    // ---------------------------
    void transitionTo(CourseStatus newStatus) {
        this.status = newStatus;
        this.state = createStateFromStatus(newStatus);
        System.out.println(code + " transitioned to " + newStatus);
    }

    // ---------------------------
    // Factory for states
    // ---------------------------
    private CourseState createStateFromStatus(CourseStatus status) {
        switch (status) {
            case DRAFT: return new DraftState(this);
            case OPEN: return new OpenState(this);
            case FULL: return new FullState(this);
            case CLOSED: return new ClosedState(this);
            case CANCELLED: return new CancelledState(this);
            default: throw new IllegalStateException("Unknown status: " + status);
        }
    }

    // ---------------------------
    // INTERNAL METHODS for states
    // ---------------------------
    void enrollStudentInternal(Student s) {
        enrolled.add(s);
        s.addEnrolledCourseDirect(this);
        System.out.println("Enrolled: " + s.name + " in " + code);
    }

    void waitlistStudentInternal(Student s) {
        waitlist.add(s);
        s.addWaitlistCourseDirect(this);
        System.out.println("Waitlisted: " + s.name + " for " + code);
    }

    boolean dropStudentInternal(Student s) {
        boolean changed = false;

        if (enrolled.contains(s)) {
            enrolled.remove(s);
            s.removeCourseDirect(this);
            System.out.println("Dropped from enrolled: " + s.name + " from " + code);
            changed = true;

            // Promote first student from waitlist if space available
            if (!waitlist.isEmpty() && enrolled.size() < capacity) {
                Student promoted = waitlist.poll();
                enrolled.add(promoted);
                promoted.addEnrolledCourseDirect(this);
                promoted.removeCourseDirect(this); // remove from waitlist
                System.out.println("Promoted from waitlist: " + promoted.name + " into " + code);
            }

            // Recalculate status
            if (enrolled.size() < capacity && status == CourseStatus.FULL) {
                transitionTo(CourseStatus.OPEN);
            }

        } else if (waitlist.contains(s)) {
            waitlist.remove(s);
            s.removeCourseDirect(this);
            System.out.println("Removed from waitlist: " + s.name + " for " + code);
            changed = true;
        } else {
            System.out.println(s.name + " is neither enrolled nor waitlisted for " + code);
        }

        return changed;
    }

    void setCapacityInternal(int newCapacity) {
        if (newCapacity < 0) newCapacity = 0;
        System.out.println("Setting capacity of " + code + " to " + newCapacity);
        this.capacity = newCapacity;

        if (status == CourseStatus.CANCELLED) {
            System.out.println("Course is CANCELLED; capacity change has no effect.");
            return;
        }

        // Adjust status based on capacity and enrolled count
        if (enrolled.size() < capacity && status != CourseStatus.DRAFT) {
            transitionTo(CourseStatus.OPEN);
        } else if (enrolled.size() >= capacity) {
            transitionTo(CourseStatus.FULL);
        }
    }

    void cancelCourseInternal() {
        status = CourseStatus.CANCELLED;
        for (Student s : new ArrayList<>(enrolled)) s.removeCourseDirect(this);
        for (Student s : new ArrayList<>(waitlist)) s.removeCourseDirect(this);
        enrolled.clear();
        waitlist.clear();
        System.out.println(code + " has been CANCELLED. All students dropped and waitlist cleared.");
    }

    void closeWithRandomWaitlistSelectionInternal(int targetCapacity) {
        status = CourseStatus.CLOSED;
        System.out.println(code + " transitioned FULL -> CLOSED");

        if (!waitlist.isEmpty()) {
            int availableSlots = targetCapacity - enrolled.size();
            if (availableSlots > 0) {
                Random random = new Random();
                List<Student> copy = new ArrayList<>(waitlist);
                int promoteCount = Math.min(availableSlots, copy.size());

                System.out.println("Randomly selecting " + promoteCount + " student(s) from waitlist:");
                for (int i = 0; i < promoteCount; i++) {
                    int idx = random.nextInt(copy.size());
                    Student s = copy.remove(idx);
                    waitlist.remove(s);
                    enrolled.add(s);
                    s.addEnrolledCourseDirect(this);
                    System.out.println("  Randomly selected: " + s.name + " for " + code);
                }
            }
        }
    }

    void handleFullToClosedInteractive(CourseStatus newStatus, Scanner scanner) {
        if (newStatus != CourseStatus.CLOSED) return;

        if (!waitlist.isEmpty()) {
            System.out.println(code + " has " + waitlist.size() + " student(s) on waitlist.");
            System.out.print("Enter new capacity, or 0 to not increase: ");
            try {
                int newCapacity = Integer.parseInt(scanner.nextLine().trim());
                if (newCapacity > capacity) {
                    capacity = newCapacity;
                    System.out.println("Capacity increased to " + newCapacity);
                    closeWithRandomWaitlistSelectionInternal(newCapacity);
                } else {
                    closeWithRandomWaitlistSelectionInternal(capacity);
                }
            } catch (NumberFormatException e) {
                closeWithRandomWaitlistSelectionInternal(capacity);
            }
        } else {
            closeWithRandomWaitlistSelectionInternal(capacity);
        }
    }

    // ---------------------------
    // UTILITY METHODS
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

    // ---------------------------
    // GETTERS
    // ---------------------------
    public int getCapacity() { return capacity; }
    public int getEnrolledCount() { return enrolled.size(); }
    public int getWaitlistCount() { return waitlist.size(); }

    public boolean hasSeatAvailable() { return enrolled.size() < capacity; }
    public boolean hasStudentEnrolled(Student s) { return enrolled.contains(s); }
    public boolean hasStudentWaitlisted(Student s) { return waitlist.contains(s); }
}

