package view;

import com.prosecution.ucm.utils.ucm.UCMDocument;

import java.util.List;

import org.apache.myfaces.trinidad.model.UploadedFile;

public class UploadedFileInfo {
    String id;
    List<String> urls;
    String attachmentName;
    String userID;
    List<UCMDocument> docInfo;

    public UploadedFileInfo(String id, List<String> urls, String attachmentName, String userID,
                            List<UCMDocument> docInfo) {
        super();
        this.id = id;
        this.urls = urls;
        this.attachmentName = attachmentName;
        this.userID = userID;
        this.docInfo = docInfo;
    }

    public UploadedFileInfo(String id, String attachmentName, String userID) {
        super();
        this.id = id;
    
        this.attachmentName = attachmentName;
        this.userID = userID;
    }

    public UploadedFileInfo(String id, String attachmentName, String userID,
                            List<UCMDocument> docInfo) {
        super();
        this.id = id;
        this.attachmentName = attachmentName;
        this.userID = userID;
        this.docInfo = docInfo;
    }

    public void setDocInfo(List<UCMDocument> docInfo) {
        this.docInfo = docInfo;
    }

    public List<UCMDocument> getDocInfo() {
        return docInfo;
    }

    public UploadedFileInfo(String id, List<String> urls, String attachmentName, String userID) {
        super();
        this.id = id;
        this.urls = urls;
        this.attachmentName = attachmentName;
        this.userID = userID;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentName() {
        return attachmentName;
    }
    

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public UploadedFileInfo() {
        super();
    }
}
