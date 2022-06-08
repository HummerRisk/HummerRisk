package io.hummerrisk.commons.constants;

/**
 * @author harris
 */
public enum CommandEnum {
    custodian("custodian"), run("run"), dryrun("--dryrun"), schema("schema"),
    report("report"), version("version"), validate("validate"), nuclei("nuclei"),
    prowler("prowler"), xray("xray");

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
