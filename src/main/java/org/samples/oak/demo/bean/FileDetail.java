package org.samples.oak.demo.bean;

import java.io.InputStream;

public class FileDetail {

    private String fileName;
    private long size;
    private String contentType;
    private InputStream fileData;
    private String createdBy;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public InputStream getFileData() {
        return fileData;
    }

    public void setFileData(InputStream fileData) {
        this.fileData = fileData;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "FileDetail [fileName=" + fileName + ", size=" + size + ", contentType=" + contentType + ", createdBy="
                + createdBy + "]";
    }

}
