package com.hummerrisk.dto;


import com.hummerrisk.base.domain.FileSystemResultItem;
import com.hummerrisk.base.domain.HistoryFileSystemResult;

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
