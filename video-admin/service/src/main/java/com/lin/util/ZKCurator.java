package com.lin.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ZKCurator{

    @Autowired
    private CuratorFramework client;

    final static Logger log= LoggerFactory.getLogger(ZKCurator.class);

    @PostConstruct
    public void init(){
        client=client.usingNamespace("admin");
        try {
            //判断在admin命名空间下是否有bgm节点 /admin/bgm
            if (client.checkExists().forPath("/bgm")==null){
                /**
                 * 对于zk来讲，有两种类型的节点:
                 * 持久节点: 当你创建一个节点的时候，这个节点就永远存在，除非你手动删除
                 * 临时节点: 你创建一个节点之后，会话断开，会自动删除，当然也可以手动删除
                 */
                client.create().creatingParentsIfNeeded()
                        .withMode(CreateMode.PERSISTENT)  //持久节点
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE) //匿名权限
                        .forPath("/bgm");
                log.info("zookeeper初始化成功...");
                log.info("zookeeper服务器状态:{}",client.isStarted());
            }
        }catch (Exception e){
            log.error("zookeeper客户端连接，初始化错误...");
            e.printStackTrace();
        }
    }

    /**
     * 增加或者删除bgm，向zookeeper创建子节点，供小程序后端监听
     * @param bgmId
     * @param operObj
     */
    public void sendBgmOperator(String bgmId,String operObj){
        try {
            client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath("/bgm/"+bgmId,operObj.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
