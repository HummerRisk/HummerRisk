package com.hummer.system.mapper.ext;

import com.hummer.common.core.domain.Plugin;
import com.hummer.common.core.domain.request.plugin.PluginRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExtPluginMapper {

    List<Plugin> getPluginList(@Param("request") PluginRequest request);

}
