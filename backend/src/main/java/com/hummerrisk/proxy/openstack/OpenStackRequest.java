package com.hummerrisk.proxy.openstack;

import com.google.gson.Gson;
import com.hummerrisk.commons.exception.PluginException;
import com.hummerrisk.proxy.Request;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpRequestInterceptor;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.connectors.httpclient.HttpClientFactory;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.identity.v3.User;
import org.openstack4j.model.image.Image;
import org.openstack4j.openstack.OSFactory;

import java.util.List;

public class OpenStackRequest extends Request {

    private OpenStackCredential OpenStackCredential;

    public OpenStackCredential getOpenStackCredential() {
        if (OpenStackCredential == null) {
            OpenStackCredential = new Gson().fromJson(getCredential(), OpenStackCredential.class);
        }
        return OpenStackCredential;
    }

    public OSClientV3 getOpenStackClient() {
        return getOpenStackClient(getOpenStackCredential().getProjectId());
    }

    public OSClientV3 getOpenStackClient(String projectId) {
        HttpClientFactory.registerInterceptor((httpClient, requestConfig, httpConfig) -> {
            httpClient.addInterceptorFirst((HttpRequestInterceptor) (httpRequest, httpContext) -> httpRequest.removeHeaders("Accept"));
        });
        OpenStackCredential OpenStackCredential = getOpenStackCredential();
        OSClientV3 osClient = OSFactory.builderV3().endpoint(OpenStackCredential.getEndpoint())
                .credentials(OpenStackCredential.getUserId(), OpenStackCredential.getPassword())
                .scopeToProject(Identifier.byId(projectId), Identifier.byId(OpenStackCredential.getDomainId()))
                .withConfig(Config.newConfig().withSSLVerificationDisabled())
                .authenticate();
        osClient.useRegion(getRegionId());
        return osClient;
    }

    public OSClientV3 searchOpenStackClientByInstanceId(String instanceId) throws PluginException {
        OSClientV3 osClient = getOpenStackClient();
        //admin用户不需要指定到project的token，直接返回
        if (OpenStackUtils.isAdmin(osClient)) {
            return osClient;
        }
        User user = osClient.getToken().getUser();
        List<? extends Project> projects = OpenStackUtils.getUserProjects(osClient);

        if (!CollectionUtils.isEmpty(projects)) {
            for (Project project : projects) {
                osClient = getOpenStackClient(project.getId());
                if (osClient.compute().servers().get(instanceId) != null) {
                    return osClient;
                }
            }
        }
        throw new PluginException(String.format("Failed to get token of project where the virtual machine: %s is located by user %s," +
                " It may be that the virtual machine has been deleted or the user does not have permission to the project where the virtual machine is located.", instanceId, user.getName()));
    }

    public OSClientV3 searchOpenStackClientByVolumeId(String volumeId) throws PluginException {
        OSClientV3 osClient = getOpenStackClient();
        //admin用户不需要指定到project的token，直接返回
        if (OpenStackUtils.isAdmin(osClient)) {
            return osClient;
        }
        User user = osClient.getToken().getUser();
        List<? extends Project> projects = OpenStackUtils.getUserProjects(osClient);

        if (!CollectionUtils.isEmpty(projects)) {
            for (Project project : projects) {
                osClient = getOpenStackClient(project.getId());
                if (osClient.blockStorage().volumes().get(volumeId) != null) {
                    return osClient;
                }
            }
        }
        throw new PluginException(String.format("Failed to get token of project where the volume: %s is located by user %s," +
                " It may be that the volume has been deleted or the user does not have permission to the project where the volume is located.", volumeId, user.getName()));
    }

    public OSClientV3 searchOpenStackClientBySnapshotId(String snapshotId) throws PluginException {
        OSClientV3 osClient = getOpenStackClient();

        User user = osClient.getToken().getUser();
        List<? extends Project> projects = OpenStackUtils.getUserProjects(osClient);

        if(!CollectionUtils.isEmpty(projects)) {
            for(Project project : projects) {
                osClient = getOpenStackClient(project.getId());
                List<? extends Image> images = osClient.images().list();
                for (Image image : images) {
                    if(snapshotId.equals(image.getId())){
                        return osClient;
                    }
                }
            }
        }
        throw new PluginException("User ["+user.getName()+"] cannot obtain the Token of the Project where the current image ["+snapshotId+"] is located. " +
                "It may be that the image has been deleted or the user does not have permission to the project where the image is located.");
    }
}
