package com.hummerrisk.dto;


import com.hummerrisk.base.domain.*;

import java.util.List;

/**
 * @author harris
 */
public class HistoryImageReportDTO extends HistoryImageTask {

    private List<ImageGrypeTable> imageGrypeTableList;//镜像检测grype结果table

    private List<ImageGrypeJson> imageGrypeJsonList;//镜像检测grype结果json

    private List<ImageSyftTable> imageSyftTableList;//镜像检测syft sbom结果table

    private List<ImageSyftJson> imageSyftJsonList;//镜像检测syft sbom结果json

    public List<ImageGrypeTable> getImageGrypeTableList() {
        return imageGrypeTableList;
    }

    public void setImageGrypeTableList(List<ImageGrypeTable> imageGrypeTableList) {
        this.imageGrypeTableList = imageGrypeTableList;
    }

    public List<ImageGrypeJson> getImageGrypeJsonList() {
        return imageGrypeJsonList;
    }

    public void setImageGrypeJsonList(List<ImageGrypeJson> imageGrypeJsonList) {
        this.imageGrypeJsonList = imageGrypeJsonList;
    }

    public List<ImageSyftTable> getImageSyftTableList() {
        return imageSyftTableList;
    }

    public void setImageSyftTableList(List<ImageSyftTable> imageSyftTableList) {
        this.imageSyftTableList = imageSyftTableList;
    }

    public List<ImageSyftJson> getImageSyftJsonList() {
        return imageSyftJsonList;
    }

    public void setImageSyftJsonList(List<ImageSyftJson> imageSyftJsonList) {
        this.imageSyftJsonList = imageSyftJsonList;
    }
}
