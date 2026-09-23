package com.dupfinder.log;

import java.nio.file.Path;

/**
 * Owner: Member 7 (Amogelang) - Testing & QA Lead (backup role: Logger, AppSettings)
 *
 * Timestamped activity log - used for the audit trail (especially by
 * QuarantineManager) and general app events/errors.
 *
 * TODO (Member 7):
 *   - Implement writing timestamped lines to a log file (e.g. app.log).
 *   - Provide log levels if useful: info(), warn(), error().
 *   - Coordinate with Member 3 on the exact format QuarantineManager needs
 *     for its audit entries (original path, quarantine path, timestamp).
 */
public class Logger {

    private final Path logFile;

    public Logger(Path logFile) {
        this.logFile = logFile;
    }

    public void info(String message) {
        throw new UnsupportedOperationException("TODO: implement in Logger (Member 7)");
    }

    public void warn(String message) {
        throw new UnsupportedOperationException("TODO: implement in Logger (Member 7)");
    }

    public void error(String message, Throwable t) {
        throw new UnsupportedOperationException("TODO: implement in Logger (Member 7)");
    }
}
