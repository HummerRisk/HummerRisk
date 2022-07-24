package com.hummerrisk.dto;


import com.hummerrisk.base.domain.*;

import java.util.List;

/**
 * @author harris
 */
public class HistoryPackageReportDTO extends HistoryPackageTask {

    private List<PackageDependencyJson> packageDependencyJsonList;//结果json

    public List<PackageDependencyJson> getPackageDependencyJsonList() {
        return packageDependencyJsonList;
    }

    public void setPackageDependencyJsonList(List<PackageDependencyJson> packageDependencyJsonList) {
        this.packageDependencyJsonList = packageDependencyJsonList;
    }
}
