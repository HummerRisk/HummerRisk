package com.hummer.system.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hummer.common.core.constant.ServerConstants;
import com.hummer.common.core.domain.HummerLicense;
import com.hummer.common.core.utils.CommandUtils;
import com.hummer.common.core.utils.FileUploadUtils;
import com.hummer.common.core.utils.ReadFileUtils;
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

    public boolean license() {
        List<HummerLicense> list = licenseMapper.selectByExample(null);
        if (list.size() > 0) {
            return true;
        }
        return false;
    }

    public void validateLicense(MultipartFile licenseFile, LoginUser loginUser) throws Exception {
        String licenseFilePath = upload(licenseFile, ServerConstants.DEFAULT_BASE_DIR);
        String license = ReadFileUtils.readToBuffer(ServerConstants.DEFAULT_BASE_DIR + licenseFilePath);

        //校验license命令
        String command = "/usr/local/bin/hummer_validator " + license;
        //returnStr
        String returnStr = CommandUtils.commonExecCmdWithResult(command, "/tmp");
        JSONObject jsonObject = JSON.parseObject(returnStr);
        String status = jsonObject.getString("status");
        String message = jsonObject.getString("message");
        JSONObject licenseObj = (JSONObject) jsonObject.get("license");
        String corporation = licenseObj.getString("corporation");
        String edition = licenseObj.getString("edition");
        String count =licenseObj.getString("count");
        String product =licenseObj.getString("product");
        String expired =licenseObj.getString("expired");
        long expiredTime = (new SimpleDateFormat("yyyy-MM-dd")).parse(expired, new ParsePosition(0)).getTime();

        List<HummerLicense> list = licenseMapper.selectByExample(null);
        if (list.size() > 0) {
            HummerLicense hummerLicense = list.get(0);
            hummerLicense.setCompany(corporation);
            hummerLicense.setEdition(edition);
            hummerLicense.setExpireTime(expiredTime);
            hummerLicense.setApplyUser(loginUser.getUserName());
            hummerLicense.setAuthorizeCount(count);
            hummerLicense.setLicenseKey(license);
            hummerLicense.setProductType(product);
            hummerLicense.setUpdateTime(System.currentTimeMillis());

            licenseMapper.updateByPrimaryKeySelective(hummerLicense);
        } else {
            HummerLicense hummerLicense = new HummerLicense();
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

            licenseMapper.insertSelective(hummerLicense);
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
