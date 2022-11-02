package com.hummerrisk.service.impl;

import com.hummerrisk.dto.ResultDTO;

public interface IProvider {

    String getName();

    ResultDTO execute(Object ...obj) throws Exception;

    String dockerLogin(Object obj) throws Exception;

}
