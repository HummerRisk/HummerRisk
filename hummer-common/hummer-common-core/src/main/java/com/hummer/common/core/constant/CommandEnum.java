package com.hummer.common.core.constant;

/**
 * @author harris
 */
public enum CommandEnum {
    custodian("custodian"), run("run"), dryrun("--dryrun"), schema("schema"),
    report("report"), version("version"), validate("validate"), prowler("prowler");

    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    CommandEnum(String command) {
        this.command = command;
    }
}
