package com.hummer.common.core.domain.request.k8s;

import java.util.List;

/**
 * harris
 */
public class K8sImage {

    private List<NameSpaceImage> nameSpaceImages;

    private Integer images;

    private Integer nameSpaces;

    private Integer riskController;

    private Integer controllers;

    private Integer critical;

    private Integer high;

    private Integer medium;

    private Integer low;

    private Integer unknown;

    private String k8sId;

    public List<NameSpaceImage> getNameSpaceImages() {
        return nameSpaceImages;
    }

    public void setNameSpaceImages(List<NameSpaceImage> nameSpaceImages) {
        this.nameSpaceImages = nameSpaceImages;
    }

    public Integer getNameSpaces() {
        return nameSpaces;
    }

    public void setNameSpaces(Integer nameSpaces) {
        this.nameSpaces = nameSpaces;
    }

    public Integer getRiskController() {
        return riskController;
    }

    public void setRiskController(Integer riskController) {
        this.riskController = riskController;
    }

    public Integer getControllers() {
        return controllers;
    }

    public void setControllers(Integer controllers) {
        this.controllers = controllers;
    }

    public Integer getCritical() {
        return critical;
    }

    public void setCritical(Integer critical) {
        this.critical = critical;
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getMedium() {
        return medium;
    }

    public void setMedium(Integer medium) {
        this.medium = medium;
    }

    public Integer getLow() {
        return low;
    }

    public void setLow(Integer low) {
        this.low = low;
    }

    public Integer getUnknown() {
        return unknown;
    }

    public void setUnknown(Integer unknown) {
        this.unknown = unknown;
    }

    public String getK8sId() {
        return k8sId;
    }

    public void setK8sId(String k8sId) {
        this.k8sId = k8sId;
    }

    public Integer getImages() {
        return images;
    }

    public void setImages(Integer images) {
        this.images = images;
    }
}
