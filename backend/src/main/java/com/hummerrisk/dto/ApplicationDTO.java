package com.hummerrisk.dto;

import com.hummerrisk.base.domain.Sbom;
import com.hummerrisk.base.domain.SbomVersion;

import java.util.List;


public class ApplicationDTO extends Sbom {

    private List<SbomVersionDTO> sbomVersionList;

    public List<SbomVersionDTO> getSbomVersionList() {
        return sbomVersionList;
    }

    public void setSbomVersionList(List<SbomVersionDTO> sbomVersionList) {
        this.sbomVersionList = sbomVersionList;
    }
}
