package com.hummer.common.mapper.domain.request.fs;


import com.hummer.common.mapper.domain.FileSystemResult;

import java.util.Map;

/**
 * @author harris
 */
public class FsResultRequest extends FileSystemResult {

    private String name;

    private String fileName;

    private Map<String, Object> combine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }
}
