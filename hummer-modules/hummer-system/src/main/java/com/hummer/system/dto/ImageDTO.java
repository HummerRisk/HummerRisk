package com.hummer.system.dto;

import com.hummer.common.core.domain.ImageResultItem;
import com.hummer.common.core.dto.ImageResultDTO;

import java.util.List;

public class ImageDTO {

    private ImageResultDTO imageResult;

    private List<ImageResultItem> imageResultItemList;

    public ImageResultDTO getImageResult() {
        return imageResult;
    }

    public void setImageResult(ImageResultDTO imageResult) {
        this.imageResult = imageResult;
    }

    public List<ImageResultItem> getImageResultItemList() {
        return imageResultItemList;
    }

    public void setImageResultItemList(List<ImageResultItem> imageResultItemList) {
        this.imageResultItemList = imageResultItemList;
    }
}
