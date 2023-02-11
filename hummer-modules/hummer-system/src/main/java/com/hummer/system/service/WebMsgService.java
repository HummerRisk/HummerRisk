package com.hummer.system.service;

import com.hummer.common.core.domain.WebMsg;
import com.hummer.common.core.domain.WebMsgExample;
import com.hummer.common.core.domain.request.webMsg.WebMsgRequest;
import com.hummer.common.core.mapper.WebMsgMapper;
import com.hummer.common.core.mapper.ext.ExtWebMsgMapper;
import com.hummer.common.core.utils.SessionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WebMsgService {

    @Resource
    private WebMsgMapper webMsgMapper;
    @Resource
    private ExtWebMsgMapper extWebMsgMapper;

    public List<WebMsg> query(WebMsg webMsg) {
        String orderClause = " create_time desc";
        WebMsgExample example = new WebMsgExample();
        WebMsgExample.Criteria criteria = example.createCriteria();
        if (webMsg.getStatus() != null) {
            criteria.andStatusEqualTo(webMsg.getStatus());
        }
        example.setOrderByClause(orderClause);
        List<WebMsg> Msgs = webMsgMapper.selectByExample(example);
        return Msgs;
    }

    public List<WebMsg> queryGrid(WebMsgRequest request) {
        List<WebMsg> msgGridDtos = extWebMsgMapper.queryGrid(request);
        return msgGridDtos;
    }

    public Long queryCount() {
        WebMsgExample example = new WebMsgExample();
        WebMsgExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(false);
        return webMsgMapper.countByExample(example);
    }

    public void setReaded(Long msgId) {
        WebMsg msg = new WebMsg();
        msg.setId(msgId);
        msg.setStatus(true);
        msg.setUserId(SessionUtils.getUserId());
        msg.setReadTime(System.currentTimeMillis());
        webMsgMapper.updateByPrimaryKeySelective(msg);
    }

    public void setBatchReaded(List<Long> msgIds) {
        extWebMsgMapper.batchStatus(msgIds, System.currentTimeMillis(), SessionUtils.getUserId());
    }

    public void batchDelete(List<Long> msgIds) {
        extWebMsgMapper.batchDelete(msgIds);
    }

    public void save(WebMsg webMsg) {
        webMsg.setUserId(SessionUtils.getUserId());
        webMsgMapper.insertSelective(webMsg);
    }

}
