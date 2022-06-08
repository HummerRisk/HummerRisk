package io.hummerrisk.config;

import io.hummerrisk.commons.utils.EncryptConfig;

import java.util.ArrayList;
import java.util.List;

public interface DBEncryptConfig {
    default List<EncryptConfig> encryptConfig() {
        return new ArrayList<>();
    }
}
