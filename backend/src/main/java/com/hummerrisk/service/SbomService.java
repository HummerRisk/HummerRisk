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
import com.hummerrisk.controller.request.sbom.SettingVersionRequest;
import com.hummerrisk.dto.*;
import com.hummerrisk.i18n.Translator;
import com.hummerrisk.proxy.code.CodeCredential;
import com.hummerrisk.proxy.code.CodeCredentialRequest;
import io.kubernetes.client.openapi.ApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    @Resource
    private HistoryCodeResultMapper historyCodeResultMapper;
    @Resource
    private HistoryImageTaskMapper historyImageTaskMapper;
    @Resource
    private HistoryPackageTaskMapper historyPackageTaskMapper;
    @Resource
    private HistoryCodeResultLogMapper historyCodeResultLogMapper;
    @Resource
    private HistoryImageTaskLogMapper historyImageTaskLogMapper;
    @Resource
    private HistoryPackageTaskLogMapper historyPackageTaskLogMapper;


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
        example.setOrderByClause("update_time desc");
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

    public void settingVersion(SettingVersionRequest request) throws Exception {
        String sbomId = request.getSbomId();
        String sbomVersionId = request.getSbomVersionId();
        //源码
        CodeExample codeExample = new CodeExample();
        codeExample.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        List<Code> codes = codeMapper.selectByExample(codeExample);
        for(Code code : codes) {
            code.setSbomId("");
            code.setSbomVersionId("");
            codeMapper.updateByPrimaryKeySelective(code);
        }
        for(String id : request.getCodeValue()) {
            Code code = codeMapper.selectByPrimaryKey(id);
            code.setSbomId(sbomId);
            code.setSbomVersionId(sbomVersionId);
            codeMapper.updateByPrimaryKeySelective(code);
        }
        //镜像
        ImageExample imageExample = new ImageExample();
        imageExample.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        List<Image> images = imageMapper.selectByExample(imageExample);
        for(Image image : images) {
            image.setSbomId("");
            image.setSbomVersionId("");
            imageMapper.updateByPrimaryKeySelective(image);
        }
        for(String id : request.getImageValue()) {
            Image image = imageMapper.selectByPrimaryKey(id);
            image.setSbomId(sbomId);
            image.setSbomVersionId(sbomVersionId);
            imageMapper.updateByPrimaryKeySelective(image);
        }
        //软件包
        PackageExample packageExample = new PackageExample();
        packageExample.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        List<Package> packages = packageMapper.selectByExample(packageExample);
        for(Package pg : packages) {
            pg.setSbomId("");
            pg.setSbomVersionId("");
            packageMapper.updateByPrimaryKeySelective(pg);
        }
        for(String id : request.getPackageValue()) {
            Package pg = packageMapper.selectByPrimaryKey(id);
            pg.setSbomId(sbomId);
            pg.setSbomVersionId(sbomVersionId);
            packageMapper.updateByPrimaryKeySelective(pg);
        }
    }

    public List<ApplicationDTO> applications(SbomRequest request) throws Exception {
        return extSbomMapper.applications(request);
    }

    public List<HistoryCodeResult> historyCodeResult(String sbomVersionId) {
        HistoryCodeResultExample example = new HistoryCodeResultExample();
        example.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        example.setOrderByClause("create_time desc");
        return historyCodeResultMapper.selectByExample(example);
    }

    public List<HistoryImageTaskDTO> historyImageTask(String sbomVersionId) throws Exception {
        HistoryImageTaskExample example = new HistoryImageTaskExample();
        example.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        example.setOrderByClause("create_time desc");
        List<HistoryImageTask> historyImageTasks = historyImageTaskMapper.selectByExample(example);
        List<HistoryImageTaskDTO> dtos = new LinkedList<>();
        for (HistoryImageTask task : historyImageTasks) {
            HistoryImageTaskDTO dto = new HistoryImageTaskDTO();
            Image image = imageMapper.selectByPrimaryKey(task.getImageId());
            BeanUtils.copyBean(dto, image);
            BeanUtils.copyBean(dto, task);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<HistoryPackageTask> historyPackageTask(String sbomVersionId) {
        HistoryPackageTaskExample example = new HistoryPackageTaskExample();
        example.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        example.setOrderByClause("create_time desc");
        return historyPackageTaskMapper.selectByExample(example);
    }

    public List<HistoryCodeResultLog> getCodeResultLog(String resultId) {
        HistoryCodeResultLogExample example = new HistoryCodeResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return historyCodeResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public HistoryCodeResult getCodeResult(String resultId) {
        HistoryCodeResult codeResult = historyCodeResultMapper.selectByPrimaryKey(resultId);
        return codeResult;
    }

    public List<HistoryImageTaskLog> getImageResultLog(String resultId) {
        HistoryImageTaskLogExample example = new HistoryImageTaskLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return historyImageTaskLogMapper.selectByExampleWithBLOBs(example);
    }

    public List<HistoryPackageTaskLog> getPackageResultLog(String resultId) {
        HistoryPackageTaskLogExample example = new HistoryPackageTaskLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return historyPackageTaskLogMapper.selectByExampleWithBLOBs(example);
    }

    public Map<String, String> codeMetricChart (String resultId) {
        return extSbomMapper.codeMetricChart(resultId);
    }

    public Map<String, String> imageMetricChart (String resultId) {
        return extSbomMapper.imageMetricChart(resultId);
    }

    public Map<String, String> packageMetricChart (String resultId) {
        return extSbomMapper.packageMetricChart(resultId);
    }


}
