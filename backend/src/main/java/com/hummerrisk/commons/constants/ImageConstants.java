package com.hummerrisk.commons.constants;

/**
 * @author harris
 */
public class ImageConstants {

    public final static String DEFAULT_BASE_DIR = "/opt/hummerrisk/image/";

    public final static String IMAGE_JPG_EXTENSION = ".png";

    /**
     * 默认大小 500M
     */
    public static final long DEFAULT_MAX_SIZE = 1024 * 1024 * 1024 * 5;

    int GB = 1024 * 1024 * 1024;
    int MB = 1024 * 1024;
    int KB = 1024;

    /**
     * 默认的文件名最大长度 100
     */
    public static final int DEFAULT_FILE_NAME_LENGTH = 100;

    public final static String GRYPE = "grype ";
    public final static String SYFT = "syft ";
    public final static String SCOPE = " --scope all-layers ";
    public final static String OUT = "-o ";
    public final static String _FILE = "--file ";
    public final static String TXT = "result.txt";
    public final static String TEXT = "text ";
    public final static String TABLE = "table ";
    public final static String JSON = "json ";
}
