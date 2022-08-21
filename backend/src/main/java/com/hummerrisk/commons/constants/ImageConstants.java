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
    public final static String OUT = " -o ";
    public final static String _FILE = "--file ";

    public final static String ADDCPE = "--add-cpes-if-none ";
    public final static String GRYPE_TABLE_TXT = "grype_table.txt";
    public final static String GRYPE_JSON_TXT = "grype_json.json";

    public final static String TABLE = "table ";
    public final static String JSON = "json ";

    public final static String SYFT_JSON = "json=";
    public final static String SYFT_TABLE = "table=";
    public final static String SYFT_JSON_TXT = "syft_json.json";
    public final static String SYFT_TABLE_TXT = "syft_table.txt";

    public final static String TRIVY_IMAGE = "trivy image ";

    public final static String TRIVY_CONFIG = "trivy config ";

    public final static String TRIVY_TYPE = " --format json -o ";

    public final static String TRIVY_JSON = "trivy.json";

    public final static String TRIVY_YAML = "trivy.yaml";

}
