package coffee.server.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class Util {
    private Util() {}

    public ZonedDateTime instantToUtcZonedDateTime(Instant instant) {
        return instant.atZone(ZoneId.of("UTC"));
    }
}
