package com.lin;

import com.lin.enums.BGMOperatorTypeEnum;

import com.lin.service.BgmService;
import com.lin.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Component
public class ZKCuratorClient {

//    @Autowired
//    private BgmService bgmService;

    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);

//    public static final String ZOOKEEPER_SERVER = "192.168.2.108:2181";

    @Autowired
    private ResourceConfig resourceConfig;

    public void init() {
        if (client != null) {
            return;
        }
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
        //创建zk客户端
        client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin").build();
        //启动客户端
        client.start();

        try {
            addChildWatch("/bgm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addChildWatch(String nodePath) throws Exception {
        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event)
                    throws Exception {
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) {
                    log.info("监听到事件CHILD_ADDED");

                    //1.从数据库查询bgm对象，获取路径Path
                    String path = event.getData().getPath();
                    String operatorObj = new String(event.getData().getData(),"UTF-8");

                    Map<String, String> map = JsonUtils.jsonToPojo(operatorObj, Map.class);
                    String operatorType = map.get("operType");
                    String songPath = map.get("path");

//                    String arr[]=path.split("/");
//                    String bgmId=arr[arr.length-1];
//                    Bgm bgm=bgmService.queryBgmById(bgmId);
//                    if (bgm==null){
//                        return;
//                    }

                    //1.1 bgm所在的相对路径
//                    String songPath=bgm.getPath();

                    //2.定义保存到本地的bgm路径
//                    String filePath = "D:\\IDEA\\videos-dev-resource" + songPath;
                    String filePath=resourceConfig.getFileSpace()+songPath;
                    //3.定义下载的路径(播放url)
                    String arrPath[] = songPath.split("\\\\");
                    String finalPath = "";
                    //3.1处理url的斜杠以及编码
                    for (int i = 0; i < arrPath.length; i++) {
                        if (StringUtils.isNotBlank(arrPath[i])) {
                            finalPath += "/";
                            finalPath += URLEncoder.encode(arrPath[i], "UTF-8");
                        }
                    }
//                    String bgmUrl = "http://192.168.2.106:9091/mvc" + finalPath;
                    String bgmUrl=resourceConfig.getBgmServer()+finalPath;
                    if (operatorType.equals(BGMOperatorTypeEnum.ADD.type)) {
                        //下载bgm到springboot服务器
                        URL url = new URL(bgmUrl);
                        File file = new File(filePath);
                        FileUtils.copyURLToFile(url, file);
                        client.delete().forPath(path);
                    } else if (operatorType.equals(BGMOperatorTypeEnum.DELETE.type)) {
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        client.delete().forPath(path);
                    }
                }

            }
        });

    }
}
