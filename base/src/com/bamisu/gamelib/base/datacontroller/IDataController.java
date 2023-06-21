package com.bamisu.gamelib.base.datacontroller;

import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;

import java.util.List;
import java.util.Map;

public interface IDataController
{
  public Object get(String name)
          throws DataControllerException;

  public Map<String, Object> multiget(List<String> keys)
          throws DataControllerException;

  public void set(String name, Object data)
          throws DataControllerException;

  public void set(String name, int expiredTime, Object data)
          throws DataControllerException;

  public void add(String name, Object data)
          throws DataControllerException;

  public void delete(String name)
          throws DataControllerException;

  public Object getCache(String name)
          throws DataControllerException;

  public void setCache(String name, int expire, Object data)
          throws DataControllerException;

  public void deleteCache(String name)
          throws DataControllerException;

  public void shutdown();

  public long getCASValue(String name)
          throws DataControllerException;

  public CASValue getS(String name);

  public CASResponse checkAndSet(String name, long casValue, Object data)
          throws DataControllerException;


}
