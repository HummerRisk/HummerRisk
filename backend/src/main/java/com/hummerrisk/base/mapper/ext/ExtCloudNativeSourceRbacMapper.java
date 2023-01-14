package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.k8s.rbac.Links;
import com.hummerrisk.controller.request.k8s.rbac.Nodes;

import java.util.List;

public interface ExtCloudNativeSourceRbacMapper {

    List<Nodes> nodes();

    List<Links> links();

}
