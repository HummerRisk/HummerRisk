package com.hummerrisk.commons.exception;

/**
 * 文件信息异常类
 *
 */
public class FileException extends HRException
{
    private static final long serialVersionUID = 1L;

    public FileException(String code, Object[] args)
    {
        super("file");
    }

}
