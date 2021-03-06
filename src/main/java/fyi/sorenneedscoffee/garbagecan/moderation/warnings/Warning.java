package fyi.sorenneedscoffee.garbagecan.moderation.warnings;

import fyi.sorenneedscoffee.garbagecan.moderation.db.tables.records.ModerationCasesRecord;
import fyi.sorenneedscoffee.garbagecan.moderation.util.ModUtil;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class Warning {
    private final int id;
    private final String uId;
    private final OffenseType offenseType;
    private final String additionalComments;
    private final Timestamp creationTime;

    public Warning(int id, String uId, OffenseType offenseType, String additionalComments, Timestamp creationTime) {
        this.id = id;
        this.uId = uId;
        this.offenseType = offenseType;
        this.additionalComments = additionalComments;
        this.creationTime = creationTime;
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
        if (!obj.getClass().equals(Warning.class))
            return false;

        return ((Warning) obj).getId() == this.id;
    }

    public String toString() {
        return "Issued at " + ModUtil.formatter.format(creationTime.toLocalDateTime()) + " UTC. \n" +
                "\n" +
                "Offense: " + offenseType.getShortName() + " - " + offenseType.getDescription() + "\n" +
                "\n" +
                "Additional Comments:\n" +
                additionalComments;
    }
}
