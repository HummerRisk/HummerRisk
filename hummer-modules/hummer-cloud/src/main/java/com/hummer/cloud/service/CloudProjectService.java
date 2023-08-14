package com.hummer.cloud.service;

import com.hummer.cloud.mapper.*;
import com.hummer.cloud.mapper.ext.ExtCloudProjectMapper;
import com.hummer.common.core.constant.ResourceOperation;
import com.hummer.common.core.constant.ResourceTypeConstants;
import com.hummer.common.core.domain.*;
import com.hummer.common.core.domain.request.project.CloudGroupRequest;
import com.hummer.common.core.dto.CloudGroupDTO;
import com.hummer.common.core.dto.CloudProcessDTO;
import com.hummer.common.core.dto.CloudProjectDTO;
import com.hummer.system.api.IOperationLogService;
import com.hummer.system.api.model.LoginUser;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author harris
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class CloudProjectService {

    @Autowired
    private ExtCloudProjectMapper extCloudProjectMapper;
    @Autowired
    private CloudProjectMapper cloudProjectMapper;
    @Autowired
    private CloudProjectLogMapper cloudProjectLogMapper;
    @Autowired
    private CloudGroupMapper cloudGroupMapper;
    @Autowired
    private CloudGroupLogMapper cloudGroupLogMapper;
    @Autowired
    private CloudProcessMapper cloudProcessMapper;
    @Autowired
    private CloudProcessLogMapper cloudProcessLogMapper;
    @Autowired
    private CloudTaskMapper cloudTaskMapper;
    @Autowired
    private CloudTaskItemMapper cloudTaskItemMapper;
    @DubboReference
    private IOperationLogService operationLogService;

    public List<CloudProjectDTO> getCloudProjectDTOs(CloudGroupRequest cloudGroupRequest) {
        return extCloudProjectMapper.getCloudProjectDTOs(cloudGroupRequest);
    }

    public CloudProjectDTO projectById(String projectId) {
        CloudGroupRequest cloudProject = new CloudGroupRequest();
        cloudProject.setId(projectId);
        List<CloudProjectDTO> list = extCloudProjectMapper.getCloudProjectDTOs(cloudProject);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new CloudProjectDTO();
        }
    }

    public void deletes(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteProject(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void deleteProject(String projectId, LoginUser loginUser) {

        CloudProject cloudProject = cloudProjectMapper.selectByPrimaryKey(projectId);
        cloudProjectMapper.deleteByPrimaryKey(projectId);

        CloudProjectLogExample cloudProjectLogExample = new CloudProjectLogExample();
        cloudProjectLogExample.createCriteria().andProjectIdEqualTo(projectId);
        cloudProjectLogMapper.deleteByExample(cloudProjectLogExample);

        CloudGroupExample cloudGroupExample = new CloudGroupExample();
        cloudGroupExample.createCriteria().andProjectIdEqualTo(projectId);
        cloudGroupMapper.deleteByExample(cloudGroupExample);

        CloudGroupLogExample cloudGroupLogExample = new CloudGroupLogExample();
        cloudGroupLogExample.createCriteria().andProjectIdEqualTo(projectId);
        cloudGroupLogMapper.deleteByExample(cloudGroupLogExample);

        CloudProcessExample cloudProcessExample = new CloudProcessExample();
        cloudProcessExample.createCriteria().andProjectIdEqualTo(projectId);
        cloudProcessMapper.deleteByExample(cloudProcessExample);

        CloudProcessLogExample cloudProcessLogExample = new CloudProcessLogExample();
        cloudProcessLogExample.createCriteria().andProjectIdEqualTo(projectId);
        cloudProcessLogMapper.deleteByExample(cloudProcessLogExample);

        operationLogService.log(loginUser, projectId, cloudProject.getAccountName(), ResourceTypeConstants.CLOUD_PROJECT.name(), ResourceOperation.DELETE, "i18n_delete_cloud_project");
    }

    public List<CloudGroupDTO> getCloudGroupDTOs(CloudGroupRequest cloudGroup) {
        return extCloudProjectMapper.getCloudGroupDTOs(cloudGroup);
    }

    public CloudGroupDTO groupById(String groupId) {
        CloudGroupRequest cloudGroup = new CloudGroupRequest();
        cloudGroup.setId(groupId);
        List<CloudGroupDTO> list = extCloudProjectMapper.getCloudGroupDTOs(cloudGroup);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new CloudGroupDTO();
        }
    }

    public void deleteGroups(List<String> ids, LoginUser loginUser) throws Exception {
        ids.forEach(id -> {
            try {
                deleteGroup(id, loginUser);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public void deleteGroup(String groupId, LoginUser loginUser) {

        CloudGroup cloudGroup = cloudGroupMapper.selectByPrimaryKey(groupId);
        cloudGroupMapper.deleteByPrimaryKey(groupId);

        CloudGroupLogExample cloudGroupLogExample = new CloudGroupLogExample();
        cloudGroupLogExample.createCriteria().andGroupIdEqualTo(groupId);
        cloudGroupLogMapper.deleteByExample(cloudGroupLogExample);

        operationLogService.log(loginUser, groupId, cloudGroup.getAccountName(), ResourceTypeConstants.CLOUD_GROUP.name(), ResourceOperation.DELETE, "i18n_delete_cloud_project");
    }

    public List<CloudProcessDTO> getCloudProcessDTOs(CloudProcess cloudProcess) {
        return extCloudProjectMapper.getCloudProcessDTOs(cloudProcess);
    }

    public CloudProcessDTO processById(String processId) {
        CloudProcess cloudProcess = new CloudProcess();
        cloudProcess.setId(processId);
        List<CloudProcessDTO> list = extCloudProjectMapper.getCloudProcessDTOs(cloudProcess);
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return new CloudProcessDTO();
        }
    }

}
