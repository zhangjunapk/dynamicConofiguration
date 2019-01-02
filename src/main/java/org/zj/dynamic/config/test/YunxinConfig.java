package org.zj.dynamic.config.test;

import org.zj.dynamic.config.core.annotation.NameSpace;
import org.zj.dynamic.config.core.annotation.Value;

/**
 * @BelongsProject: dynamicConfiguration
 * @BelongsPackage: org.zj.dynamic.config.test
 * @Author: ZhangJun
 * @CreateTime: 2018/12/30
 * @Description: ${Description}
 */
@NameSpace("yunxin_config")
public class YunxinConfig {
    @Value(key = "base_url",init = "http://www.baidu.com")
    public static String baseUrl;

    @Value(key = "uploadfile_url",init = "http://www.upload.com")
    public static String uploadFileUrl;

}
