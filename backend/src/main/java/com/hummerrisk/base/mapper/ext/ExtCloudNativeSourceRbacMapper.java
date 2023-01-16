package com.hummerrisk.base.mapper.ext;

import com.hummerrisk.controller.request.k8s.rbac.Links;
import com.hummerrisk.controller.request.k8s.rbac.Nodes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudNativeSourceRbacMapper {

    List<Nodes> nodes(@Param("k8sId") String k8sId);

    List<Links> links(@Param("k8sId") String k8sId);

}
