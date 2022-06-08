package io.hummerrisk.commons.utils;

import com.huaweicloud.sdk.iam.v3.IamClient;
import com.huaweicloud.sdk.iam.v3.model.KeystoneListRegionsRequest;
import com.huaweicloud.sdk.iam.v3.model.KeystoneListRegionsResponse;
import com.huaweicloud.sdk.iam.v3.model.Region;
import com.huaweicloud.sdk.iam.v3.model.RegionLocales;

import java.util.List;

/**
 * @author harris
 */
public class RegionUtil {
    public static List<Region> allRegions(IamClient iamClient ){
        KeystoneListRegionsResponse regionsResponse = iamClient.keystoneListRegions(new KeystoneListRegionsRequest());
        return regionsResponse.getRegions();
    }

    public static String getZHCNName(List<RegionLocales> local){
        for(RegionLocales l:local){
            String name = l.getZhCn();
            if(name!=null) return name;
        }
        return null;
    }

}
