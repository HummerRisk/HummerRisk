package com.hummer.system.dto;

import com.hummer.common.core.domain.CodeResultItem;
import com.hummer.common.core.dto.CodeResultDTO;

import java.util.List;

public class CodeDTO {

    private CodeResultDTO codeResult;

    private List<CodeResultItem> codeResultItemList;

    public CodeResultDTO getCodeResult() {
        return codeResult;
    }

    public void setCodeResult(CodeResultDTO codeResult) {
        this.codeResult = codeResult;
    }

    public List<CodeResultItem> getCodeResultItemList() {
        return codeResultItemList;
    }

    public void setCodeResultItemList(List<CodeResultItem> codeResultItemList) {
        this.codeResultItemList = codeResultItemList;
    }
}
