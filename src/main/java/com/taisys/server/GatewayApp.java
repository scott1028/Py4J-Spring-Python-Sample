package com.taisys.server;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;
import py4j.GatewayServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class GatewayApp {
    private static final Logger logger = Logger.getLogger(GatewayApp.class);

    @Resource(name="jedisPool")
    private JedisPool jedisPool;

    public static void main(String[] args) {
        ApplicationContext context =
                new FileSystemXmlApplicationContext("src/main/resources/config.xml");

        // 取代 GatewayApp app =  new GatewayApp();
        // 使用 context.getBean(GatewayApp.class); 實例獲得 Spring 載入功能
        GatewayApp app = context.getBean(GatewayApp.class);
        GatewayServer server = new GatewayServer(app);
        server.start();
    }


    public int addition(int first, int second) throws Exception {
        return first + second;
    }

    public int testAdd(int a, int b) throws Exception {
        this.acquireLock("11", 1);
        int res = a*b;
        this.releaseLock("11", 1);
        return res;
    }

    // 獲得互斥鎖
    public void acquireLock(String lockTag, int DatabaseNo) throws Exception {
        try{
            logger.info("[acquireLock][start] Lock: " + lockTag);
            Jedis jedis = jedisPool.getResource();
            jedis.select(DatabaseNo);
            Long flag = 0l;
            logger.info("[acquireLock] Try to acquire Lock, flag: " + flag);
            long begin_timestamp = new Date().getTime();
            while(flag == 0l && begin_timestamp + 1000l*60*2 >= new Date().getTime()) {
                // This redis had a deadlock, so set it expired after 60 seconds.
                if(jedis.ttl(lockTag) == -1l){
                    jedis.expire(lockTag, 60);
                }
                flag = jedis.setnx(lockTag, "True");
                Thread.sleep(500);
            }
            logger.info("[acquireLock] Got a Lock, flag: " + flag);
            if (flag == 1) {
                jedis.expire(lockTag, 60);
                jedisPool.returnResource(jedis);
                logger.info("[acquireLock][end] Lock: " + lockTag);
                return;
            } else {
                logger.info("[acquireLock][end] Timeout! Can't not acquire mutex lock from Redis. Lock: " + lockTag);
                throw new Exception("Timeout! Can't not acquire mutex lock from Redis.");
            }
        }
        catch(Exception e){
            logger.info("[acquireLock][fail] Lock: " + lockTag);
            throw new Exception("Redis Connection Error.");
        }
    }

    // 解開互斥鎖( 記得於 Exception 狀況也要解開 )
    public void releaseLock(String lockTag, int DatabaseNo) throws Exception {
        try {
            logger.info("[releaseLock][start] Lock: " + lockTag);
            Jedis jedis = jedisPool.getResource();
            jedis.select(DatabaseNo);
            jedis.del(lockTag);
            jedisPool.returnResource(jedis);
            logger.info("[releaseLock][end] Lock: " + lockTag);
        }
        catch(Exception e){
            logger.info("[releaseLock][fail] Lock: " + lockTag);
        }
    }
}
