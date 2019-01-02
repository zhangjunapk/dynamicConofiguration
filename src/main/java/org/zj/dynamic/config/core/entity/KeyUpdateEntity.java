package org.zj.dynamic.config.core.entity;

import java.util.Date;
import java.util.Objects;

/**
 * @BelongsProject: dynamicConfiguration
 * @BelongsPackage: org.zj.dynamic.config.core.entity
 * @Author: ZhangJun
 * @CreateTime: 2019/1/2
 * @Description: 存放指定key的上次更新时间
 */
public class KeyUpdateEntity {
    private String key;
    private Date updateTime;

    public KeyUpdateEntity() {
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public KeyUpdateEntity(String key, Date updateTime) {
        this.key = key;
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyUpdateEntity that = (KeyUpdateEntity) o;
        return key.equals(that.key) &&
                updateTime.equals(that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, updateTime);
    }
}
