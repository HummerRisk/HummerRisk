package com.hummer.system.mapper.ext;

import com.hummer.common.core.domain.*;
import com.hummer.common.core.dto.CodeResultDTO;
import com.hummer.common.core.dto.FsResultDTO;
import com.hummer.common.core.dto.ImageResultDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtAccountMapper {

    Account account(@Param("id") String id);

    List<CloudTask> cloudTaskList(@Param("id") String id);

    CloudNativeResult cloudNativeResult(@Param("id") String id);

    List<CloudNativeResultItem> cloudNativeResultItemList(@Param("id") String id);

    List<CloudNativeResultConfigItem> cloudNativeResultConfigItemList(@Param("id") String id);

    List<CloudNativeResultKubenchWithBLOBs> cloudNativeResultKubenchList(@Param("id") String id);

    Server server(@Param("id") String id);

    ServerLynisResult serverLynisResult(@Param("id") String id);

    List<ServerLynisResultDetail> serverLynisResultDetailList(@Param("id") String id);

    List<ServerResult> serverResultList(@Param("id") String id);

    ImageResultDTO imageResult(@Param("id") String id);

    List<ImageResultItem> imageResultItemList(@Param("id") String id);

    CloudNativeConfigResult cloudNativeConfigResult(@Param("id") String id);

    List<CloudNativeConfigResultItem> cloudNativeConfigResultItemList(@Param("id") String id);

    CodeResultDTO codeResult(@Param("id") String id);

    List<CodeResultItem> codeResultItemList(@Param("id") String id);

    FsResultDTO fileSystemResult(@Param("id") String id);

    List<FileSystemResultItem> fileSystemResultItemList(@Param("id") String id);

}
