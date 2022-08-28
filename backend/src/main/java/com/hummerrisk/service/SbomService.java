package com.hummerrisk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hummerrisk.base.domain.*;
import com.hummerrisk.base.domain.Package;
import com.hummerrisk.base.mapper.*;
import com.hummerrisk.base.mapper.ext.ExtCodeMapper;
import com.hummerrisk.base.mapper.ext.ExtCodeResultMapper;
import com.hummerrisk.base.mapper.ext.ExtCodeRuleMapper;
import com.hummerrisk.base.mapper.ext.ExtSbomMapper;
import com.hummerrisk.commons.constants.*;
import com.hummerrisk.commons.exception.HRException;
import com.hummerrisk.commons.utils.*;
import com.hummerrisk.controller.request.code.CodeRequest;
import com.hummerrisk.controller.request.code.CodeResultRequest;
import com.hummerrisk.controller.request.code.CodeRuleRequest;
import com.hummerrisk.controller.request.sbom.SbomRequest;
import com.hummerrisk.controller.request.sbom.SbomVersionRequest;
import com.hummerrisk.dto.*;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.proxy.code.CodeCredential;
import com.hummerrisk.proxy.code.CodeCredentialRequest;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SbomService {

    @Resource
    private ExtSbomMapper extSbomMapper;
    @Resource
    private SbomMapper sbomMapper;
    @Resource
    private SbomVersionMapper sbomVersionMapper;
    @Resource
    private CodeMapper codeMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private PackageMapper packageMapper;
    @Resource
    private CodeService codeService;
    @Resource
    private ImageService imageService;
    @Resource
    private PackageService packageService;


    public List<SbomDTO> sbomList(SbomRequest request) {
        return extSbomMapper.sbomList(request);
    }

    public Sbom addSbom(Sbom sbom) throws Exception {
        String id = UUIDUtil.newUUID();
        sbom.setId(id);
        sbom.setCreator(SessionUtils.getUserId());
        sbom.setCreateTime(System.currentTimeMillis());
        sbom.setUpdateTime(System.currentTimeMillis());

        OperationLogService.log(SessionUtils.getUser(), sbom.getId(), sbom.getName(), ResourceTypeConstants.SBOM.name(), ResourceOperation.CREATE, "i18n_create_sbom");
        sbomMapper.insertSelective(sbom);
        return sbom;
    }

    public Sbom updateSbom(Sbom sbom) throws Exception {
        sbom.setUpdateTime(System.currentTimeMillis());

        OperationLogService.log(SessionUtils.getUser(), sbom.getId(), sbom.getName(), ResourceTypeConstants.SBOM.name(), ResourceOperation.UPDATE, "i18n_update_sbom");
        sbomMapper.updateByPrimaryKeySelective(sbom);
        return sbom;
    }

    public void deleteSbom(String id) throws Exception {
        sbomMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.SBOM.name(), ResourceOperation.DELETE, "i18n_delete_sbom");
    }

    public List<SbomVersion> sbomVersionList(SbomVersionRequest request) {
        SbomVersionExample example = new SbomVersionExample();
        example.createCriteria().andSbomIdEqualTo(request.getSbomId());
        return sbomVersionMapper.selectByExample(example);
    }

    public SbomVersion addSbomVersion(SbomVersion sbomVersion) throws Exception {
        String id = UUIDUtil.newUUID();
        sbomVersion.setId(id);
        sbomVersion.setCreateTime(System.currentTimeMillis());
        sbomVersion.setUpdateTime(System.currentTimeMillis());

        OperationLogService.log(SessionUtils.getUser(), sbomVersion.getId(), sbomVersion.getName(), ResourceTypeConstants.SBOM_VERSION.name(), ResourceOperation.CREATE, "i18n_create_sbom_version");
        sbomVersionMapper.insertSelective(sbomVersion);
        return sbomVersion;
    }

    public SbomVersion updateSbomVersion(SbomVersion sbomVersion) throws Exception {
        sbomVersion.setUpdateTime(System.currentTimeMillis());

        OperationLogService.log(SessionUtils.getUser(), sbomVersion.getId(), sbomVersion.getName(), ResourceTypeConstants.SBOM_VERSION.name(), ResourceOperation.UPDATE, "i18n_update_sbom_version");
        sbomVersionMapper.updateByPrimaryKeySelective(sbomVersion);
        return sbomVersion;
    }

    public void deleteSbomVersion(String id) throws Exception {
        sbomVersionMapper.deleteByPrimaryKey(id);
        OperationLogService.log(SessionUtils.getUser(), id, id, ResourceTypeConstants.SBOM_VERSION.name(), ResourceOperation.DELETE, "i18n_delete_sbom_version");
    }

    public void scan(String id) throws Exception {
        CodeExample codeExample = new CodeExample();
        codeExample.createCriteria().andSbomVersionIdEqualTo(id);
        List<Code> codes = codeMapper.selectByExample(codeExample);
        codes.forEach(code -> {
            try {
                codeService.scan(code.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        ImageExample imageExample = new ImageExample();
        imageExample.createCriteria().andSbomVersionIdEqualTo(id);
        List<Image> images = imageMapper.selectByExample(imageExample);
        images.forEach(image -> {
            try {
                imageService.scan(image.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        PackageExample packageExample = new PackageExample();
        packageExample.createCriteria().andSbomVersionIdEqualTo(id);
        List<Package> packages = packageMapper.selectByExample(packageExample);
        packages.forEach(pg -> {
            try {
                packageService.scan(pg.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

}
