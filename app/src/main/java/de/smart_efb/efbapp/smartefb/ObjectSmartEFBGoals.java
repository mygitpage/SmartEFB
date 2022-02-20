package de.smart_efb.efbapp.smartefb;

public class ObjectSmartEFBGoals {

    private Integer rowID;
    private String goal;
    private String authorName;
    private String blockid;
    private String changeTo;
    private Long jointlyGoalWriteTime;
    private Long debetableGoalWriteTime;
    private Long lastEvalTime;
    private Integer debetableGoal;
    private Integer evaluatePossible;
    private Integer newEntry;
    private Integer serverIdGoal;
    private Integer status;
    private Integer positionNumber;

    // constructor for object jointly goal and debetable goal
    public ObjectSmartEFBGoals (Integer rowID, String goal, String authorName, String blockid, String changeTo, Long jointlyGoalWriteTime, Long debetableGoalWriteTime, Long lastEvalTime, Integer debetableGoal, Integer evaluatePossible, Integer newEntry, Integer serverIdGoal, Integer status, Integer positionNumber) {

        // object for now arrangement and sketch arrangement

        this.rowID = rowID;
        this.goal = goal;
        this.authorName = authorName;
        this.blockid = blockid;
        this.changeTo = changeTo;
        this.jointlyGoalWriteTime = jointlyGoalWriteTime;
        this.debetableGoalWriteTime = debetableGoalWriteTime;
        this.lastEvalTime = lastEvalTime;
        this.debetableGoal = debetableGoal;
        this.evaluatePossible = evaluatePossible;
        this.newEntry = newEntry;
        this.serverIdGoal = serverIdGoal;
        this.status = status;
        this.positionNumber = positionNumber;

    }


    public String getAuthorName () {
        return authorName;
    }

    public Long getJointlyGoalWriteTime () {
        return jointlyGoalWriteTime;
    }

    public Long getDebetableGoalWriteTime () {
        return debetableGoalWriteTime;
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

    public String getGoalText () {
        return goal;
    }

    public Integer getPositionNumber () {return positionNumber;}

    public Integer getServerIdGoal () {return serverIdGoal;}

    public Integer getEvaluatePossible () {return evaluatePossible;}

    public Long getLastEvalTime () {return lastEvalTime;}

    public Integer getDebetableGoal () {return debetableGoal;}

}

