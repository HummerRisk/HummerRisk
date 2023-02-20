package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.request.k8s.rbac.Links;
import com.hummer.common.mapper.domain.request.k8s.rbac.Nodes;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtCloudNativeSourceRbacMapper {

    List<Nodes> nodes(@Param("k8sId") String k8sId);

    List<Links> links(@Param("k8sId") String k8sId);

}
