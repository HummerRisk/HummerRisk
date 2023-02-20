package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.FileSystemResultItemWithBLOBs;
import com.hummer.common.mapper.domain.request.fs.FsResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtFileSystemResultItemMapper {

    List<FileSystemResultItemWithBLOBs> resultItemListBySearch(@Param("request") FsResultItemRequest request);


}
