import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtil {
    public static final String DEFAULT_ALGORITHM = "SHA-256";

    private HashUtil(){
        throw new AssertionError("Utility class, do not instantiate");
    }

    public static String bytesToHex(byte[] bytes){
        if (bytes == null){
            throw new IllegalArgumentException("Bytes array cannnot be null");
        }
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        for (byte b : bytes){
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }

        return sb.toString();
    }

    public static MessageDigest creationDigest(String algorithm){
        if (algorithm == null || algorithm.isBlank()){
            throw new IllegalArgumentException("Algorithm can be null or blank");
        }
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException e){
            throw new IllegalArgumentException("Unsupported hash algorithm: " + algorithm, e);
        }
    }

    public static MessageDigest sha256(){
        return creationDigest(DEFAULT_ALGORITHM);
    }
}
