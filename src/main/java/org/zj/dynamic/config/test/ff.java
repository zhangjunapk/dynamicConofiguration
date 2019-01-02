package org.zj.dynamic.config.test;

/**
 * @BelongsProject: dynamicConfiguration
 * @BelongsPackage: org.zj.dynamic.config.test
 * @Author: ZhangJun
 * @CreateTime: 2019/1/3
 * @Description: ${Description}
 */
public class ff {
    public static void main(String[] args) {
        new Thread(() -> {
            for(;;){

                System.out.println(YunxinConfig.uploadFileUrl);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).run();

    }
}
