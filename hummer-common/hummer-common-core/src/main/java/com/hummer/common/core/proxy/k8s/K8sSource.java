package com.hummer.common.core.proxy.k8s;

import com.hummerrisk.base.domain.CloudNativeSourceImage;
import com.hummerrisk.base.domain.CloudNativeSourceWithBLOBs;

import java.util.ArrayList;
import java.util.List;

public class K8sSource {

    private List<CloudNativeSourceWithBLOBs> k8sSource = new ArrayList<>();

    private List<CloudNativeSourceImage> k8sSourceImage = new ArrayList<>();

    public List<CloudNativeSourceWithBLOBs> getK8sSource() {
        return k8sSource;
    }

    public void setK8sSource(List<CloudNativeSourceWithBLOBs> k8sSource) {
        this.k8sSource = k8sSource;
    }

    public List<CloudNativeSourceImage> getK8sSourceImage() {
        return k8sSourceImage;
    }

    public void setK8sSourceImage(List<CloudNativeSourceImage> k8sSourceImage) {
        this.k8sSourceImage = k8sSourceImage;
    }
}
