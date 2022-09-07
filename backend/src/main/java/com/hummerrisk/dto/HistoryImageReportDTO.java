package com.hummerrisk.dto;


import com.hummerrisk.base.domain.HistoryImageTask;
import com.hummerrisk.base.domain.ImageTrivyJsonWithBLOBs;

import java.util.List;

/**
 * @author harris
 */
public class HistoryImageReportDTO extends HistoryImageTask {

    private List<ImageTrivyJsonWithBLOBs> imageTrivyJsonWithBLOBsList;//镜像检测结果

    public List<ImageTrivyJsonWithBLOBs> getImageTrivyJsonWithBLOBsList() {
        return imageTrivyJsonWithBLOBsList;
    }

    public void setImageTrivyJsonWithBLOBsList(List<ImageTrivyJsonWithBLOBs> imageTrivyJsonWithBLOBsList) {
        this.imageTrivyJsonWithBLOBsList = imageTrivyJsonWithBLOBsList;
    }
}
