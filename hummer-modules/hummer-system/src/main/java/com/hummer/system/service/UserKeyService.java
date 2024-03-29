package com.hummer.system.service;

import com.hummer.common.core.constant.ApiKeyConstants;
import com.hummer.common.core.constant.ResourceConstants;
import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.domain.UserKey;
import com.hummer.common.core.domain.UserKeyExample;
import com.hummer.common.core.domain.request.user.UserKeyRequest;
import com.hummer.common.core.exception.HRException;
import com.hummer.common.core.i18n.Translator;
import com.hummer.system.api.model.LoginUser;
import com.hummer.system.mapper.UserKeyMapper;
import com.hummer.system.mapper.ext.ExtUserKeyMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author harris
 */
@Service
public class UserKeyService {

    @Autowired
    private UserKeyMapper userKeyMapper;

    @Autowired
    private ExtUserKeyMapper extUserKeyMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private OperationLogService operationLogService;

    public List<UserKey> getUserKeysInfo(UserKeyRequest request, LoginUser loginUser) {
        String userId = Objects.requireNonNull(loginUser).getUserId();
        if (!StringUtils.equals(userId, "admin")) request.setUserId(userId);
        return extUserKeyMapper.getUserKeysInfo(request);
    }

    public UserKey generateUserKey(String userId, LoginUser loginUser) throws Exception {
        if (userService.getUserDTO(userId) == null) {
            HRException.throwException(Translator.get("user_not_exist") + userId);
        }
        UserKeyExample userKeysExample = new UserKeyExample();
        userKeysExample.createCriteria().andUserIdEqualTo(userId);
        List<UserKey> userKeysList = userKeyMapper.selectByExample(userKeysExample);

        if (!CollectionUtils.isEmpty(userKeysList) && userKeysList.size() >= 5) {
            HRException.throwException(Translator.get("user_apikey_limit"));
        }

        UserKey userKeys = new UserKey();
        userKeys.setId(UUID.randomUUID().toString());
        userKeys.setUserId(userId);
        userKeys.setStatus(ApiKeyConstants.ACTIVE.name());
        userKeys.setAccessKey(RandomStringUtils.randomAlphanumeric(16));
        userKeys.setSecretKey(RandomStringUtils.randomAlphanumeric(16));
        userKeys.setCreateTime(System.currentTimeMillis());
        userKeyMapper.insert(userKeys);
        operationLogService.log(loginUser, userKeys.getAccessKey(), ApiKeyConstants.ACTIVE.name(), ResourceConstants.SystemConstants, ResourceOperation.CREATE, "创建API Keys");
        return userKeyMapper.selectByPrimaryKey(userKeys.getId());
    }

    public void deleteUserKey(String id) {
        userKeyMapper.deleteByPrimaryKey(id);
    }

    public void activeUserKey(String id) {
        UserKey userKeys = new UserKey();
        userKeys.setId(id);
        userKeys.setStatus(ApiKeyConstants.ACTIVE.name());
        userKeyMapper.updateByPrimaryKeySelective(userKeys);
    }

    public void disableUserKey(String id) {
        UserKey userKeys = new UserKey();
        userKeys.setId(id);
        userKeys.setStatus(ApiKeyConstants.DISABLED.name());
        userKeyMapper.updateByPrimaryKeySelective(userKeys);
    }

    public void deleteApiKeys(List<String> ids) throws Exception {
        ids.forEach(id -> {
            try {
                deleteUserKey(id);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public UserKey getUserKey(String accessKey) {
        UserKeyExample userKeyExample = new UserKeyExample();
        userKeyExample.createCriteria().andAccessKeyEqualTo(accessKey).andStatusEqualTo(ApiKeyConstants.ACTIVE.name());
        List<UserKey> userKeysList = userKeyMapper.selectByExample(userKeyExample);
        if (!CollectionUtils.isEmpty(userKeysList)) {
            return userKeysList.get(0);
        }
        return null;
    }
}
