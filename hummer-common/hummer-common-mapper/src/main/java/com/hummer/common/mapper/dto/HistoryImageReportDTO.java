package com.hummer.common.mapper.dto;



import com.hummer.common.mapper.domain.HistoryImageResult;
import com.hummer.common.mapper.domain.ImageResultItemWithBLOBs;

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
