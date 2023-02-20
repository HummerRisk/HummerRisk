package com.hummer.common.mapper.dto;

import com.hummer.common.mapper.domain.Sbom;

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
