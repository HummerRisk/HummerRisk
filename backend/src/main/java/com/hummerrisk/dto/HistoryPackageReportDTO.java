package com.hummerrisk.dto;


import com.hummerrisk.base.domain.HistoryPackageTask;
import com.hummerrisk.base.domain.PackageDependencyJsonWithBLOBs;

import java.util.List;

/**
 * @author harris
 */
public class HistoryPackageReportDTO extends HistoryPackageTask {

    private List<PackageDependencyJsonWithBLOBs> packageDependencyJsonList;//结果json

    public List<PackageDependencyJsonWithBLOBs> getPackageDependencyJsonList() {
        return packageDependencyJsonList;
    }

    public void setPackageDependencyJsonList(List<PackageDependencyJsonWithBLOBs> packageDependencyJsonList) {
        this.packageDependencyJsonList = packageDependencyJsonList;
    }
}
