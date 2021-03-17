public class Course {
    private int courseID;
    private String name;
    private char term;
    private boolean allowAnonymous = false;

    public Course(int courseID, String name, char term, boolean allowAnonymous) {
        this.courseID = courseID;
        this.name = name;
        this.term = term;
        this.allowAnonymous = allowAnonymous;
    }

    public String toString() {
        return this.name + ": " + this.term + ", Allow anonymous? " + (this.allowAnonymous ?  "yes" : "no");
    }

}
