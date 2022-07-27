package de.smart_efb.efbapp.smartefb;

public class ObjectSmartEFBGoals {

    private final Integer rowID;
    private final String goal;
    private final String authorName;
    private final String blockid;
    private final String changeTo;
    private final Long jointlyGoalWriteTime;
    private final Long debetableGoalWriteTime;
    private final Long lastEvalTime;
    private final Integer debetableGoal;
    private final Integer evaluatePossible;
    private final Integer newEntry;
    private final Integer serverIdGoal;
    private final Integer status;
    private final Integer positionNumber;

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

