package com.hummerrisk.commons.utils;

import com.hummerrisk.base.domain.Rule;

/**
 * @ClassName DashboardTarget
 * @Author harris
 **/
public class DashboardTarget extends Rule {

    private String ratio;

    private String reSum;

    private String ruSum;

    private String tagName;

    private String assets;

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public String getReSum() {
        return reSum;
    }

    public void setReSum(String reSum) {
        this.reSum = reSum;
    }

    public String getRuSum() {
        return ruSum;
    }

    public void setRuSum(String ruSum) {
        this.ruSum = ruSum;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getAssets() {
        return assets;
    }

    public void setAssets(String assets) {
        this.assets = assets;
    }
}
