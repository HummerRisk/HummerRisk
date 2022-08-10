package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.account.CloudAccountRequest;
import com.hummerrisk.controller.request.cloudNative.CloudNativeRequest;
import com.hummerrisk.dto.AccountDTO;
import com.hummerrisk.dto.CloudNativeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudNativeMapper {

    List<CloudNativeDTO> getCloudNativeList(@Param("request") CloudNativeRequest request);

}
