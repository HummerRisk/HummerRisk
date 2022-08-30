package com.hummerrisk.dto;


import com.hummerrisk.base.domain.HistoryImageTask;

/**
 * @author harris
 */
public class HistoryImageTaskDTO extends HistoryImageTask {

    private String type;

    private String imageUrl;

    private String imageTag;

    private String path;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageTag() {
        return imageTag;
    }

    public void setImageTag(String imageTag) {
        this.imageTag = imageTag;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
