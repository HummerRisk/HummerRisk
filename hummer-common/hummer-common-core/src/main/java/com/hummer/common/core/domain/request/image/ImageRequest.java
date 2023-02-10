package com.hummer.common.core.domain.request.image;

import com.hummer.common.core.domain.Image;

import java.util.Map;

/**
 * harris
 */
public class ImageRequest extends Image {

    private String imageRepoName;

    private String repo;

    private String repoItemId;

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

    public String getRepoItemId() {
        return repoItemId;
    }

    public void setRepoItemId(String repoItemId) {
        this.repoItemId = repoItemId;
    }

    private Map<String, Object> combine;

    public Map<String, Object> getCombine() {
        return combine;
    }

    public void setCombine(Map<String, Object> combine) {
        this.combine = combine;
    }

}
