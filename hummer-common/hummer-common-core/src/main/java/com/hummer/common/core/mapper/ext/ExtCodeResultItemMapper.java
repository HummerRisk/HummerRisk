package com.hummer.common.core.mapper.ext;

import com.hummer.common.core.domain.CodeResultItemWithBLOBs;
import com.hummer.common.core.domain.request.code.CodeResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCodeResultItemMapper {

    List<CodeResultItemWithBLOBs> resultItemListBySearch(@Param("request") CodeResultItemRequest request);


}
