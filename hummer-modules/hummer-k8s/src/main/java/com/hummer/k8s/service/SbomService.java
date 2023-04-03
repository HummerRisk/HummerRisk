package com.hummer.k8s.service;

import com.hummer.cloud.api.ICloudProviderService;
import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.constant.ResourceTypeConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.code.CodeResultRequest;
import com.hummer.common.core.domain.request.fs.FsResultRequest;
import com.hummer.common.core.domain.request.image.ImageResultRequest;
import com.hummer.common.core.domain.request.sbom.DownloadRequest;
import com.hummer.common.core.domain.request.sbom.SbomRequest;
import com.hummer.common.core.domain.request.sbom.SbomVersionRequest;
import com.hummer.common.core.domain.request.sbom.SettingVersionRequest;
import com.hummer.common.core.dto.*;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.k8s.mapper.*;
import com.hummer.k8s.mapper.ext.ExtCodeResultMapper;
import com.hummer.k8s.mapper.ext.ExtFileSystemResultMapper;
import com.hummer.k8s.mapper.ext.ExtImageResultMapper;
import com.hummer.k8s.mapper.ext.ExtSbomMapper;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.ISystemProviderService;
import com.hummer.system.api.model.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SbomService {

    @Autowired
    private ExtSbomMapper extSbomMapper;
    @Autowired
    private SbomMapper sbomMapper;
    @Autowired
    private SbomVersionMapper sbomVersionMapper;
    @Autowired
    private CodeMapper codeMapper;
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private CodeService codeService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageResultLogMapper imageResultLogMapper;
    @Autowired
    private ExtFileSystemResultMapper extFileSystemResultMapper;
    @Autowired
    private CodeResultLogMapper codeResultLogMapper;
    @Autowired
    private ExtCodeResultMapper extCodeResultMapper;
    @Autowired
    private ExtImageResultMapper extImageResultMapper;
    @DubboReference
    private ISystemProviderService systemProviderService;
    @DubboReference
    private IOperationLogService operationLogService;
    @DubboReference
    private ICloudProviderService cloudProviderService;


    public List<SbomDTO> sbomList(SbomRequest request) {
        return extSbomMapper.sbomList(request);
    }

    public Sbom addSbom(Sbom sbom, LoginUser loginUser) throws Exception {
        String id = UUIDUtil.newUUID();
        sbom.setId(id);
        sbom.setCreator(loginUser.getUserId());
        sbom.setCreateTime(System.currentTimeMillis());
        sbom.setUpdateTime(System.currentTimeMillis());

        operationLogService.log(loginUser, sbom.getId(), sbom.getName(), ResourceTypeConstants.SBOM.name(), ResourceOperation.CREATE, "i18n_create_sbom");
        sbomMapper.insertSelective(sbom);
        return sbom;
    }

    public Sbom updateSbom(Sbom sbom, LoginUser loginUser) throws Exception {
        sbom.setUpdateTime(System.currentTimeMillis());

        operationLogService.log(loginUser, sbom.getId(), sbom.getName(), ResourceTypeConstants.SBOM.name(), ResourceOperation.UPDATE, "i18n_update_sbom");
        sbomMapper.updateByPrimaryKeySelective(sbom);
        return sbom;
    }

    public void deleteSbom(String id, LoginUser loginUser) throws Exception {
        sbomMapper.deleteByPrimaryKey(id);
        operationLogService.log(loginUser, id, id, ResourceTypeConstants.SBOM.name(), ResourceOperation.DELETE, "i18n_delete_sbom");
    }

    public List<SbomVersion> sbomVersionList(SbomVersionRequest request) {
        SbomVersionExample example = new SbomVersionExample();
        example.createCriteria().andSbomIdEqualTo(request.getSbomId());
        example.setOrderByClause("update_time desc");
        return sbomVersionMapper.selectByExample(example);
    }

    public SbomVersion addSbomVersion(SbomVersion sbomVersion, LoginUser loginUser) throws Exception {
        String id = UUIDUtil.newUUID();
        sbomVersion.setId(id);
        sbomVersion.setCreateTime(System.currentTimeMillis());
        sbomVersion.setUpdateTime(System.currentTimeMillis());

        operationLogService.log(loginUser, sbomVersion.getId(), sbomVersion.getName(), ResourceTypeConstants.SBOM_VERSION.name(), ResourceOperation.CREATE, "i18n_create_sbom_version");
        sbomVersionMapper.insertSelective(sbomVersion);
        return sbomVersion;
    }

    public SbomVersion updateSbomVersion(SbomVersion sbomVersion, LoginUser loginUser) throws Exception {
        sbomVersion.setUpdateTime(System.currentTimeMillis());

        operationLogService.log(loginUser, sbomVersion.getId(), sbomVersion.getName(), ResourceTypeConstants.SBOM_VERSION.name(), ResourceOperation.UPDATE, "i18n_update_sbom_version");
        sbomVersionMapper.updateByPrimaryKeySelective(sbomVersion);
        return sbomVersion;
    }

    public void deleteSbomVersion(String id, LoginUser loginUser) throws Exception {
        sbomVersionMapper.deleteByPrimaryKey(id);
        operationLogService.log(loginUser, id, id, ResourceTypeConstants.SBOM_VERSION.name(), ResourceOperation.DELETE, "i18n_delete_sbom_version");
    }

    public void scan(String id, LoginUser loginUser) throws Exception {
        CodeExample codeExample = new CodeExample();
        codeExample.createCriteria().andSbomVersionIdEqualTo(id);
        List<Code> codes = codeMapper.selectByExample(codeExample);
        codes.forEach(code -> {
            try {
                codeService.scan(code.getId(), loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        ImageExample imageExample = new ImageExample();
        imageExample.createCriteria().andSbomVersionIdEqualTo(id);
        List<Image> images = imageMapper.selectByExample(imageExample);
        images.forEach(image -> {
            try {
                imageService.scan(image.getId(), loginUser);
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
        for (Code code : codes) {
            code.setSbomId("");
            code.setSbomVersionId("");
            codeMapper.updateByPrimaryKeySelective(code);
        }
        for (String id : request.getCodeValue()) {
            Code code = codeMapper.selectByPrimaryKey(id);
            code.setSbomId(sbomId);
            code.setSbomVersionId(sbomVersionId);
            codeMapper.updateByPrimaryKeySelective(code);
        }
        //镜像
        ImageExample imageExample = new ImageExample();
        imageExample.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        List<Image> images = imageMapper.selectByExample(imageExample);
        for (Image image : images) {
            image.setSbomId("");
            image.setSbomVersionId("");
            imageMapper.updateByPrimaryKeySelective(image);
        }
        for (String id : request.getImageValue()) {
            Image image = imageMapper.selectByPrimaryKey(id);
            image.setSbomId(sbomId);
            image.setSbomVersionId(sbomVersionId);
            imageMapper.updateByPrimaryKeySelective(image);
        }
    }

    public List<ApplicationDTO> applications(SbomRequest request) throws Exception {
        return extSbomMapper.applications(request);
    }

    public List<CodeResultDTO> codeResult(String sbomVersionId) {
        CodeResultRequest request = new CodeResultRequest();
        request.setSbomVersionId(sbomVersionId);
        List<CodeResultDTO> list = extCodeResultMapper.resultList(request);
        return list;
    }

    public List<ImageResultDTO> imageResult(String sbomVersionId) {
        ImageResultRequest request = new ImageResultRequest();
        request.setSbomVersionId(sbomVersionId);
        List<ImageResultDTO> list = extImageResultMapper.resultList(request);
        return list;
    }

    public List<FsResultDTO> fsResult(String sbomVersionId) {
        FsResultRequest request = new FsResultRequest();
        request.setSbomVersionId(sbomVersionId);
        List<FsResultDTO> list = extFileSystemResultMapper.resultList(request);
        return list;
    }

    public List<HistoryCodeResult> historyCodeResult(String sbomVersionId) {
        HistoryCodeResultExample example = new HistoryCodeResultExample();
        example.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        example.setOrderByClause("create_time desc");
        return systemProviderService.historyCodeResultByExample(example);
    }

    public List<HistoryImageResultDTO> historyImageResult(String sbomVersionId) throws Exception {
        HistoryImageResultExample example = new HistoryImageResultExample();
        example.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        example.setOrderByClause("create_time desc");
        List<HistoryImageResult> historyImageResults = systemProviderService.historyImageResultByExample(example);
        List<HistoryImageResultDTO> dtos = new LinkedList<>();
        for (HistoryImageResult task : historyImageResults) {
            HistoryImageResultDTO dto = new HistoryImageResultDTO();
            Image image = imageMapper.selectByPrimaryKey(task.getImageId());
            BeanUtils.copyBean(dto, image);
            BeanUtils.copyBean(dto, task);
            dtos.add(dto);
        }
        return dtos;
    }

    public List<HistoryFsResultDTO> historyFsResult(String sbomVersionId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("sbomVersionId", sbomVersionId);
        List<HistoryFsResultDTO> historyList = systemProviderService.fsHistory(params);
        return historyList;
    }


    public List<CodeResultLogWithBLOBs> getCodeResultLog(String resultId) {
        CodeResultLogExample example = new CodeResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return codeResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public HistoryCodeResult getCodeResult(String resultId) {
        HistoryCodeResult codeResult = systemProviderService.codeResult(resultId);
        return codeResult;
    }

    public List<ImageResultLogWithBLOBs> getImageResultLog(String resultId) {
        ImageResultLogExample example = new ImageResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return imageResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public MetricChartDTO codeMetricChart(String resultId) {
        return extSbomMapper.codeMetricChart(resultId);
    }

    public MetricChartDTO imageMetricChart(String resultId) {
        return extSbomMapper.imageMetricChart(resultId);
    }

    public MetricChartDTO fsMetricChart(String resultId) {
        return extSbomMapper.fsMetricChart(resultId);
    }

    public String download(DownloadRequest request) throws Exception {
        String str = "";
        if (StringUtils.equalsIgnoreCase(request.getType(), "code")) {
            HistoryCodeResult codeResult = systemProviderService.codeResult(request.getSourceId());
            str = codeResult.getReturnJson() != null ? codeResult.getReturnJson() : "{}";
        } else if (StringUtils.equalsIgnoreCase(request.getType(), "image")) {
            HistoryImageResultWithBLOBs imageTask = systemProviderService.imageResult(request.getSourceId());
            str = imageTask.getResultJson() != null ? imageTask.getResultJson() : "{}";
        } else if (StringUtils.equalsIgnoreCase(request.getType(), "fs")) {
            HistoryFileSystemResult fsTask = systemProviderService.fsResult(request.getSourceId());
            str = fsTask.getReturnJson() != null ? fsTask.getReturnJson() : "{}";
        }
        return str;
    }

}
