package com.hummer.common.core.constant;

import java.util.Arrays;
import java.util.List;

public interface ParamConstants {

    String getValue();

    enum Type implements ParamConstants {

        PASSWORD("password"),
        TEXT("text"),
        BOOLEAN("boolean"),
        JSON("json");

        private String value;

        Type(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    enum Classify implements ParamConstants {
        MAIL("smtp"),
        WECHAT("wechat"),
        DINGDING("dingding"),
        MESSAGE("message"),
        REGISTRY("registry"),
        SYSTEM("system"),
        SCAN("scansetting"),
        ANALYSIS("analysis");

        private String value;

        Classify(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


    enum Registry implements ParamConstants {
        URL("registry.url"),
        REPO("registry.repo"),
        USERNAME("registry.username"),
        PASSWORD("registry.password");

        private String value;

        Registry(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }

    enum I18n implements ParamConstants {

        LANGUAGE("i18n.language");

        private String value;

        I18n(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    enum MAIL {
        SERVER("smtp.server", 1),
        PORT("smtp.port", 2),
        ACCOUNT("smtp.account", 3),
        PASSWORD("smtp.password", 4),
        SSL("smtp.ssl", 5),
        TLS("smtp.tls", 6),
        SMTP("smtp.smtp", 7);
//        ANON("smtp.anon", 8);

        private String key;
        private Integer value;

        private MAIL(String key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public Integer getValue() {
            return this.value;
        }
    }

    enum WECHAT {
        CROPID("wechat.cropId", 1),
        AGENTID("wechat.agentId", 2),
        SECRET("wechat.secret", 3),
        TESTUSER("wechat.testUser", 4);

        private String key;
        private Integer value;

        private WECHAT(String key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public Integer getValue() {
            return this.value;
        }
    }

    enum DINGDING {
        APPKEY("dingding.appKey", 1),
        AGENTID("dingding.agentId", 2),
        APPSECRET("dingding.appSecret", 3),
        TESTUSER("dingding.testUser", 4);

        private String key;
        private Integer value;

        private DINGDING(String key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public Integer getValue() {
            return this.value;
        }
    }

    enum MEAASGE {
        SERVER("meaasge.title", 1),
        PORT("smtp.recipient", 2),
        ACCOUNT("smtp.mails", 3),
        PASSWORD("smtp.mailContent", 4),
        SSL("smtp.textContent", 5),
        TLS("smtp.messageType", 6),
        SMTP("smtp.triggerAction", 7);

        private String key;
        private Integer value;

        private MEAASGE(String key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public Integer getValue() {
            return this.value;
        }
    }

    enum ANALYSIS {
        COLOR("analysis.color", 1),
        CYCLE("analysis.cycle", 2),
        IDS("analysis.ids", 3),
        TYPES("analysis.types", 4),
        USERS("analysis.users", 5);

        private String key;
        private Integer value;

        private ANALYSIS(String key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public Integer getValue() {
            return this.value;
        }

        public final static String color = "#409EFF";
        public final static Integer cycle = 30;
        public final static List<Boolean> ids = Arrays.asList(true, false, false, false, false, false, false, false);
        public final static List<String> types = Arrays.asList("cloudAccount", "serverAccount", "k8sAccount", "imageAccount", "configAccount", "fsAccount");
        public final static List<String> users = Arrays.asList("admin");

    }

    enum SCAN {
        SkipDbUpdate("scansetting.skipDbUpdate", 1),
        SecurityChecks("scansetting.securityChecks", 2),
        IgnoreUnfixed("scansetting.ignoreUnfixed", 3),
        Severity("scansetting.severity", 4),
        OfflineScan("scansetting.offlineScan", 5);

        private String key;
        private Integer value;

        private SCAN(String key, Integer value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public Integer getValue() {
            return this.value;
        }
    }
}
