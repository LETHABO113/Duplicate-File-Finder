package com.dupfinder.report;

import com.dupfinder.core.DuplicateGroup;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Owner: Member 5 (Nkazimulo) - CLI Developer (backup role: ReportGenerator)
 *
 * Produces output in three formats: console summary, CSV export, JSON export.
 *
 * TODO (Member 5):
 *   - printConsoleSummary(): "Duplicate groups found: N", "Wasted space: X GB",
 *     then top 3 groups by wasted space, as shown in the project brief.
 *   - exportCsv(): one row per file, columns like group hash, size, path.
 *   - exportJson(): same data, JSON array of groups.
 *   - Write tests for CSV/JSON formatting on a small sample set of groups.
 */
public class ReportGenerator {

    public void printConsoleSummary(List<DuplicateGroup<Path>> groups) {
        throw new UnsupportedOperationException("TODO: implement in ReportGenerator (Member 5)");
    }

    public void exportCsv(List<DuplicateGroup<Path>> groups, Path outputFile) throws IOException {
        throw new UnsupportedOperationException("TODO: implement in ReportGenerator (Member 5)");
    }

    public void exportJson(List<DuplicateGroup<Path>> groups, Path outputFile) throws IOException {
        throw new UnsupportedOperationException("TODO: implement in ReportGenerator (Member 5)");
    }
}
