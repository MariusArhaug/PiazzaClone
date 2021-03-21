
//Folder class that lets us store different data about a folder.
public class Folder {

    private final int folderID;
    private final String name;
    private final int courseID;
    private final int superFolderID;

    public Folder(int folderID, String name, int courseID, int superFolderID) {
        this.folderID = folderID;
        this.name = name;
        this.courseID = courseID;
        this.superFolderID = superFolderID;
    }

    public int getFolderID() {
        return folderID;
    }

    public String toString() {
        return this.name + " ID: " + this.folderID +
                (this.superFolderID != 0 ?  " Parent folder: " + this.superFolderID : "") ;
    }
}
