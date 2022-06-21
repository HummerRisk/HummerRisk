package com.hummerrisk.service;

import com.hummerrisk.base.domain.MailAttachmentInfo;
import com.hummerrisk.base.domain.SystemParameter;
import com.hummerrisk.base.mapper.SystemParameterMapper;
import com.hummerrisk.commons.constants.ParamConstants;
import com.hummerrisk.commons.utils.EncryptUtils;
import com.hummerrisk.commons.utils.LogUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
public class MailService {

    @Resource
    private SystemParameterMapper systemParameterMapper;
    @Resource
    private SystemParameterService systemParameterService;

    public JavaMailSenderImpl getMailSender() {
        Properties props = new Properties();
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        List<SystemParameter> paramList = systemParameterService.getParamList(ParamConstants.Classify.MAIL.getValue());
        javaMailSender.setDefaultEncoding("UTF-8");
        javaMailSender.setProtocol("smtp");
        props.put("mail.smtp.auth", "true");

        for (SystemParameter p : paramList) {
            switch (p.getParamKey()) {
                case "smtp.host":
                    javaMailSender.setHost(p.getParamValue());
                    break;
                case "smtp.port":
                    javaMailSender.setPort(Integer.parseInt(p.getParamValue()));
                    break;
                case "smtp.account":
                    javaMailSender.setUsername(p.getParamValue());
                    break;
                case "smtp.password":
                    javaMailSender.setPassword(EncryptUtils.aesDecrypt(p.getParamValue()).toString());
                    break;
                case "smtp.ssl":
                    if (BooleanUtils.toBoolean(p.getParamValue())) {
                        javaMailSender.setProtocol("smtps");
                        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    }
                    break;
                case "smtp.tls":
                    String result = BooleanUtils.toString(BooleanUtils.toBoolean(p.getParamValue()), "true", "false");
                    props.put("mail.smtp.starttls.enable", result);
                    props.put("mail.smtp.starttls.required", result);
                    break;
             /*   case "smtp.anon":
                    boolean isAnon = BooleanUtils.toBoolean(p.getParamValue());
                    if (isAnon) {
                        props.put("mail.smtp.auth", "false");
                        javaMailSender.setUsername(null);
                        javaMailSender.setPassword(null);
                    }
                    break;*/
                default:
                    break;
            }
        }

        props.put("mail.smtp.timeout", "30000");
        props.put("mail.smtp.connectiontimeout", "5000");
        javaMailSender.setJavaMailProperties(props);
        return javaMailSender;
    }

    @Async
    public void sendSimpleEmail(String subject, String content, String... to) {
        JavaMailSenderImpl javaMailSender = getMailSender();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setFrom(Objects.requireNonNull(javaMailSender.getUsername()));
        simpleMailMessage.setSentDate(new Date());
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);
        javaMailSender.send(simpleMailMessage);
    }

    @Async
    public void sendHtmlEmail(String subject, String content, String... to) {
        JavaMailSenderImpl javaMailSender = getMailSender();
        MimeMessage message = javaMailSender.createMimeMessage();//创建一个MINE消息
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(getNotNullValue(ParamConstants.MAIL.ACCOUNT.getKey()));
            helper.setTo(to);
            helper.setSentDate(new Date());
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            LogUtil.error(e.getMessage());
        }
    }

    //发送邮件带附件
    public void sendAttachmentEmail(String subject, String content, MailAttachmentInfo mailAttachmentInfo, String... to) {
        List<MailAttachmentInfo> mailAttachmentInfoList = new ArrayList<>();
        mailAttachmentInfoList.add(mailAttachmentInfo);
        sendAttachmentEmail(subject, content, mailAttachmentInfoList, to);
    }

    //发送邮件带附件
    public void sendAttachmentEmail(String subject, String content, List<MailAttachmentInfo> mailAttachmentInfoList, String... to) {
        JavaMailSenderImpl javaMailSender = getMailSender();
        MimeMessage message = javaMailSender.createMimeMessage();//创建一个MINE消息
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(getNotNullValue(ParamConstants.MAIL.ACCOUNT.getKey()));
            helper.setTo(to);
            helper.setSentDate(new Date());
            helper.setSubject(subject);
            helper.setText(content, true);
            if (CollectionUtils.isNotEmpty(mailAttachmentInfoList)) {
                for (MailAttachmentInfo mailAttachmentInfo : mailAttachmentInfoList) {
                    helper.addAttachment(mailAttachmentInfo.getAttachmentName(), mailAttachmentInfo.getInputStreamSource());
                }
            }
            javaMailSender.send(message);
        } catch (MessagingException e) {
            LogUtil.error(e.getMessage());
        }
    }

    private String getValue(String key) {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(key);
        if (systemParameter != null) {
            return systemParameter.getParamValue();
        }
        return null;
    }

    private String getNotNullValue(String key) {
        SystemParameter systemParameter = systemParameterMapper.selectByPrimaryKey(key);
        if (systemParameter == null) {
            throw new NullPointerException(key + " not set");
        }
        String value = systemParameter.getParamValue();
        if (StringUtils.isBlank(value)) {
            throw new NullPointerException(key + " not set");
        }
        return value;
    }
}
