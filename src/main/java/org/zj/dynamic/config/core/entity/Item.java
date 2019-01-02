package org.zj.dynamic.config.core.entity;

import java.util.Date;

/**
 * @BelongsProject: dynamicConfiguration
 * @BelongsPackage: org.zj.dynamic.config.core.entity
 * @Author: ZhangJun
 * @CreateTime: 2019/1/2
 * @Description: ${Description}
 */
public class Item {
    private String key;
    private String value;
    private String nameSpace;
    private String note;
    private Date updateTime;

    public Item() {
    }

    public Item(String key, String value, String nameSpace, String note, Date updateTime) {
        this.key = key;
        this.value = value;
        this.nameSpace = nameSpace;
        this.note = note;
        this.updateTime = updateTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
