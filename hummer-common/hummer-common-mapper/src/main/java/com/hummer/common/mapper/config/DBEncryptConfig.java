package com.hummer.common.mapper.config;


import com.hummer.common.core.utils.EncryptConfig;

import java.util.ArrayList;
import java.util.List;

public interface DBEncryptConfig {
    default List<EncryptConfig> encryptConfig() {
        return new ArrayList<>();
    }
}
