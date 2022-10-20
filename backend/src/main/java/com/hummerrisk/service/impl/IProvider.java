package com.hummerrisk.service.impl;

public interface IProvider {

    String getName();

    String execute(Object ...obj) throws Exception;

    String dockerLogin(Object obj) throws Exception;

}
