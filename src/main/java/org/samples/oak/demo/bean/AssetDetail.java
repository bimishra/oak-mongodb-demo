package org.samples.oak.demo.bean;

public class AssetDetail {

    private String assetName;
    private double size;
    private String createdBy;
    private String createdOn;
    private String assetType;
    private String assetId;

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    @Override
    public String toString() {
        return "AssetDetail [assetName=" + assetName + ", size=" + size + ", createdBy=" + createdBy + ", createdOn="
                + createdOn + ", assetType=" + assetType + ", assetId=" + assetId + "]";
    }

}
