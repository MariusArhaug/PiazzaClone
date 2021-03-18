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

    public int getCourseID() {
        return courseID;
    }

    public String getName() {
        return this.name;
    }

    public char getTerm() {
        return term;
    }

    public String toString() {
        return this.name + " : " + this.term;
    }

}
