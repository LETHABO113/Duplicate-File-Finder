package duplicatefilefinder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ReportGenerator {
    public void printToConsole(List<DuplicateGroup<File>> groups) {
        if (groups == null || groups.isEmpty()) {
            System.out.println("No duplicate files found.");
            return;
        }

        long totalWastedSpace = 0;
        for (DuplicateGroup<File> group : groups) {
            totalWastedSpace += group.getWastedSpace();
        }

        System.out.println("Duplicate groups found: " + groups.size());
        System.out.println("Wasted space: " + totalWastedSpace + " bytes");
        for (int i = 0; i < groups.size(); i++) {
            DuplicateGroup<File> group = groups.get(i);
            System.out.println();
            System.out.println("Group " + (i + 1) + " - " + group.getCount()
                    + " files, " + group.getWastedSpace() + " bytes wasted");
            System.out.println("SHA-256: " + group.getHash());
            for (File file : group.getFiles()) {
                System.out.println("  " + file.getAbsolutePath());
            }
        }
    }

    public void exportToCsv(List<DuplicateGroup<File>> groups, Path outputPath) throws IOException {
        validateExport(groups, outputPath);
        StringBuilder csv = new StringBuilder("Group,Hash,File Size (Bytes),Wasted Space (Bytes),File Path\n");

        for (int i = 0; i < groups.size(); i++) {
            DuplicateGroup<File> group = groups.get(i);
            for (File file : group.getFiles()) {
                csv.append(i + 1).append(',')
                        .append(csvValue(group.getHash())).append(',')
                        .append(group.getFileSize()).append(',')
                        .append(group.getWastedSpace()).append(',')
                        .append(csvValue(file.getAbsolutePath())).append('\n');
            }
        }

        writeReport(outputPath, csv.toString());
    }

    public void exportToJson(List<DuplicateGroup<File>> groups, Path outputPath) throws IOException {
        validateExport(groups, outputPath);
        StringBuilder json = new StringBuilder("{\n  \"duplicateGroups\": [");

        for (int i = 0; i < groups.size(); i++) {
            DuplicateGroup<File> group = groups.get(i);
            if (i > 0) {
                json.append(',');
            }
            json.append("\n    {\n")
                    .append("      \"hash\": \"").append(jsonValue(group.getHash())).append("\",\n")
                    .append("      \"fileSizeBytes\": ").append(group.getFileSize()).append(",\n")
                    .append("      \"wastedSpaceBytes\": ").append(group.getWastedSpace()).append(",\n")
                    .append("      \"files\": [");

            for (int fileIndex = 0; fileIndex < group.getFiles().size(); fileIndex++) {
                if (fileIndex > 0) {
                    json.append(',');
                }
                json.append("\n        \"")
                        .append(jsonValue(group.getFiles().get(fileIndex).getAbsolutePath()))
                        .append('"');
            }
            if (!group.getFiles().isEmpty()) {
                json.append('\n').append("      ");
            }
            json.append("]\n    }");
        }
        if (!groups.isEmpty()) {
            json.append('\n').append("  ");
        }
        json.append("]\n}\n");

        writeReport(outputPath, json.toString());
    }

    private void validateExport(List<DuplicateGroup<File>> groups, Path outputPath) {
        if (groups == null || groups.isEmpty()) {
            throw new IllegalArgumentException("There are no duplicate results to export");
        }
        if (outputPath == null) {
            throw new IllegalArgumentException("Output path cannot be null");
        }
    }

    private void writeReport(Path outputPath, String content) throws IOException {
        Path parent = outputPath.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(outputPath, content, StandardCharsets.UTF_8);
    }

    private String csvValue(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String jsonValue(String value) {
        StringBuilder escaped = new StringBuilder();
        for (char character : value.toCharArray()) {
            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.toString();
    }
}
