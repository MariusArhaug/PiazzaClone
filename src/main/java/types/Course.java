package types;

import java.util.List;
import java.util.stream.Collectors;

/**
 * types.Course class that lets us store different data about a course.
 */
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

    public boolean allowAnonymous() {
        return allowAnonymous;
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

    public static void printCourses(List<Course> courses) {
        System.out.println(
                courses.stream()
                        .map(e -> "| " + e.getName() + "    ID: " + e.getCourseID())
                        .collect(Collectors.joining(" \n")) + " \n");
    }
}
