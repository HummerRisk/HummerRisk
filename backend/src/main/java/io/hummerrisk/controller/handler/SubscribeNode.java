package io.hummerrisk.controller.handler;


import java.io.Serializable;

public class SubscribeNode implements Serializable {

    private static final long serialVersionUID = -1680823237289721438L;

    private Long typeId;

    private Long channelId;

    public Boolean match(Long type, Long channel) {
        return type == typeId && channel == channelId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }
}
