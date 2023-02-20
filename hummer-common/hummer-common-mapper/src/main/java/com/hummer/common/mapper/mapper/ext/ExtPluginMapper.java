package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.Plugin;
import com.hummer.common.mapper.domain.request.plugin.PluginRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExtPluginMapper {

    List<Plugin> getPluginList(@Param("request") PluginRequest request);

}
