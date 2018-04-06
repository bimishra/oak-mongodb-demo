package org.samples.oak.demo.bean;

import java.util.Date;

import javax.jcr.Node;

public class VersionHistory {
    String fileName;
    String modifiedBy;
    Date modifiedOn;
    String identifier;
    long size;
    boolean current;
    private Node content;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isCurrent() {
        return current;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "VersionHistory [fileName=" + fileName + ", modifiedBy=" + modifiedBy + ", modifiedOn=" + modifiedOn
                + ", identifier=" + identifier + ", size=" + size + ",current=" + current + "]";
    }

}
