package com.hummer.system.service;

import com.hummer.common.core.domain.WebMsg;
import com.hummer.common.core.domain.WebMsgExample;
import com.hummer.common.core.domain.request.webMsg.WebMsgRequest;
import com.hummer.common.security.service.TokenService;
import com.hummer.system.mapper.WebMsgMapper;
import com.hummer.system.mapper.ext.ExtWebMsgMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WebMsgService {

    @Resource
    private WebMsgMapper webMsgMapper;
    @Resource
    private ExtWebMsgMapper extWebMsgMapper;
    @Resource
    private TokenService tokenService;

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
        msg.setUserId(tokenService.getLoginUser().getUserId());
        msg.setReadTime(System.currentTimeMillis());
        webMsgMapper.updateByPrimaryKeySelective(msg);
    }

    public void setBatchReaded(List<Long> msgIds) {
        extWebMsgMapper.batchStatus(msgIds, System.currentTimeMillis(), tokenService.getLoginUser().getUserId());
    }

    public void batchDelete(List<Long> msgIds) {
        extWebMsgMapper.batchDelete(msgIds);
    }

    public void save(WebMsg webMsg) {
        webMsg.setUserId(tokenService.getLoginUser().getUserId());
        webMsgMapper.insertSelective(webMsg);
    }

}
