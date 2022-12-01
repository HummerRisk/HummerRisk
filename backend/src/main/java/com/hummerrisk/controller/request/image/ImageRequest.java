package com.hummerrisk.controller.request.image;

import com.hummerrisk.base.domain.Image;

import java.util.Map;

/**
 * harris
 */
public class ImageRequest extends Image {

    private String imageRepoName;

    private String repo;

    public String getImageRepoName() {
        return imageRepoName;
    }

    public void setImageRepoName(String imageRepoName) {
        this.imageRepoName = imageRepoName;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
