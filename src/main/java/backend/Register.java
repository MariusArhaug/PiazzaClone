package backend;

import types.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

//Let users register to the database, hashPasswords, and register users to different courses.
public class Register extends DBConnect {

    private PreparedStatement regStatement;

    private final Login login = new Login();
    private final View view = new View();

    public Register() {
        super.connect();
    }

    /**
     * Hash password
     * @param passwordToHash cleartext password
     * @return hashed password
     */
    public static String hashPassword(String passwordToHash) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(passwordToHash.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    /**
     * Interface for user form
     * @return types.User object.
     */
    public User registerUser() {
        while (true) {
            Scanner in = new Scanner(System.in);
            System.out.println("Name: ");
            String name = in.nextLine();
            System.out.println("Email: ");
            String email = in.nextLine();
            System.out.println("Password: ");
            String password = Register.hashPassword(in.nextLine());
            System.out.println("Instructor: (y/n)");
            boolean isInstructor = in.nextLine().equalsIgnoreCase("y");
            if (name.equals("") || email.equals("") || password.equals("")) {
                System.out.println("You must fill out every field! Name, email and password!");
                continue;
            }
            User user = this.insertUser(name, email, password, isInstructor);
            if (user == null) {System.out.println("An error has occurred! Please try again!"); continue;}
            return user;
        }
    }

    /**
     * Insert new user into database
     * @param name username
     * @param email user email
     * @param password password
     * @param isInstructor student/instructor
     * @return types.User object.
     */
    private User insertUser(String name, String email, String password, boolean isInstructor) {
        User existingUser = this.login.getUser(email, password);

        if (existingUser != null) {
            System.out.println("This user already exists, please choose another email");
            return null;
        }
        try {
            String SQL = "INSERT INTO users (name, email, password, isInstructor) " +
                    "VALUES ((?), (?), (?), (?))";

            this.regStatement = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);

            this.regStatement.setString(1, name);
            this.regStatement.setString(2, email);
            this.regStatement.setString(3, password);
            this.regStatement.setBoolean(4, isInstructor);
            this.regStatement.executeUpdate();
            ResultSet rs = this.regStatement.getGeneratedKeys();
            int userID = 0;
            if (rs.next()) {
                userID = Math.toIntExact(rs.getLong(1));
            }
            return new User(userID, name, email, password, isInstructor);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Interface to register user to course
     * @param courseID ID of course
     */
    public void registerUserToCourse(int courseID) {
        Scanner in = new Scanner(System.in);
        List<User> users = this.view.viewUsersNotInCourse(courseID);

        if (users.isEmpty()) {
            System.out.println("It appears that every registered user in Piazza is registered for this course!");
            return;
        }
        System.out.println("Students: " + users
                .stream()
                .map(User::toString)
                .collect(Collectors.joining("\n")));
        int userID;
        while (true) {
            System.out.println("Select an studentID you want to invite to this course:");
            System.out.println("Student ID: [" + users
                    .stream()
                    .map(e -> Integer.toString(e.getUserID()))
                    .collect(Collectors.joining(", ")) + "]");
            userID = Integer.parseInt(in.nextLine());
            int finalUserID = userID;
            if (users.stream().anyMatch(e -> e.getUserID() == finalUserID)) break;
            System.out.println("You must choose a valid user ID!");
        }
        int finalUserID1 = userID;
        System.out.println("User" + users.stream()
                .filter(e -> e.getUserID() == finalUserID1)
                .map(Object::toString)
                .collect(Collectors.joining("")) + " has been invited!");

        this.insertUserToCourse(userID, courseID);
    }

    /**
     * Insert user and course into userCourse table in database
     * @param userID ID of user
     * @param courseID ID of course.
     */
    private void insertUserToCourse(int userID, int courseID) {
        try {
            String SQL = "INSERT INTO userCourse (userID, courseID) " +
                    "VALUES ((?), (?))";

            this.regStatement = conn.prepareStatement(SQL);
            this.regStatement.setInt(1, userID);
            this.regStatement.setInt(2, courseID);
            this.regStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
