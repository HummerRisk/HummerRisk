package com.hummer.common.core.proxy.openstack;

import com.hummer.common.core.proxy.Request;
import org.apache.commons.collections4.CollectionUtils;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.types.ServiceType;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.identity.v3.Role;
import org.openstack4j.model.identity.v3.Service;
import org.openstack4j.model.identity.v3.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("Duplicates")
public class OpenStackUtils {

    public static OpenStackRequest convert2OpenStackRequest(Request request) {
        OpenStackRequest openstackRequest = new OpenStackRequest();
        openstackRequest.setCredential(request.getCredential());
        if (request.getRegionId() != null && request.getRegionId().trim().length() > 0) {
            openstackRequest.setRegionId(request.getRegionId());
        }
        return openstackRequest;
    }

    public static boolean isAdmin(OSClient osClient) {
        boolean isAdmin = false;
        List<? extends Role> roles = ((OSClient.OSClientV3) osClient).getToken().getRoles();
        for (Role role : roles) {
            if ("admin".equals(role.getName())) {
                isAdmin = true;
            }
        }
        return isAdmin;
    }

    public static boolean isSupport(OSClient osClient, ServiceType serviceType) {
        List<? extends Service> services = ((OSClient.OSClientV3) osClient).getToken().getCatalog();
        for (Service service : services) {
            if (!CollectionUtils.isEmpty(service.getEndpoints())
                    && serviceType.getType().equalsIgnoreCase(service.getType())) {
                return true;
            }
        }
        return false;
    }

    public static String getRegion(OSClient osClient) {
        List<? extends Service> services = ((OSClient.OSClientV3) osClient).getToken().getCatalog();
        for (Service service : services) {
            if (!CollectionUtils.isEmpty(service.getEndpoints())
                    && ServiceType.IMAGE.getType().equalsIgnoreCase(service.getType())) {
                return service.getEndpoints().get(0).getRegion();
            }
        }
        return null;
    }

    public static List<? extends Project> getUserProjects(OSClient.OSClientV3 osClient) {
        User user = osClient.getToken().getUser();
        List<? extends Project> projects = new ArrayList<>();
        try {
            projects = osClient.identity().users().listUserProjects(user.getId());
        } catch (Exception ignored) {
        }

        if (CollectionUtils.isEmpty(projects)) {
            projects = Collections.singletonList(osClient.getToken().getProject());
        }

        return projects;
    }

}
