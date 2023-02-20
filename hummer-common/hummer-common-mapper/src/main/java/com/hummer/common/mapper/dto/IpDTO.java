package com.hummer.common.mapper.dto;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class IpDTO implements Serializable {

    @ApiModelProperty("IP地址")
    private String ip;

    @ApiModelProperty("子网掩码")
    private String mask;

    @ApiModelProperty("网关")
    private String gateway;

    @ApiModelProperty("DNS1")
    private String dns1;

    @ApiModelProperty("DNS2")
    private String dns2;

    @ApiModelProperty("ip类型: ipv4 与 ipv6")
    private String ipType;

    @ApiModelProperty("ip网段ID")
    private String ipPartId;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDns1() {
        return dns1;
    }

    public void setDns1(String dns1) {
        this.dns1 = dns1;
    }

    public String getDns2() {
        return dns2;
    }

    public void setDns2(String dns2) {
        this.dns2 = dns2;
    }

    public String getIpType() {
        return ipType;
    }

    public void setIpType(String ipType) {
        this.ipType = ipType;
    }

    public String getIpPartId() {
        return ipPartId;
    }

    public void setIpPartId(String ipPartId) {
        this.ipPartId = ipPartId;
    }
}
