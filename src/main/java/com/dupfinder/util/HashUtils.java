package com.dupfinder.util;

/**
 * Owner: Member 8 (Buhle) - Documentation & UML Lead (backup role: FileUtils, HashUtils)
 *
 * Shared hash-related helper methods, e.g. converting a raw byte[] digest
 * into a lowercase hex string. Kept separate from HashCalculator so
 * HashCalculator can stay focused on the actual file-reading/threading logic.
 *
 * TODO (Member 8):
 *   - bytesToHex(byte[] digest): return the lowercase hex string
 *     (used by HashCalculator after MessageDigest.digest()).
 */
public class HashUtils {

    private HashUtils() {
        // utility class
    }

    public static String bytesToHex(byte[] digest) {
        throw new UnsupportedOperationException("TODO: implement in HashUtils (Member 8)");
    }
}
