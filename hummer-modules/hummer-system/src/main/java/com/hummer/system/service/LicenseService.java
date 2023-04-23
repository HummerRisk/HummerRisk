package com.hummer.system.service;

import com.hummer.common.core.domain.HummerLicense;
import com.hummer.system.mapper.HummerLicenseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LicenseService {

    @Autowired
    private HummerLicenseMapper licenseMapper;

    public HummerLicense getHummerLicense(String id) {
        return licenseMapper.selectByPrimaryKey(id);
    }

    public HummerLicense getLicense() {
        List<HummerLicense> list = licenseMapper.selectByExample(null);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

}
