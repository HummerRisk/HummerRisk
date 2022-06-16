package com.hummerrisk.commons.constants;

import java.util.HashMap;
import java.util.Map;

public class RegionsConstants {

    public static Map<String, String> AwsMap;

    public static Map<String, String> AzureMap;

    public static Map<String, String> AliyunMap;

    public static Map<String, String> HuaweiMap;

    public static Map<String, String> TencentMap;

    public static Map<String, String> GcpMap;

    public static Map<String, String> BaiduMap;

    public static Map<String, String> QiniuMap;

    public static Map<String, String> QingcloudMap;

    public static Map<String, String> UcloudMap;

    static
    {
        AwsMap = new HashMap<>();
        // AWS 中国 start
        AwsMap.put("cn-north-1", "中国（北京）");
        AwsMap.put("cn-northwest-1", "中国（宁夏）");
        // AWS 中国 end

        // AWS 国际 start
        AwsMap.put("us-east-2", "美国东部（俄亥俄州）");
        AwsMap.put("us-east-1", "美国东部（弗吉尼亚北部）");
        AwsMap.put("us-west-1", "美国西部（加利福尼亚北部）");
        AwsMap.put("us-west-2", "美国西部（俄勒冈）");
        AwsMap.put("af-south-1", "非洲（开普敦）");
        AwsMap.put("ap-east-1", "亚太地区（香港）");
        AwsMap.put("ap-south-1", "亚太地区（孟买）");
        AwsMap.put("ap-northeast-3", "亚太区域（大阪当地）");
        AwsMap.put("ap-northeast-2", "亚太区域（首尔）");
        AwsMap.put("ap-southeast-1", "亚太区域（新加坡）");
        AwsMap.put("ap-southeast-2", "亚太区域（悉尼）");
        AwsMap.put("ap-northeast-1", "亚太区域（东京）");
        AwsMap.put("ca-central-1", "加拿大（中部）");
        AwsMap.put("eu-central-1", "欧洲（法兰克福）");
        AwsMap.put("eu-west-1", "欧洲（爱尔兰）");
        AwsMap.put("eu-west-2", "欧洲（伦敦）");
        AwsMap.put("eu-south-1", "欧洲（米兰）");
        AwsMap.put("eu-west-3", "欧洲（巴黎）");
        AwsMap.put("eu-north-1", "欧洲（斯德哥尔摩）");
        AwsMap.put("me-south-1", "中东（巴林）");
        AwsMap.put("sa-east-1", "南美洲（圣保罗）");
        // AWS 国际 end

        AzureMap = new HashMap<>();
        // Azure start
        AzureMap.put("AzureChinaCloud", "中国区");
        AzureMap.put("AzureCloud", "国际区");
        // Azure end

        AliyunMap = new HashMap<>();
        // Aliyun start
        AliyunMap.put("cn-qingdao", "华北 1（青岛）");
        AliyunMap.put("cn-beijing", "华北 2（北京）");
        AliyunMap.put("cn-zhangjiakou", "华北 3（张家口）");
        AliyunMap.put("cn-huhehaote", "华北 5（呼和浩特）");
        AliyunMap.put("cn-wulanchabu", "华北6（乌兰察布）");
        AliyunMap.put("cn-hangzhou", "华东 1（杭州）");
        AliyunMap.put("cn-shanghai", "华东 2（上海）");
        AliyunMap.put("cn-nanjing", "华东 5（南京）");
        AliyunMap.put("cn-shenzhen", "华南 1（深圳）");
        AliyunMap.put("cn-heyuan", "华南2（河源）");
        AliyunMap.put("cn-guangzhou", "华南3（广州）");
        AliyunMap.put("cn-chengdu", "西南1（成都）");
        AliyunMap.put("cn-hongkong", "中国香港（香港）");
        AliyunMap.put("ap-northeast-1", "亚太东北 1 (东京)");
        AliyunMap.put("ap-southeast-1", "亚太东南 1 (新加坡)");
        AliyunMap.put("ap-southeast-2", "亚太东南 2 (悉尼)");
        AliyunMap.put("ap-southeast-3", "亚太东南 3 (吉隆坡)");
        AliyunMap.put("ap-southeast-5", "亚太东南 5 (雅加达)");
        AliyunMap.put("ap-south-1", "亚太南部 1 (孟买)");
        AliyunMap.put("us-east-1", "美国东部 1 (弗吉尼亚)");
        AliyunMap.put("us-west-1", "美国西部 1 (硅谷)");
        AliyunMap.put("eu-west-1", "英国 (伦敦)");
        AliyunMap.put("me-east-1", "中东东部 1 (迪拜)");
        AliyunMap.put("eu-central-1", "欧洲中部 1 (法兰克福)");
        // Aliyun end

        HuaweiMap = new HashMap<>();
        // Huawei start
        HuaweiMap.put("af-south-1", "非洲-约翰内斯堡");
        HuaweiMap.put("ap-southeast-1", "亚太-香港");
        HuaweiMap.put("ap-southeast-2", "亚太-曼谷");
        HuaweiMap.put("ap-southeast-3", "亚太-新加坡");
        HuaweiMap.put("cn-northeast-1", "东北-大连");
        HuaweiMap.put("cn-east-2", "华东-上海二");
        HuaweiMap.put("cn-east-3", "华东-上海一");
        HuaweiMap.put("cn-north-1", "华北-北京一");
        HuaweiMap.put("cn-north-4", "华北-北京四");
        HuaweiMap.put("cn-south-1", "华南-广州");
        HuaweiMap.put("cn-southwest-2", "西南-贵阳一");
        HuaweiMap.put("la-south-2", "拉美-圣地亚哥");
        HuaweiMap.put("sa-brazil-1", "拉美-圣保罗一");
        HuaweiMap.put("na-mexico-1", "拉美-墨西哥城一");
        // Huawei end

        TencentMap = new HashMap<>();
        // Tencent start
        TencentMap.put("ap-bangkok", "亚太地区(曼谷)");
        TencentMap.put("ap-beijing", "华北地区(北京)");
        TencentMap.put("ap-chengdu", "西南地区(成都)");
        TencentMap.put("ap-chongqing", "西南地区(重庆)");
        TencentMap.put("ap-guangzhou", "华南地区(广州)");
        TencentMap.put("ap-guangzhou-open", "华南地区(广州Open)");
        TencentMap.put("ap-hongkong", "港澳台地区(中国香港)");
        TencentMap.put("ap-mumbai", "亚太地区(孟买)");
        TencentMap.put("ap-nanjing", "华东地区(南京)");
        TencentMap.put("ap-seoul", "亚太地区(首尔)");
        TencentMap.put("ap-shanghai", "华东地区(上海)");
        TencentMap.put("ap-shanghai-fsi", "华东地区(上海金融)");
        TencentMap.put("ap-shenzhen-fsi", "华南地区(深圳金融)");
        TencentMap.put("ap-singapore", "东南亚地区(新加坡)");
        TencentMap.put("ap-tokyo", "亚太地区(东京)");
        TencentMap.put("eu-frankfurt", "欧洲地区(法兰克福)");
        TencentMap.put("eu-moscow", "欧洲地区(莫斯科)");
        TencentMap.put("na-ashburn", "美国东部(弗吉尼亚)");
        TencentMap.put("na-siliconvalley", "美国西部(硅谷)");
        TencentMap.put("na-toronto", "北美地区(多伦多)");
        // Tencent end

        GcpMap = new HashMap<>();
        // GCP 国际 start
        GcpMap.put("asia-east1-a", "亚太地区台湾彰化a");
        GcpMap.put("asia-east1-b", "亚太地区台湾彰化b");
        GcpMap.put("asia-east1-c", "亚太地区台湾彰化c");
        GcpMap.put("asia-east2-a", "亚太地区香港a");
        GcpMap.put("asia-east2-b", "亚太地区香港b");
        GcpMap.put("asia-east2-c", "亚太地区香港c");
        GcpMap.put("asia-northeast1-a", "亚太地区日本东京a");
        GcpMap.put("asia-northeast1-b", "亚太地区日本东京b");
        GcpMap.put("asia-northeast1-c", "亚太地区日本东京c");
        GcpMap.put("asia-northeast2-a", "亚太地区日本大阪a");
        GcpMap.put("asia-northeast2-b", "亚太地区日本大阪b");
        GcpMap.put("asia-northeast2-c", "亚太地区日本大阪c");
        GcpMap.put("asia-northeast3-a", "亚太地区韩国首尔a");
        GcpMap.put("asia-northeast3-b", "亚太地区韩国首尔b");
        GcpMap.put("asia-northeast3-c", "亚太地区韩国首尔c");
        GcpMap.put("asia-south1-a", "亚太地区印度孟买a");
        GcpMap.put("asia-south1-b", "亚太地区印度孟买b");
        GcpMap.put("asia-south1-c", "亚太地区印度孟买c");
        GcpMap.put("asia-southeast1-a", "亚太地区新加坡裕廊西a");
        GcpMap.put("asia-southeast1-b", "亚太地区新加坡裕廊西b");
        GcpMap.put("asia-southeast1-c", "亚太地区新加坡裕廊西c");
        GcpMap.put("asia-southeast2-a", "亚太地区印度尼西亚雅加达a");
        GcpMap.put("asia-southeast2-b", "亚太地区印度尼西亚雅加达b");
        GcpMap.put("asia-southeast2-c", "亚太地区印度尼西亚雅加达c");
        GcpMap.put("australia-southeast1-a", "亚太地区澳大利亚悉尼a");
        GcpMap.put("australia-southeast1-b", "亚太地区澳大利亚悉尼b");
        GcpMap.put("australia-southeast1-c", "亚太地区澳大利亚悉尼c");
        GcpMap.put("europe-central2-a", "欧洲波兰华沙a");
        GcpMap.put("europe-central2-b", "欧洲波兰华沙b");
        GcpMap.put("europe-central2-c", "欧洲波兰华沙c");
        GcpMap.put("europe-north1-a", "欧洲芬兰哈米纳a");
        GcpMap.put("europe-north1-b", "欧洲芬兰哈米纳b");
        GcpMap.put("europe-north1-c", "欧洲芬兰哈米纳c");
        GcpMap.put("europe-west1-b", "欧洲比利时圣吉斯兰b");
        GcpMap.put("europe-west1-c", "欧洲比利时圣吉斯兰c");
        GcpMap.put("europe-west1-d", "欧洲比利时圣吉斯兰d");
        GcpMap.put("europe-west2-a", "欧洲英国伦敦a");
        GcpMap.put("europe-west2-b", "欧洲英国伦敦b");
        GcpMap.put("europe-west2-c", "欧洲英国伦敦c");
        GcpMap.put("europe-west3-a", "欧洲德国法兰克福a");
        GcpMap.put("europe-west3-b", "欧洲德国法兰克福b");
        GcpMap.put("europe-west3-c", "欧洲德国法兰克福c");
        GcpMap.put("europe-west4-a", "欧洲荷兰埃姆斯哈文a");
        GcpMap.put("europe-west4-b", "欧洲荷兰埃姆斯哈文b");
        GcpMap.put("europe-west4-c", "欧洲荷兰埃姆斯哈文c");
        GcpMap.put("europe-west6-a", "欧洲瑞士苏黎世a");
        GcpMap.put("europe-west6-b", "欧洲瑞士苏黎世b");
        GcpMap.put("europe-west6-c", "欧洲瑞士苏黎世c");
        GcpMap.put("northamerica-northeast1-a", "北美洲魁北克省蒙特利尔a");
        GcpMap.put("northamerica-northeast1-b", "北美洲魁北克省蒙特利尔b");
        GcpMap.put("northamerica-northeast1-c", "北美洲魁北克省蒙特利尔c");
        GcpMap.put("southamerica-east1-a", "南美洲巴西圣保罗省奥萨斯库a");
        GcpMap.put("southamerica-east1-b", "南美洲巴西圣保罗省奥萨斯库b");
        GcpMap.put("southamerica-east1-c", "南美洲巴西圣保罗省奥萨斯库c");
        GcpMap.put("us-central1-a", "北美洲爱荷华州康瑟布拉夫斯a");
        GcpMap.put("us-central1-b", "北美洲爱荷华州康瑟布拉夫斯b");
        GcpMap.put("us-central1-c", "北美洲爱荷华州康瑟布拉夫斯c");
        GcpMap.put("us-central1-f", "北美洲爱荷华州康瑟布拉夫斯f");
        GcpMap.put("us-east1-b", "北美洲南卡罗来纳州蒙克斯科纳b");
        GcpMap.put("us-east1-c", "北美洲南卡罗来纳州蒙克斯科纳c");
        GcpMap.put("us-east1-d", "北美洲南卡罗来纳州蒙克斯科纳a");
        GcpMap.put("us-east4-a", "北美洲弗吉尼亚阿什本a");
        GcpMap.put("us-east4-b", "北美洲弗吉尼亚阿什本b");
        GcpMap.put("us-east4-c", "北美洲弗吉尼亚阿什本c");
        GcpMap.put("us-west1-a", "北美洲俄勒冈州达尔斯a");
        GcpMap.put("us-west1-b", "北美洲俄勒冈州达尔斯b");
        GcpMap.put("us-west1-c", "北美洲俄勒冈州达尔斯c");
        GcpMap.put("us-west2-a", "北美洲加利福尼亚州洛杉矶a");
        GcpMap.put("us-west2-b", "北美洲加利福尼亚州洛杉矶b");
        GcpMap.put("us-west2-c", "北美洲加利福尼亚州洛杉矶c");
        GcpMap.put("us-west3-a", "北美洲犹他州盐湖城a");
        GcpMap.put("us-west3-b", "北美洲犹他州盐湖城b");
        GcpMap.put("us-west3-c", "北美洲犹他州盐湖城c");
        GcpMap.put("us-west4-a", "北美洲内华达州拉斯维加斯a");
        GcpMap.put("us-west4-b", "北美洲内华达州拉斯维加斯b");
        GcpMap.put("us-west4-c", "北美洲内华达州拉斯维加斯c");
        // GCP 国际 end

        BaiduMap = new HashMap<>();
        // Baidu start
        BaiduMap.put("default", "默认");
        BaiduMap.put("bj", "华北-北京");
        BaiduMap.put("bd", "华北-保定");
        BaiduMap.put("hbfsg", "华北-度小满金融专区");
        BaiduMap.put("su", "华东-苏州");
        BaiduMap.put("gz", "华南-广州");
        BaiduMap.put("fsh", "金融华东-上海");
        BaiduMap.put("fwh", "金融华中-武汉");
        BaiduMap.put("hkg", "香港");
        BaiduMap.put("sin", "新加坡");

        QiniuMap = new HashMap<>();
        // Qiniu start
        QiniuMap.put("z0", "华东");
        QiniuMap.put("z1", "华北");
        QiniuMap.put("z2", "华南");
        QiniuMap.put("na0", "北美");
        QiniuMap.put("as0", "东南亚");
        QiniuMap.put("fog-cn-east-1", "华东-1");
        QiniuMap.put("fog-cn-east-2", "华东-浙江2");

        QingcloudMap = new HashMap<>();
        // Baidu start
        QingcloudMap.put("PEK3", "北京3");
        QingcloudMap.put("GD2", "广东2");
        QingcloudMap.put("SH1", "上海1");
        QingcloudMap.put("AP2", "亚太2");
        QingcloudMap.put("AP3", "雅加达区");

        UcloudMap = new HashMap<>();
        // Ucloud start
        UcloudMap.put("cn-bj2", "华北一");
        UcloudMap.put("cn-gd", "广州");
        UcloudMap.put("cn-qz", "福建");
        UcloudMap.put("cn-sh2", "上海二");
        UcloudMap.put("cn-wlcb", "华北二");
        UcloudMap.put("afr-nigeria", "拉各斯");
        UcloudMap.put("bra-saopaulo", "圣保罗");
        UcloudMap.put("ge-fra", "法兰克福");
        UcloudMap.put("hk", "香港");
        UcloudMap.put("idn-jakarta", "雅加达");
        UcloudMap.put("ind-mumbai", "孟买");
        UcloudMap.put("jpn-tky", "东京");
        UcloudMap.put("kr-seoul", "首尔");
        UcloudMap.put("ph-mnl", "马尼拉");
        UcloudMap.put("rus-mosc", "莫斯科");
        UcloudMap.put("sg", "新加坡");
        UcloudMap.put("th-bkk", "曼谷");
        UcloudMap.put("tw-tp", "台北");
        UcloudMap.put("uae-dubai", "迪拜");
        UcloudMap.put("uk-london", "伦敦");
        UcloudMap.put("us-ca", "洛杉矶");
        UcloudMap.put("us-ws", "华盛顿");
        UcloudMap.put("vn-sng", "胡志明市");
    }

}
