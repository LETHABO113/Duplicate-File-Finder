package com.dupfinder.hash;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HashCalculator {

    public String hashFile(Path file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            try (InputStream in = new BufferedInputStream(Files.newInputStream(file))) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            }

            byte[] hashBytes = digest.digest();
            return bytesToHex(hashBytes);

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is built into every Java installation, so this branch is effectively unreachable
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public Map<Path, String> hashAll(List<Path> files) {
        int threadCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        Map<Path, String> results = new ConcurrentHashMap<>();

        List<Future<?>> futures = new ArrayList<>();

        for (Path file : files) {
            Future<?> future = executor.submit(() -> {
                try {
                    String hash = hashFile(file);
                    results.put(file, hash);
                } catch (IOException e) {
                    // Skip this file, don't let it crash the whole batch
                    System.err.println("Could not hash " + file + ": " + e.getMessage());
                }
            });
            futures.add(future);
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Hashing task failed: " + e.getMessage());
            }
        }

        executor.shutdown();
        return results;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}