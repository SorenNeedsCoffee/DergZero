package fyi.sorenneedscoffee.derg_zero.moderation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId utc = ZoneId.of("UTC");

    public static ZonedDateTime toUTC(LocalDateTime date, ZoneId zone) {
        ZonedDateTime from = date.atZone(zone);
        return from.withZoneSameInstant(utc);
    }

    public static ZonedDateTime fromUTC(LocalDateTime date, ZoneId zone) {
        ZonedDateTime from = date.atZone(utc);
        return from.withZoneSameInstant(zone);
    }
}
