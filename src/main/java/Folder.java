public class Folder {

    private int folderID;
    private String name;
    private int courseID;
    private int superFolderID;

    public Folder(int folderID, String name, int courseID, int superFolderID) {
        this.folderID = folderID;
        this.name = name;
        this.courseID = courseID;
        this.superFolderID = superFolderID;
    }

    public int getFolderID() {
        return folderID;
    }

    public String getName() {
        return name;
    }
    public String toString() {
        return this.name + " ID: " + this.folderID;
    }
}
