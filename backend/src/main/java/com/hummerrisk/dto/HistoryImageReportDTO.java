package com.hummerrisk.dto;


import com.hummerrisk.base.domain.HistoryImageResult;
import com.hummerrisk.base.domain.ImageResultItemWithBLOBs;

import java.util.List;

/**
 * @author harris
 */
public class HistoryImageReportDTO extends HistoryImageResult {

    private List<ImageResultItemWithBLOBs> imageResultItemWithBLOBsList;//镜像检测结果

    public List<ImageResultItemWithBLOBs> getImageResultItemWithBLOBsList() {
        return imageResultItemWithBLOBsList;
    }

    public void setImageResultItemWithBLOBsList(List<ImageResultItemWithBLOBs> imageResultItemWithBLOBsList) {
        this.imageResultItemWithBLOBsList = imageResultItemWithBLOBsList;
    }
}
