package utils;


public final class Constants {

    private Constants() {  }

    public static final int RING_SIZE_BYTES = 2;
    public static final int RING_SIZE = 8 * RING_SIZE_BYTES;
    public static final int SALT_SIZE = 64;
    public static final int MIN_PORT = 1024;
    public static final int MAX_PORT = 49151;

    public static final String ENTRY_ADDRESS = "127.0.0.1";
    public static final int ENTRY_PORT = 10000;
}