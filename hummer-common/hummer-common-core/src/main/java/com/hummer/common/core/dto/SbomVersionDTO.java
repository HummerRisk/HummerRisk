package com.hummer.common.core.dto;

import com.hummerrisk.base.domain.SbomVersion;


public class SbomVersionDTO extends SbomVersion {

    private String sbomName;

    private String sbomDesc;

    public String getSbomName() {
        return sbomName;
    }

    public void setSbomName(String sbomName) {
        this.sbomName = sbomName;
    }

    public String getSbomDesc() {
        return sbomDesc;
    }

    public void setSbomDesc(String sbomDesc) {
        this.sbomDesc = sbomDesc;
    }
}
