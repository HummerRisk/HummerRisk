package com.hummer.k8s.api;

import com.hummer.common.core.domain.*;
import com.hummer.system.api.model.LoginUser;

import java.util.List;

public interface IK8sProviderService {

    List<CloudNativeResult> cloudNativeResults(CloudNativeResultExample example);

    List<CloudNativeConfigResult> cloudNativeConfigResults(CloudNativeConfigResultExample example);

    List<ServerResult> serverResults(ServerResultExample example);

    List<ImageResult> imageResults(ImageResultExample example);

    List<CodeResult> codeResults(CodeResultExample example);

    List<FileSystemResult> fileSystemResults(FileSystemResultExample example);

    ServerResult serverResult(String id);

    ServerRule serverRule(String id);

    Server server(String id);

    ImageResult imageResult(String id);

    ImageRule imageRule(String id);

    Image image(String id);

    CodeResult codeResult(String id);

    CodeRule codeRule(String id);

    Code code(String id);

    CloudNativeResult cloudNativeResult(String id);

    CloudNativeRule cloudNativeRule(String id);

    CloudNative cloudNative(String id);

    CloudNativeConfigResult cloudNativeConfigResult(String id);

    CloudNativeConfigRule cloudNativeConfigRule(String id);

    CloudNativeConfig cloudNativeConfig(String id);

    FileSystemResult fileSystemResult(String id);

    FileSystemRule fileSystemRule(String id);

    FileSystem fileSystem(String id);

    String serverRescan(String id, LoginUser loginUser) throws Exception;

    String codeRescan(String id, LoginUser loginUser) throws Exception;

    String imageRescan(String id, LoginUser logiUser) throws Exception;

    String k8sRescan(String id, LoginUser loginUser) throws Exception;

    String configRescan(String id, LoginUser loginUser) throws Exception;

    String fileSystemRescan(String id, LoginUser loginUser) throws Exception;

    String serverGroupName(String id);

    void deleteServerResult(ServerResultExample example);

    void insertServerResult(ServerResult result);

    void saveServerResultLog(String resultId, String operation, String output, boolean result, LoginUser logiUser) throws Exception;

    void deleteK8sResult(CloudNativeResultExample example);

    void insertk8sResult(CloudNativeResultWithBLOBs result);

    void saveCloudNativeResultLog(String resultId, String operation, String output, boolean result, LoginUser logiUser) throws Exception;

    void deleteConfigResult(CloudNativeConfigResultExample example);

    void insertConfigResult(CloudNativeConfigResult result);

    void saveCloudNativeConfigResultLog(String resultId, String operation, String output, boolean result, LoginUser logiUser) throws Exception;

    void deleteCodeResult(CodeResultExample example);

    void insertCodeResult(CodeResult result);

    void saveCodeResultLog(String resultId, String operation, String output, boolean result, LoginUser logiUser) throws Exception;

    void deleteFileSystemResult(FileSystemResultExample example);

    void insertFileSystemResult(FileSystemResult result);

    void saveFsResultLog(String resultId, String operation, String output, boolean result, LoginUser logiUser) throws Exception;

    void deleteImageResult(ImageResultExample example);

    void insertImageResult(ImageResultWithBLOBs result);

    void saveImageResultLog(String resultId, String operation, String output, boolean result, LoginUser logiUser) throws Exception;


}
