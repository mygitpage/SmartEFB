package de.smart_efb.efbapp.smartefb;


public class ObjectSmartEFBComment {

    private String comment;
    private String authorName;
    private String blockid;
    private Long commentTime;
    private Long uploadTime;
    private Long localeTime;
    private Long arrangementTime;
    private Long currentDateOfArrangement;
    private Integer newEntry;
    private Integer serverIdComment;
    private Integer timerStatus;
    private Integer status;
    private Integer rowID;

    public ObjectSmartEFBComment (Integer rowID, String comment, String authorName, String blockid, Long commentTime, Long uploadTime, Long localeTime, Long arrangementTime, Long currentDateOfArrangement, Integer newEntry, Integer serverIdComment, Integer timerStatus, Integer status) {

        this.rowID = rowID;
        this.comment = comment;
        this.authorName = authorName;
        this.blockid = blockid;
        this.commentTime = commentTime;
        this.uploadTime= uploadTime;
        this.localeTime = localeTime;
        this.arrangementTime = arrangementTime;
        this.currentDateOfArrangement = currentDateOfArrangement;
        this.newEntry = newEntry;
        this.serverIdComment = serverIdComment;
        this.timerStatus = timerStatus;
        this.status = status;

    }


    // TODO: getter and setter function implement

    public String getAuthorName () {
        return authorName;
    }

    public Long getLocalTime () {
        return localeTime;
    }

    public Integer getStatus () {
        return status;
    }

    public Integer getNewEntry () {
        return newEntry;
    }

    public Integer getRowID () {
        return rowID;
    }

    public String getCommentText () {
        return comment;
    }

}
