package fyi.sorenneedscoffee.derg_zero.moderation;

import fyi.sorenneedscoffee.derg_zero.moderation.db.tables.records.ModerationCasesRecord;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Warning {
    private int id;
    private String uId;
    private OffenseType offenseType;
    private String additionalComments;
    private Timestamp creationTime;

    public Warning(int id, String uId, OffenseType offenseType, String additionalComments, Timestamp creationTime) {
        this.id = id;
        this.uId = uId;
        this.offenseType = offenseType;
        this.additionalComments = additionalComments;
        this.creationTime = creationTime;
    }

    public Warning(ModerationCasesRecord record) {
        this.id = record.getId();
        this.uId = record.getUserId();
        this.offenseType = OffenseType.getTypeById(record.getOffenseId());
        this.additionalComments = record.getAdditionalComments();
        this.creationTime = record.getCreationTime();
    }

    public Warning() {
        this.id = -1;
        this.uId = "";
        this.offenseType = null;
        this.additionalComments = "";
        this.creationTime = Timestamp.valueOf(LocalDateTime.MIN);
    }

    public int getId() {
        return id;
    }

    public String getuId() {
        return uId;
    }

    public OffenseType getOffenseType() {
        return offenseType;
    }

    public String getAdditionalComments() {
        return additionalComments;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean equals(Object obj) {
        if(!obj.getClass().equals(Warning.class))
            return false;

        return ((Warning) obj).getId() == this.id;
    }
}
