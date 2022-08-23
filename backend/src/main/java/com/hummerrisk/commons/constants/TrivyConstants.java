package com.hummerrisk.commons.constants;

/**
 * @author harris
 */
public class TrivyConstants {

    public final static String DEFAULT_BASE_DIR = "/opt/hummerrisk/trivy/";

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

    public final static String TRIVY_IMAGE = "trivy image ";

    public final static String TRIVY_CONFIG = "trivy config ";

    public final static String TRIVY_REPO = "trivy repo ";

    public final static String TRIVY_SBOM = "trivy sbom ";

    public final static String TRIVY_TYPE = " --format json -o ";

    public final static String TRIVY_JSON = "trivy.json";

    public final static String TRIVY_YAML = "trivy.yaml";

    public final static String TRIVY_SKIP = " --skip-update ";

}
