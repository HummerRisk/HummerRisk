package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.CodeResultItemWithBLOBs;
import com.hummerrisk.controller.request.code.CodeResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCodeResultItemMapper {

    List<CodeResultItemWithBLOBs> resultItemListBySearch(@Param("request") CodeResultItemRequest request);


}
