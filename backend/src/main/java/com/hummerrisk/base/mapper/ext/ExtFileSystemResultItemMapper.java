package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.base.domain.FileSystemResultItemWithBLOBs;
import com.hummerrisk.controller.request.fs.FsResultItemRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtFileSystemResultItemMapper {

    List<FileSystemResultItemWithBLOBs> resultItemListBySearch(@Param("request") FsResultItemRequest request);


}
