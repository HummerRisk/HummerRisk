package com.hummer.common.core.dto;



import com.hummer.common.core.domain.FileSystemResultItem;
import com.hummer.common.core.domain.HistoryFileSystemResult;

import java.util.List;

/**
 * @author harris
 */
public class HistoryFsReportDTO extends HistoryFileSystemResult {

    private List<FileSystemResultItem> fileSystemResultItemList;//结果json

    public List<FileSystemResultItem> getFileSystemResultItemList() {
        return fileSystemResultItemList;
    }

    public void setFileSystemResultItemList(List<FileSystemResultItem> fileSystemResultItemList) {
        this.fileSystemResultItemList = fileSystemResultItemList;
    }
}
