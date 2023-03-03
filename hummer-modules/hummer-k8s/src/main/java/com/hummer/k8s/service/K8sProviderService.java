package com.hummer.k8s.service;

import com.hummer.common.core.domain.*;
import com.hummer.k8s.api.IK8sProviderService;
import com.hummer.k8s.mapper.*;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author harris
 */
@DubboService
public class K8sProviderService implements IK8sProviderService {

    @Resource
    private ServerResultMapper serverResultMapper;
    @Resource
    private CloudNativeResultMapper cloudNativeResultMapper;
    @Resource
    private CloudNativeConfigResultMapper cloudNativeConfigResultMapper;
    @Resource
    private ImageResultMapper imageResultMapper;
    @Resource
    private CodeResultMapper codeResultMapper;
    @Resource
    private FileSystemResultMapper fileSystemResultMapper;
    @Resource
    private ServerRuleMapper serverRuleMapper;
    @Resource
    private CloudNativeRuleMapper cloudNativeRuleMapper;
    @Resource
    private CloudNativeConfigRuleMapper configRuleMapper;
    @Resource
    private ImageRuleMapper imageRuleMapper;
    @Resource
    private CodeRuleMapper codeRuleMapper;
    @Resource
    private FileSystemRuleMapper fileSystemRuleMapper;
    @Resource
    private ServerMapper serverMapper;
    @Resource
    private ImageMapper imageMapper;
    @Resource
    private CloudNativeConfigMapper configMapper;
    @Resource
    private CloudNativeMapper cloudNativeMapper;
    @Resource
    private CodeMapper codeMapper;
    @Resource
    private FileSystemMapper fileSystemMapper;
    @Resource
    private ServerService serverService;
    @Resource
    private CodeService codeService;
    @Resource
    private ImageService imageService;
    @Resource
    private K8sService k8sService;
    @Resource
    private ConfigService configService;
    @Resource
    private FileSystemService fileSystemService;
    @Resource
    private ServerGroupMapper serverGroupMapper;


    @Override
    public List<CloudNativeResult> cloudNativeResults(CloudNativeResultExample example) {
        return cloudNativeResultMapper.selectByExample(example);
    }

    @Override
    public List<CloudNativeConfigResult> cloudNativeConfigResults(CloudNativeConfigResultExample example) {
         return cloudNativeConfigResultMapper.selectByExample(example);
    }

    @Override
    public List<ServerResult> serverResults(ServerResultExample example) {
        return serverResultMapper.selectByExample(example);
    }

    @Override
    public List<ImageResult> imageResults(ImageResultExample example) {
        return imageResultMapper.selectByExample(example);
    }

    @Override
    public List<CodeResult> codeResults(CodeResultExample example) {
        return codeResultMapper.selectByExample(example);
    }

    @Override
    public List<FileSystemResult> fileSystemResults(FileSystemResultExample example) {
        return fileSystemResultMapper.selectByExample(example);
    }

    @Override
    public ServerResult serverResult(String id) {
        return serverResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public ServerRule serverRule(String id) {
        return serverRuleMapper.selectByPrimaryKey(id);
    }

    @Override
    public Server server(String id) {
        return serverMapper.selectByPrimaryKey(id);
    }

    @Override
    public ImageResult imageResult(String id) {
        return imageResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public ImageRule imageRule(String id) {
        return imageRuleMapper.selectByPrimaryKey(id);
    }

    @Override
    public Image image(String id) {
        return imageMapper.selectByPrimaryKey(id);
    }

    @Override
    public CodeResult codeResult(String id) {
        return codeResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public CodeRule codeRule(String id) {
        return codeRuleMapper.selectByPrimaryKey(id);
    }

    @Override
    public Code code(String id) {
        return codeMapper.selectByPrimaryKey(id);
    }

    @Override
    public CloudNativeResult cloudNativeResult(String id) {
        return cloudNativeResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public CloudNativeRule cloudNativeRule(String id) {
        return cloudNativeRuleMapper.selectByPrimaryKey(id);
    }

    @Override
    public CloudNative cloudNative(String id) {
        return cloudNativeMapper.selectByPrimaryKey(id);
    }

    @Override
    public CloudNativeConfigResult cloudNativeConfigResult(String id) {
        return cloudNativeConfigResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public CloudNativeConfigRule cloudNativeConfigRule(String id) {
        return configRuleMapper.selectByPrimaryKey(id);
    }

    @Override
    public CloudNativeConfig cloudNativeConfig(String id) {
        return configMapper.selectByPrimaryKey(id);
    }

    @Override
    public FileSystemResult fileSystemResult(String id) {
        return fileSystemResultMapper.selectByPrimaryKey(id);
    }

    @Override
    public FileSystemRule fileSystemRule(String id) {
        return fileSystemRuleMapper.selectByPrimaryKey(id);
    }

    @Override
    public FileSystem fileSystem(String id) {
        return fileSystemMapper.selectByPrimaryKey(id);
    }

    @Override
    public String serverRescan(String id) throws Exception {
        return serverService.rescan(id);
    }

    @Override
    public String codeRescan(String id) throws Exception {
        return codeService.reScan(id);
    }

    @Override
    public String imageRescan(String id) throws Exception {
        return imageService.reScan(id);
    }

    @Override
    public String k8sRescan(String id) throws Exception {
        return k8sService.reScan(id);
    }

    @Override
    public String configRescan(String id) throws Exception {
        return configService.reScan(id);
    }

    @Override
    public String fileSystemRescan(String id) throws Exception {
        return fileSystemService.reScan(id);
    }

    @Override
    public String serverGroupName(String id) {
        return serverGroupMapper.selectByPrimaryKey(id).getName();
    }

    @Override
    public void deleteServerResult(ServerResultExample example) {
        serverResultMapper.deleteByExample(example);
    }

    @Override
    public void insertServerResult(ServerResult result) {
        serverResultMapper.insertSelective(result);
    }

    @Override
    public void saveServerResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        serverService.saveServerResultLog(resultId, operation, output, result);
    }

    @Override
    public void deleteK8sResult(CloudNativeResultExample example) {
        cloudNativeResultMapper.deleteByExample(example);
    }

    @Override
    public void insertk8sResult(CloudNativeResultWithBLOBs result) {
        cloudNativeResultMapper.insertSelective(result);
    }

    @Override
    public void saveCloudNativeResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        k8sService.saveCloudNativeResultLog(resultId, operation, output, result);
    }

    @Override
    public void deleteConfigResult(CloudNativeConfigResultExample example) {
        cloudNativeConfigResultMapper.deleteByExample(example);
    }

    @Override
    public void insertConfigResult(CloudNativeConfigResult result) {
        cloudNativeConfigResultMapper.insertSelective(result);
    }

    @Override
    public void saveCloudNativeConfigResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        configService.saveCloudNativeConfigResultLog(resultId, operation, output, result);
    }

    @Override
    public void deleteCodeResult(CodeResultExample example) {
        codeResultMapper.deleteByExample(example);
    }

    @Override
    public void insertCodeResult(CodeResult result) {
        codeResultMapper.insertSelective(result);
    }

    @Override
    public void saveCodeResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        codeService.saveCodeResultLog(resultId, operation, output, result);
    }

    @Override
    public void deleteFileSystemResult(FileSystemResultExample example) {
        fileSystemResultMapper.deleteByExample(example);
    }

    @Override
    public void insertFileSystemResult(FileSystemResult result) {
        fileSystemResultMapper.insertSelective(result);
    }

    @Override
    public void saveFsResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        fileSystemService.saveFsResultLog(resultId, operation, output, result);
    }

    @Override
    public void deleteImageResult(ImageResultExample example) {
        imageResultMapper.deleteByExample(example);
    }

    @Override
    public void insertImageResult(ImageResultWithBLOBs result) {
        imageResultMapper.insertSelective(result);
    }

    @Override
    public void saveImageResultLog(String resultId, String operation, String output, boolean result) throws Exception {
        imageService.saveImageResultLog(resultId, operation, output, result);
    }


}
