package com.hummer.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hummer.common.core.domain.HummerLicenseWithBLOBs;
import com.hummer.common.core.utils.CommandUtils;
import com.hummer.common.core.utils.FileUploadUtils;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.system.api.model.LoginUser;
import com.hummer.system.mapper.HummerLicenseMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LicenseService {

    @Autowired
    private HummerLicenseMapper licenseMapper;

    public HummerLicenseWithBLOBs getHummerLicense(String id) {
        return licenseMapper.selectByPrimaryKey(id);
    }

    public HummerLicenseWithBLOBs getLicense() {
        List<HummerLicenseWithBLOBs> list = licenseMapper.selectByExampleWithBLOBs(null);
        if (list.size() > 0) {
            return list.get(0);
        }
        return new HummerLicenseWithBLOBs();
    }

    public boolean license() {
        List<HummerLicenseWithBLOBs> list = licenseMapper.selectByExampleWithBLOBs(null);
        if (list.size() > 0) {
            HummerLicenseWithBLOBs hummerLicense = list.get(0);
            if (!StringUtils.equals(hummerLicense.getStatus(), "valid")) return false;
            //license状态，可能值为：valid、invalid、expired，分别代表：有效、无效、已过期
            long expireTime = hummerLicense.getExpireTime();
            long now = System.currentTimeMillis();
            if (expireTime >= now) {
                return true;
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(new Date(Long.valueOf(expireTime)));
                String message = "license has expired since " + date;
                hummerLicense.setUpdateTime(now);
                hummerLicense.setStatus("expired");
                hummerLicense.setMessage(message);
                licenseMapper.updateByPrimaryKeySelective(hummerLicense);
            }

        }
        return false;
    }


    public void validateLicense(MultipartFile licenseFile, LoginUser loginUser) throws Exception {
        try {
            String license = new String(licenseFile.getBytes());

            //校验license命令
            String command = "/tmp/validator_darwin_arm64 " + license;
            //returnStr
            String returnStr = CommandUtils.commonExecCmdWithResult(command, "/tmp");
            JSONObject jsonObject = JSON.parseObject(returnStr);
            String status = jsonObject.getString("status");
            String message = jsonObject.getString("message");//license has expired since 2023-07-03
            JSONObject licenseObj = (JSONObject) jsonObject.get("license");
            String corporation = licenseObj.getString("corporation");
            String edition = licenseObj.getString("edition");
            String count =licenseObj.getString("count");
            String product =licenseObj.getString("product");
            String expired =licenseObj.getString("expired");
            long expiredTime = (new SimpleDateFormat("yyyy-MM-dd")).parse(expired, new ParsePosition(0)).getTime();

            if (!StringUtils.equals(product, "HummerRisk")) {
                status = "invalid";//无效
                message = "The license product type is incorrect。";
            }

            List<HummerLicenseWithBLOBs> list = licenseMapper.selectByExampleWithBLOBs(null);
            if (list.size() > 0) {
                HummerLicenseWithBLOBs hummerLicense = list.get(0);
                hummerLicense.setCompany(corporation);
                hummerLicense.setEdition(edition);
                hummerLicense.setExpireTime(expiredTime);
                hummerLicense.setApplyUser(loginUser.getUserName());
                hummerLicense.setAuthorizeCount(count);
                hummerLicense.setLicenseKey(license);
                hummerLicense.setProductType(product);
                hummerLicense.setUpdateTime(System.currentTimeMillis());
                hummerLicense.setStatus(status);
                hummerLicense.setMessage(message);

                licenseMapper.updateByPrimaryKeySelective(hummerLicense);
            } else {
                HummerLicenseWithBLOBs hummerLicense = new HummerLicenseWithBLOBs();
                hummerLicense.setId(UUIDUtil.newUUID());
                hummerLicense.setCompany(corporation);
                hummerLicense.setEdition(edition);
                hummerLicense.setExpireTime(expiredTime);
                hummerLicense.setApplyUser(loginUser.getUserName());
                hummerLicense.setAuthorizeCount(count);
                hummerLicense.setLicenseKey(license);
                hummerLicense.setProductType(product);
                hummerLicense.setCreateTime(System.currentTimeMillis());
                hummerLicense.setUpdateTime(System.currentTimeMillis());
                hummerLicense.setStatus(status);
                hummerLicense.setMessage(message);

                licenseMapper.insertSelective(hummerLicense);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    /**
     * 以默认配置进行文件上传
     *
     * @param file 上传的文件
     * @return 文件名称
     * @throws Exception
     */
    public static final String upload(MultipartFile file, String dir) throws IOException {
        try {
            String fileName = file.getOriginalFilename();
            String extension = StringUtils.isNotBlank(fileName) && fileName.contains(".") ? fileName.split("\\.")[fileName.split("\\.").length - 1] : "";
            //png、html等小文件存放路径，页面需要显示，项目内目录
            //jar包等大文件存放路径，项目外目录
            return FileUploadUtils.uploadCertificate(dir, file, extension.contains(".") ? "." + extension : "");
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}
