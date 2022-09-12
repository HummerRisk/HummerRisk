package com.hummerrisk.service.impl;

public interface IProvider {

    String getName();

    String execute(Object ...obj);

    String dockerLogin(Object obj);

}
