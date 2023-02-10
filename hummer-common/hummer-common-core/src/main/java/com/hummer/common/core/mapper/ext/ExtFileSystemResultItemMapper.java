package com.hummer.common.core.mapper.ext;

import com.hummer.common.core.domain.FileSystemResultItemWithBLOBs;
import com.hummer.common.core.domain.request.fs.FsResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtFileSystemResultItemMapper {

    List<FileSystemResultItemWithBLOBs> resultItemListBySearch(@Param("request") FsResultItemRequest request);


}
