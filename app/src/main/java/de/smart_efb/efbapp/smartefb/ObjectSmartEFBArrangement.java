package de.smart_efb.efbapp.smartefb;

public class ObjectSmartEFBArrangement {

    private Integer rowID;
    private String arrangement;
    private String authorName;
    private String blockid;
    private String changeTo;
    private Long arrangementWriteTime;
    private Long arrangementSketchWriteTime;
    private Long lastEvalTime;
    private Integer sketchArrangement;
    private Integer evaluatePossible;
    private Integer newEntry;
    private Integer serverIdArrangement;
    private Integer status;
    private Integer positionNumber;

    // constructor for object now arrangement and sketch arrangement
    public ObjectSmartEFBArrangement (Integer rowID, String arrangement, String authorName, String blockid, String changeTo, Long arrangementWriteTime, Long arrangementSketchWriteTime, Long lastEvalTime, Integer sketchArrangement, Integer evaluatePossible, Integer newEntry, Integer serverIdArrangement, Integer status, Integer positionNumber) {

        // object for now arrangement and sketch arrangement

        this.rowID = rowID;
        this.arrangement = arrangement;
        this.authorName = authorName;
        this.blockid = blockid;
        this.changeTo = changeTo;
        this.arrangementWriteTime = arrangementWriteTime;
        this.arrangementSketchWriteTime = arrangementSketchWriteTime;
        this.lastEvalTime = lastEvalTime;
        this.sketchArrangement = sketchArrangement;
        this.evaluatePossible = evaluatePossible;
        this.newEntry = newEntry;
        this.serverIdArrangement = serverIdArrangement;
        this.status = status;
        this.positionNumber = positionNumber;

    }


    public String getAuthorName () {
        return authorName;
    }

    public Long getArrangementWriteTime () {
        return arrangementWriteTime;
    }

    public Long getArrangementSketchWriteTime () {
        return arrangementSketchWriteTime;
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

    public String getArrangementText () {
        return arrangement;
    }

    public Integer getPositionNumber () {return positionNumber;}

    public Integer getServerIdArrangement () {return serverIdArrangement;}

    public Integer getEvaluatePossible () {return evaluatePossible;}

    public Long getLastEvalTime () {return lastEvalTime;}

    public Integer getSketchArrangement () {return sketchArrangement;}

}
