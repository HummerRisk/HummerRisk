package com.hummerrisk.commons.constants;

public class CloudAccountConstants {
    public CloudAccountConstants() {
    }

    public static enum SyncStatus {
        pending,
        success,
        error,
        sync;

        private SyncStatus() {
        }
    }

    public static enum Status {
        VALID,
        INVALID,
        DELETED;

        private Status() {
        }
    }

    public static enum ProxyType {
        Http,
        Https;

        private ProxyType() {
        }
    }
}
