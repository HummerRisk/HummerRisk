package com.hummerrisk.commons.utils;

import com.huaweicloud.sdk.iam.v3.IamClient;
import com.huaweicloud.sdk.iam.v3.model.KeystoneListProjectsForUserRequest;
import com.huaweicloud.sdk.iam.v3.model.KeystoneShowProjectRequest;
import com.huaweicloud.sdk.iam.v3.model.KeystoneShowProjectResponse;
import com.huaweicloud.sdk.iam.v3.model.ProjectResult;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author harris
 */
public class ProjectUtil {

    private static final Set<String> noPermissionRegions = new HashSet(){{add("MOS");add("cn-north-1");}};

    public static List<ProjectResult> listProjects(IamClient iamClient, String userId){
        KeystoneListProjectsForUserRequest request = new KeystoneListProjectsForUserRequest().withUserId(userId);
        List<ProjectResult> projects = iamClient.keystoneListProjectsForUser(request).getProjects();
        return projects.stream().filter(ProjectUtil::projectAccess).collect(Collectors.toList());
    }

    private static Boolean projectAccess(ProjectResult projectResult){
        return !noPermissionRegions.contains(projectResult.getName());
    }

    public static ProjectResult project(IamClient iamClient, String projectId){
        KeystoneShowProjectResponse response = iamClient.keystoneShowProject(new KeystoneShowProjectRequest().withProjectId(projectId));
        ProjectResult project = response.getProject();
        return project;
    }

    public static List<ProjectResult> filterWithRequest(List<ProjectResult> projects, IamRequest request){
        if (StringUtils.isNotEmpty(request.getRegionId())){
            String regionId = request.getRegionId();
            projects = projects.stream().filter(project -> StringUtils.equals(project.getName(), regionId)).collect(Collectors.toList());
        }
        if (StringUtils.isNotEmpty(request.getHuaweiCloudCredential().getProjectId())){
            String projectId = request.getHuaweiCloudCredential().getProjectId();
            projects = projects.stream().filter(project -> StringUtils.equals(project.getId(), projectId)).collect(Collectors.toList());
        }
        return projects;
    }
}
