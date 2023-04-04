package com.hummer.system.service;

import com.hummer.common.core.domain.WebMsg;
import com.hummer.common.core.domain.WebMsgExample;
import com.hummer.common.core.domain.request.webMsg.WebMsgRequest;
import com.hummer.system.api.model.LoginUser;
import com.hummer.system.mapper.WebMsgMapper;
import com.hummer.system.mapper.ext.ExtWebMsgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebMsgService {

    @Autowired
    private WebMsgMapper webMsgMapper;
    @Autowired
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

    public void setReaded(Long msgId, LoginUser loginUser) {
        WebMsg msg = new WebMsg();
        msg.setId(msgId);
        msg.setStatus(true);
        msg.setUserId(loginUser.getUserId());
        msg.setReadTime(System.currentTimeMillis());
        webMsgMapper.updateByPrimaryKeySelective(msg);
    }

    public void setBatchReaded(List<Long> msgIds, LoginUser loginUser) {
        extWebMsgMapper.batchStatus(msgIds, System.currentTimeMillis(), loginUser.getUserId());
    }

    public void batchDelete(List<Long> msgIds) {
        extWebMsgMapper.batchDelete(msgIds);
    }

    public void save(WebMsg webMsg, LoginUser loginUser) {
        webMsg.setUserId(loginUser.getUserId());
        webMsgMapper.insertSelective(webMsg);
    }

}
