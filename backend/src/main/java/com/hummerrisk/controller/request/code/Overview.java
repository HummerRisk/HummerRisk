package com.hummerrisk.controller.request.code;

import java.util.List;
import java.util.Map;

/**
 * harris
 */
public class Overview {

    private List<String> dimensions;

    private List<Map<String, Object>> source;

    public List<String> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<String> dimensions) {
        this.dimensions = dimensions;
    }

    public List<Map<String, Object>> getSource() {
        return source;
    }

    public void setSource(List<Map<String, Object>> source) {
        this.source = source;
    }
}
