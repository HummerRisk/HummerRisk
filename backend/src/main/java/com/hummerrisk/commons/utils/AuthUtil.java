package com.hummerrisk.commons.utils;

import com.huaweicloud.sdk.iam.v3.IamClient;
import com.huaweicloud.sdk.iam.v3.model.ShowCredential;
import com.huaweicloud.sdk.iam.v3.model.ShowPermanentAccessKeyRequest;
import com.hummerrisk.commons.exception.HRException;

/**
 * @author harris
 */
public class AuthUtil {

    public static ShowCredential validate(IamClient client ,String ak) throws HRException {
        ShowPermanentAccessKeyRequest request = new ShowPermanentAccessKeyRequest();
        request.withAccessKey(ak);
        ShowCredential result = client.showPermanentAccessKey(request).getCredential();
        return result;
    }


}
