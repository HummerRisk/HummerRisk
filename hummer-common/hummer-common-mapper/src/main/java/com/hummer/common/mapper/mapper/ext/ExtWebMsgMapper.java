package com.hummer.common.mapper.mapper.ext;

import com.hummer.common.mapper.domain.WebMsg;
import com.hummer.common.mapper.domain.request.webMsg.WebMsgRequest;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ExtWebMsgMapper {

    @Update({
        "<script>",
            "update web_msg set status = 1, read_time = #{time}, user_id = #{userId} where id in ",
            "<foreach collection='msgIds' item='msgId' open='(' separator=',' close=')' >",
                " #{msgId}",
            "</foreach>",
        "</script>"
    })
    int batchStatus(@Param("msgIds") List<Long> msgIds, @Param("time") Long time, @Param("userId") String userId);


    @Delete({
            "<script>",
                "delete from web_msg where id in ",
                "<foreach collection='msgIds' item='msgId' open='(' separator=',' close=')' >",
                    " #{msgId}",
                "</foreach>",
            "</script>"
    })
    int batchDelete(@Param("msgIds") List<Long> msgIds);

    List<WebMsg> queryGrid(@Param("request") WebMsgRequest request);

}
