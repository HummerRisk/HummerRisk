package com.hummer.common.core.dto;



import com.hummer.common.core.domain.CodeResultItem;
import com.hummer.common.core.domain.HistoryCodeResult;

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
