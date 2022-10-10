package com.hummerrisk.commons.utils;

import com.hummerrisk.dto.IpDTO;
import com.hummerrisk.i18n.Translator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.SubnetUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IpUtil {
    private static final int TIME_OUT = 3000;

    public static List<String> getIpRange(String startIp, String endIp, String gateway, String mask) {
        List<String> result = new ArrayList<>();
        SubnetUtils utils = new SubnetUtils(gateway, mask);
        SubnetUtils.SubnetInfo info = utils.getInfo();
        String[] allIps = info.getAllAddresses();
        for (String allIp : allIps) {
            if (ipInRange(allIp, startIp, endIp)) {
                result.add(allIp);
            }
        }
        return result;
    }

    private static Boolean ipInRange(String ip, String startIp, String endIp) {
        String ipSection = startIp + "-" + endIp;
        ip = ip.trim();
        final String REGX_IP = "((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)";
        final String REGX_IPB = REGX_IP + "\\-" + REGX_IP;
        if (!ipSection.matches(REGX_IPB) || !ip.matches(REGX_IP))
            return false;
        int idx = ipSection.indexOf('-');
        String[] sips = ipSection.substring(0, idx).split("\\.");
        String[] sipe = ipSection.substring(idx + 1).split("\\.");
        String[] sipt = ip.split("\\.");
        long ips = 0L, ipe = 0L, ipt = 0L;
        for (int i = 0; i < 4; ++i) {
            ips = ips << 8 | Integer.parseInt(sips[i]);
            ipe = ipe << 8 | Integer.parseInt(sipe[i]);
            ipt = ipt << 8 | Integer.parseInt(sipt[i]);
        }
        if (ips > ipe) {
            long t = ips;
            ips = ipe;
            ipe = t;
        }
        return ips <= ipt && ipt <= ipe;
    }

    public static IpDTO getConnectableIp(List<IpDTO> ips, String policy) {
        if (CollectionUtils.isEmpty(ips)) {
            return null;
        }
        switch (policy){
            case "Random":
                //打乱列表顺序
                Collections.shuffle(ips);
                for (IpDTO ip : ips) {
                    try {
                        //返回不可达的IP
                        if (!InetAddress.getByName(ip.getIp()).isReachable(TIME_OUT)) {
                            return ip;
                        }
                    } catch (IOException ignore) {
                        //忽略异常
                    }
                }
                break;
            case "BigToSmall":
                for (IpDTO ip : ips) {
                    try {
                        //返回不可达的IP
                        if (!InetAddress.getByName(ip.getIp()).isReachable(TIME_OUT)) {
                            return ip;
                        }
                    } catch (IOException ignore) {
                        //忽略异常
                    }
                }
                break;
            case "SmallToBig":
                for (IpDTO ip : ips) {
                    try {
                        //返回不可达的IP
                        if (!InetAddress.getByName(ip.getIp()).isReachable(TIME_OUT)) {
                            return ip;
                        }
                    } catch (IOException ignore) {
                        //忽略异常
                    }
                }
                break;
        }
        return null;
    }

    public static String getConnectableIpv6(String ipv6) {
        if (StringUtils.isEmpty(ipv6)) {
            return null;
        }
        try {
            //返回不可达的IP
            if (!Inet6Address.getByName(ipv6).isReachable(TIME_OUT)) {
                return ipv6;
            }
        } catch (IOException ignore) {
            //忽略异常
        }
        return null;
    }

    public static boolean isValidIpv6Addr(String ipStr){
//        boolean iPv6LiteralAddress = IPAddressUtil.isIPv6LiteralAddress(ipStr);
//        if (!iPv6LiteralAddress){
//            return false;
//        }
        return true;
    }

    public static BigInteger countIpv6(String startIp, String endIp) throws Exception{
        if(ipv6ToNumber(startIp).compareTo(ipv6ToNumber(endIp)) > (-1)){
            throw new Exception(Translator.get("i18n_start_end_ip_part"));
        }
        return ipv6ToNumber(endIp).subtract(ipv6ToNumber(startIp)).add(BigInteger.valueOf(1));
    }

    public static String getIpv6ByIpPart(BigInteger num){
        return numberToIPv6(num);
    }

    public static BigInteger ipv6ToNumber(String addr) {
        addr = ipv6Trans(addr);
        int startIndex=addr.indexOf("::");

        if(startIndex!=-1){


            String firstStr=addr.substring(0,startIndex);
            String secondStr=addr.substring(startIndex+2, addr.length());


            BigInteger first=ipv6ToNumber(firstStr);

            int x=countChar(addr, ':');

            if(secondStr.isEmpty()){
                first=first.shiftLeft(16*(7-x)).add(countN(x));
            }else{
                first=first.shiftLeft(16*(7-x)).add(ipv6ToNumber(secondStr));
            }

            return first;
        }


        String[] strArr = addr.split(":");

        BigInteger retValue = BigInteger.valueOf(0);
        for (int i=0;i<strArr.length;i++) {
            BigInteger bi=new BigInteger(strArr[i], 16);
            retValue = retValue.shiftLeft(16).add(bi);
        }
        return retValue;
    }

    public static BigInteger countN(int x){
        BigInteger count = BigInteger.valueOf(1);
        for(int n = 0; n < (7-x); n++){
            count = count.multiply(BigInteger.valueOf(65536));
        }
        return count;
    }

    public static String numberToIPv6(BigInteger ipNumber) {
        String ipString ="";
        BigInteger a=new BigInteger("FFFF", 16);

        for (int i=0; i<8; i++) {
            ipString=ipNumber.and(a).toString(16)+":"+ipString;

            ipNumber = ipNumber.shiftRight(16);
        }

        return ipString.substring(0, ipString.length()-1);

    }

    public static int countChar(String str, char reg){
        char[] ch=str.toCharArray();
        int count=0;
        for(int i=0; i<ch.length; ++i){
            if(ch[i]==reg){
                if(ch[i+1]==reg){
                    ++i;
                    continue;
                }
                ++count;
            }
        }
        return count;
    }

    public static String ipv6Trans (String abbreviation){
        String fullIPv6 = "";
        if ("::".equals(abbreviation)) {
            return "0000:0000:0000:0000:0000:0000:0000:0000";
        }
        String[] arr = { "0000", "0000", "0000", "0000", "0000", "0000", "0000", "0000" };
        if (abbreviation.startsWith("::")){
            String[] temp = abbreviation.substring(2, abbreviation.length()).split(":");
            for (int i = 0; i < temp.length; i++){
                String tempStr = "0000" + temp[i];
                arr[i + 8 - temp.length] = tempStr.substring(tempStr.length() - 4);
            }
        }else if (abbreviation.endsWith("::")){
            String[] temp = abbreviation.substring(0, abbreviation.length() - 2).split(":");
            for (int i = 0; i < temp.length; i++){
                String tempStr = "0000" + temp[i];
                arr[i] = tempStr.substring(tempStr.length() - 4);
            }
        }else if (abbreviation.contains("::")){
            String[] tempArr = abbreviation.split("::");
            String[] temp0 = tempArr[0].split(":");
            for (int i = 0; i < temp0.length; i++){
                String tempStr = "0000" + temp0[i];
                arr[i] = tempStr.substring(tempStr.length() - 4);
            }
            String[] temp1 = tempArr[1].split(":");
            for (int i = 0; i < temp1.length; i++){
                String tempStr = "0000" + temp1[i];
                arr[i + 8 - temp1.length] = tempStr.substring(tempStr.length() - 4);
            }
        }else{
            String[] tempArr = abbreviation.split(":");
            for (int i = 0; i < tempArr.length; i++) {
                String tempStr = "0000" + tempArr[i];
                arr[i] = tempStr.substring(tempStr.length() - 4);
            }
        }

        fullIPv6 = StringUtils.join(arr, ":");
        return fullIPv6;
    }

    /**
     * IPv6地址转换成数字
     * @param ip
     * @return
     */
    public BigInteger ipv6ToNumberMethod2(String ip) {
        ip = ipv6Trans(ip);
        String[] ips = ip.split(":");
        BigInteger rs = new BigInteger("0");

        for (int i = 0; i < ips.length; i++) {
            BigInteger a = BigInteger.valueOf(Integer.parseInt(ips[i], 16));
            BigInteger b = BigInteger.valueOf(65536).pow(7 - i);
            BigInteger c = a.multiply(b);
            rs = rs.add(c);
        }
        return rs;
    }

    /**
     * 数字转换成IPV6地址
     * @param number
     * @return
     */
    public String numberToIpv6Method2(BigInteger number) {
        String ip = "";
        List<String> ips = new ArrayList<String>();

        for (int i = 0; i < 8; i++) {
            ips.add(Integer.toHexString(number.divideAndRemainder(BigInteger.valueOf(65536))[1].intValue()));
            number = number.shiftRight(16);
        }

        for (int i = ips.size() - 1; i >= 0; i--) {
            ip = ip.concat(ips.get(i));
            if (i > 0) {
                ip = ip.concat(":");
            }
        }
        return ip;
    }

    /**
     * 查找两个IP地址之间的IP
     * @param startIp
     * @param endIp
     * @return
     */
    public List<String> findIPs(String startIp, String endIp) {
        BigInteger startNumber = this.ipv6ToNumberMethod2(startIp);
        BigInteger endNumber = this.ipv6ToNumberMethod2(endIp).add(BigInteger.valueOf(1));
        List<String> ips = new ArrayList<String>();
        while (startNumber.compareTo(endNumber) < 0) {
            ips.add(this.numberToIpv6Method2(startNumber));
            startNumber = startNumber.add(BigInteger.valueOf(1));
        }
        return ips;
    }
}
