public class Course {
    private final int courseID;
    private final String name;
    private final char term;
    private final boolean allowAnonymous;

    //Constructor for course objects
    public Course(int courseID, String name, char term, boolean allowAnonymous) {
        this.courseID = courseID;
        this.name = name;
        this.term = term;
        this.allowAnonymous = allowAnonymous;
    }

    public int getCourseID() {
        return courseID;
    }

    public String getName() {
        return this.name;
    }


    public String toString() {
        return this.name + " : " + this.term;
    }

}
