package de.smart_efb.efbapp.smartefb;

public class ObjectSmartEFBGoalsComment {


    private final String comment;
    private final String authorName;
    private final String blockid;
    private final Long commentTime;
    private final Long uploadTime;
    private final Long localeTime;
    private final Long goalTime;
    private final Long currentDateOfGoal;
    private final Integer newEntry;
    private final Integer serverIdComment;
    private final Integer timerStatus;
    private final Integer status;
    private final Integer rowID;
    private final Integer question1;
    private final Integer question2;
    private final Integer question3;

    public ObjectSmartEFBGoalsComment (Integer rowID, String comment, String authorName, String blockid, Long commentTime, Long uploadTime, Long localeTime, Long goalTime, Long currentDateOfGoal, Integer newEntry, Integer serverIdComment, Integer timerStatus, Integer status, Integer question1, Integer question2, Integer question3) {

        this.rowID = rowID;
        this.comment = comment;
        this.authorName = authorName;
        this.blockid = blockid;
        this.commentTime = commentTime;
        this.uploadTime= uploadTime;
        this.localeTime = localeTime;
        this.goalTime = goalTime;
        this.currentDateOfGoal = currentDateOfGoal;
        this.newEntry = newEntry;
        this.serverIdComment = serverIdComment;
        this.timerStatus = timerStatus;
        this.status = status;
        this.question1 = question1;
        this.question2 = question2;
        this.question3 = question3;

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

    public Integer getCommentResultStructQuestion1 () {return question1;}

    public Long getCommentTime () {return commentTime;}

    public Integer getTimerStatus () {return timerStatus;}

}
