package org.loktevik.Lab3;

public class PostfixEntry {
    private EEntryType type;
    private int index;
    private ECmd cmd;
    private String value;
    private int cmdPtr;
    private int curValue;

    public EEntryType getType() {
        return type;
    }

    public void setType(EEntryType type) {
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ECmd getCmd() {
        return cmd;
    }

    public void setCmd(ECmd cmd) {
        this.cmd = cmd;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCmdPtr() {
        return cmdPtr;
    }

    public void setCmdPtr(int cmdPtr) {
        this.cmdPtr = cmdPtr;
    }

    public int getCurValue() {
        return curValue;
    }

    public void setCurValue(int curValue) {
        this.curValue = curValue;
    }

    @Override
    public String toString() {
        if (cmd != null)
            return cmd.toString();
        else if (value != null)
            return value;
        else return String.valueOf(cmdPtr);
    }
}
