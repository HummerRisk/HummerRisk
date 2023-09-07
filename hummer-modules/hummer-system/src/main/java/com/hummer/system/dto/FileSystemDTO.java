package com.hummer.system.dto;

import com.hummer.common.core.domain.FileSystemResultItem;
import com.hummer.common.core.dto.FsResultDTO;

import java.util.List;

public class FileSystemDTO {

    private FsResultDTO fileSystemResult;

    private List<FileSystemResultItem> fileSystemResultItemList;

    public FsResultDTO getFileSystemResult() {
        return fileSystemResult;
    }

    public void setFileSystemResult(FsResultDTO fileSystemResult) {
        this.fileSystemResult = fileSystemResult;
    }

    public List<FileSystemResultItem> getFileSystemResultItemList() {
        return fileSystemResultItemList;
    }

    public void setFileSystemResultItemList(List<FileSystemResultItem> fileSystemResultItemList) {
        this.fileSystemResultItemList = fileSystemResultItemList;
    }
}
