//订阅发布
//服务发布
@Override
public void doRegister(URL url){
    try{
        zkClient.create(toUrlPath(url)),url.getParameter(DYNAMIC_KEY,true));
        } catch (Throwable e){
        throw new RpcException("Failed to register "+ url + " to zookeeper"+ getUrl() + ", cause: "+ e.getMessage(),e);
        }
}


//RedisRegistry的构造方法中开启一个定时任务：
this.expireFuture = expireExecutor.scheduleWithFixedDelay(() ->{
    try{
        deferExpired();//Extend the expire time
        } catch (Throwable t){//Defensive fault tolerance
        logger.error("Unexpected exception occur at defer expire time,cause: "+ t.getMessage(),t);

        }
        },expirePeriod / 2, expirePeriod / 2, TimeUnit.MILLISECONDS);


//deferExpired是怎么续期key、删除过期key的
private void deferExpired() {
        for (Map.Entry<String, JedisPool> entry : jedisPools.entrySet()) {
            JedisPool jedisPool = entry.getValue();
            try {
                try (Jedis jedis = jedisPool.getResource()) {
                    for (URL url : new HashSet<>(getRegistered())) {
                        if (url.getParameter(DYNAMIC_KEY, true)) {
                            String key = toCategoryPath(url);
                             // 对key续期，key：/dubbo/com.xx.xxService/providers，url是提供者ip端口等信息
                            // expirePeriod默认值60*1000ms，即1分钟，每次续1分钟
                        if (jedis.hset(key, url.toFullString(), String.valueOf(System.currentTimeMillis() + expirePeriod)) == 1) {
                            // 返回1 说明key已经被删除，这次算重新发布
                            jedis.publish(key, REGISTER);
                        }
                    }
                }
                if (admin) { // 服务治理中心（key过期后不会主动通知，需要治理中心轮询清理）
                    clean(jedis); // 删除过期key
                }
                if (!replicate) {
                    break;//  If the server side has synchronized data, just write a single machine
                }
            }
        } catch (Throwable t) {
            logger.warn("Failed to write provider heartbeat to redis registry. registry: " + entry.getKey() + ", cause: " + t.getMessage(), t);
            }
        }
}
//clean(jedis)会删除掉已过期的Key，并且发布unregister事件：
if (expire < now) {
    jedis.hdel(key, entry.getKey());
    jedis.publish(key, UNREGISTER);
}
