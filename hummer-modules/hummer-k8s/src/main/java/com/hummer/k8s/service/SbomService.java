package com.hummer.k8s.service;

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
import com.hummer.common.mapper.mapper.*;
import com.hummer.common.mapper.mapper.ext.ExtCodeResultMapper;
import com.hummer.common.mapper.mapper.ext.ExtFileSystemResultMapper;
import com.hummer.common.mapper.mapper.ext.ExtImageResultMapper;
import com.hummer.common.mapper.mapper.ext.ExtSbomMapper;
import com.hummer.common.core.utils.BeanUtils;
import com.hummer.common.core.utils.SessionUtils;
import com.hummer.common.core.utils.UUIDUtil;
import com.hummer.common.mapper.service.CodeService;
import com.hummer.common.mapper.service.ImageService;
import com.hummer.common.mapper.service.OperationLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    private CodeService codeService;
    @Resource
    private ImageService imageService;
    @Resource
    private HistoryCodeResultMapper historyCodeResultMapper;
    @Resource
    private HistoryImageResultMapper historyImageResultMapper;
    @Resource
    private ImageResultLogMapper imageResultLogMapper;
    @Resource
    private ExtFileSystemResultMapper extFileSystemResultMapper;
    @Resource
    private HistoryFileSystemResultMapper historyFileSystemResultMapper;
    @Resource
    private CodeResultLogMapper codeResultLogMapper;
    @Resource
    private ExtCodeResultMapper extCodeResultMapper;
    @Resource
    private ExtImageResultMapper extImageResultMapper;


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
        return historyCodeResultMapper.selectByExample(example);
    }

    public List<HistoryImageResultDTO> historyImageResult(String sbomVersionId) throws Exception {
        HistoryImageResultExample example = new HistoryImageResultExample();
        example.createCriteria().andSbomVersionIdEqualTo(sbomVersionId);
        example.setOrderByClause("create_time desc");
        List<HistoryImageResult> historyImageResults = historyImageResultMapper.selectByExample(example);
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
        List<HistoryFsResultDTO> historyList = extFileSystemResultMapper.history(params);
        return historyList;
    }


    public List<CodeResultLogWithBLOBs> getCodeResultLog(String resultId) {
        CodeResultLogExample example = new CodeResultLogExample();
        example.createCriteria().andResultIdEqualTo(resultId);
        return codeResultLogMapper.selectByExampleWithBLOBs(example);
    }

    public HistoryCodeResult getCodeResult(String resultId) {
        HistoryCodeResult codeResult = historyCodeResultMapper.selectByPrimaryKey(resultId);
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
            HistoryCodeResult codeResult = historyCodeResultMapper.selectByPrimaryKey(request.getSourceId());
            str = codeResult.getReturnJson() != null ? codeResult.getReturnJson() : "{}";
        } else if (StringUtils.equalsIgnoreCase(request.getType(), "image")) {
            HistoryImageResultWithBLOBs imageTask = historyImageResultMapper.selectByPrimaryKey(request.getSourceId());
            str = imageTask.getResultJson() != null ? imageTask.getResultJson() : "{}";
        } else if (StringUtils.equalsIgnoreCase(request.getType(), "fs")) {
            HistoryFileSystemResult fsTask = historyFileSystemResultMapper.selectByPrimaryKey(request.getSourceId());
            str = fsTask.getReturnJson() != null ? fsTask.getReturnJson() : "{}";
        }
        return str;
    }

}
