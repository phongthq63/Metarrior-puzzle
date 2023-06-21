package com.bamisu.gamelib.base.datacontroller.couchbase;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import com.bamisu.gamelib.base.datacontroller.DataControllerException;
import com.bamisu.gamelib.base.config.ConfigHandle;
import com.bamisu.gamelib.base.datacontroller.IDataController;
import com.bamisu.gamelib.utils.business.CommonHandle;
import com.bamisu.gamelib.utils.business.Debug;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.extensions.BaseSFSExtension;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CouchbaseDataController implements IDataController {
    private Logger logger = Logger.getLogger(CouchbaseDataController.class);
    static CouchbaseDataController _instance;
    static final Object lock = new Object();

    protected CouchbaseClient dataCli;
//    protected CouchbaseClient cacheCli;
    protected Zone zone;
    protected BaseSFSExtension extension;

    public CouchbaseDataController(Zone zone) {
        try {
            this.zone = zone;
            this.extension = ((BaseSFSExtension) zone.getExtension());

            extension.trace(" CouchbaseDataController: ------ init");
            String hostStr = extension.getConfigProperties().getProperty("cluster_servers");
            String[] hostArr = hostStr.split(",");

            String dataBucket = extension.getConfigProperties().getProperty("data_bucket");
            String cacheBucket = extension.getConfigProperties().getProperty("cache_bucket");
            Long blockTime = Long.valueOf(extension.getConfigProperties().getProperty("opsBlockTime"));
            Long timeout = Long.valueOf(extension.getConfigProperties().getProperty("opsTimeOut"));
            String pass = extension.getConfigProperties().getProperty("bucket_pass");//
            ArrayList<URI> addrs = new ArrayList<URI>();
            for (String h : hostArr) {
                addrs.add(URI.create(h));
            }
            CouchbaseConnectionFactoryBuilder cfb = new CouchbaseConnectionFactoryBuilder();
            cfb.setOpTimeout(timeout);
            cfb.setReadBufferSize(1024);
            cfb.setShouldOptimize(true);
            cfb.setTimeoutExceptionThreshold(100);
            dataCli = new CouchbaseClient(cfb.buildCouchbaseConnection(addrs, dataBucket, pass));
//            cacheCli = new CouchbaseClient(cfb.buildCouchbaseConnection(addrs, cacheBucket, pass));
            extension.trace(" CouchbaseDataController: ------ init success");

        } catch (Exception e) {
            e.printStackTrace();
            CommonHandle.writeErrLog(e);
        }
    }

    public CouchbaseDataController(String cluster_servers, String data_bucket, String cache_bucket, String bucket_pass) {
        try {
            String hostStr = cluster_servers;
            String[] hostArr = hostStr.split(",");

            String dataBucket = data_bucket;
            String cacheBucket = cache_bucket;
            Long blockTime = Long.valueOf(1000);
            Long timeout = Long.valueOf(5000);
            String pass = bucket_pass;
            ArrayList<URI> addrs = new ArrayList<URI>();
            for (String h : hostArr) {
                addrs.add(URI.create(h));
            }
            CouchbaseConnectionFactoryBuilder cfb = new CouchbaseConnectionFactoryBuilder();
            cfb.setOpTimeout(timeout);
            cfb.setReadBufferSize(1024);
            cfb.setShouldOptimize(true);
            cfb.setTimeoutExceptionThreshold(100);
            dataCli = new CouchbaseClient(cfb.buildCouchbaseConnection(addrs, dataBucket, pass));
//            cacheCli = new CouchbaseClient(cfb.buildCouchbaseConnection(addrs, cacheBucket, pass));
            System.out.println(" CouchbaseDataController: ------ init success " + data_bucket);
        } catch (Exception e) {
            e.printStackTrace();
            CommonHandle.writeErrLog(e);
        }
    }

    @Override
    public Object get(String name) throws DataControllerException {
//        Debug.trace("get:" + name);
        try {
            return dataCli.get(name);
        } catch (Exception e) {
            this.logger.info("cbGet error with key " + name);
            throw e;
        }

    }

    @Override
    public Map<String, Object> multiget(List<String> keys)
            throws DataControllerException {
        return dataCli.getBulk(keys);
    }

    @Override
    public void set(String name, Object data) throws DataControllerException {
        try {
            dataCli.set(name, 0, data);
        } catch (Exception e) {
            this.logger.info("cbSet error with key " + name);
            this.logger.info(data);
            throw e;
        }

    }

    @Override
    public void set(String name, int expiredTime, Object data) throws DataControllerException {
        dataCli.set(name, expiredTime, data);

    }

    @Override
    public void add(String name, Object data) throws DataControllerException {
        dataCli.add(name, 0, data);
    }

    @Override
    public void delete(String name) throws DataControllerException {
        dataCli.delete(name);
    }

    @Override
    public Object getCache(String name) throws DataControllerException {
        return null;
    }

    @Override
    public void setCache(String name, int expire, Object data)
            throws DataControllerException {
//        cacheCli.set(name, expire, data);
    }

    @Override
    public void deleteCache(String name) throws DataControllerException {
//        cacheCli.delete(name);
    }

    @Override
    public void shutdown() {
        dataCli.shutdown(3, TimeUnit.SECONDS);
//        cacheCli.shutdown(3, TimeUnit.SECONDS);
    }

    @Override
    public long getCASValue(String name) throws DataControllerException {
        CASValue casVak = dataCli.gets(name);
        if (casVak == null)
            return 0;
        return casVak.getCas();
    }

    @Override
    public CASValue getS(String name) {
        return dataCli.gets(name);
    }

    @Override
    public CASResponse checkAndSet(String name, long casValue, Object data)
            throws DataControllerException {
        return dataCli.cas(name, casValue, data);
    }

    public CouchbaseClient getClient() {
        return dataCli;
    }
}
