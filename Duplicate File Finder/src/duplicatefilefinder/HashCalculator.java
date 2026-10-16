package duplicatefilefinder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class HashCalculator implements AutoCloseable {
    private static final int BUFFER_SIZE = 8192;

    private final ExecutorService executor;

    public HashCalculator() {
        this(Math.max(1, Runtime.getRuntime().availableProcessors()));
    }

    public HashCalculator(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("Thread count must be at least 1");
        }
        executor = Executors.newFixedThreadPool(threadCount);
    }

    public String calculateHash(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        return calculateHash(file.toPath());
    }

    public String calculateHash(Path path) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new IOException("File is missing, unreadable, or not a regular file: " + path);
        }

        MessageDigest digest = HashUtil.sha256();
        byte[] buffer = new byte[BUFFER_SIZE];

        try (InputStream input = Files.newInputStream(path)) {
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        return HashUtil.bytesToHex(digest.digest());
    }

    public Map<File, String> calculateHashes(List<File> files) throws IOException {
        if (files == null) {
            throw new IllegalArgumentException("File list cannot be null");
        }
        for (File file : files) {
            if (file == null) {
                throw new IllegalArgumentException("File list cannot contain null values");
            }
        }

        List<Future<String>> results = new ArrayList<>(files.size());
        try {
            for (File file : files) {
                results.add(executor.submit(() -> calculateHash(file)));
            }
        } catch (RuntimeException e) {
            cancelAll(results);
            throw e;
        }

        Map<File, String> hashes = new LinkedHashMap<>();
        try {
            for (int i = 0; i < files.size(); i++) {
                hashes.put(files.get(i), results.get(i).get());
            }
        } catch (InterruptedException e) {
            cancelAll(results);
            Thread.currentThread().interrupt();
            throw new IOException("Hash calculation was interrupted", e);
        } catch (ExecutionException e) {
            cancelAll(results);
            Throwable cause = e.getCause();
            if (cause instanceof IOException ioException) {
                throw ioException;
            }
            throw new IOException("Unable to calculate a file hash", cause);
        }

        return hashes;
    }

    private void cancelAll(List<Future<String>> results) {
        for (Future<String> result : results) {
            result.cancel(true);
        }
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
