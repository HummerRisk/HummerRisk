package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.CodeResultItemWithBLOBs;
import com.hummer.common.mapper.domain.request.code.CodeResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCodeResultItemMapper {

    List<CodeResultItemWithBLOBs> resultItemListBySearch(@Param("request") CodeResultItemRequest request);


}
