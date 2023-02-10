package com.hummer.common.core.dto;


import com.hummerrisk.base.domain.CodeResultItem;
import com.hummerrisk.base.domain.HistoryCodeResult;

import java.util.List;

/**
 * @author harris
 */
public class HistoryCodeReportDTO extends HistoryCodeResult {

    private List<CodeResultItem> codeResultItemList;//结果json

    public List<CodeResultItem> getCodeResultItemList() {
        return codeResultItemList;
    }

    public void setCodeResultItemList(List<CodeResultItem> codeResultItemList) {
        this.codeResultItemList = codeResultItemList;
    }
}
