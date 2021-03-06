package cn.linhome.lib.cache.api.impl;


import cn.linhome.lib.cache.IDiskInfo;
import cn.linhome.lib.cache.api.IObjectCache;
import cn.linhome.lib.cache.handler.impl.ObjectHandler;

/**
 * 对象缓存
 */
public class ObjectCache implements IObjectCache
{
    private final IDiskInfo mDiskInfo;

    public ObjectCache(IDiskInfo diskInfo)
    {
        mDiskInfo = diskInfo;
    }

    @Override
    public boolean put(Object value)
    {
        if (value == null)
        {
            return false;
        }

        final String key = value.getClass().getName();
        return new ObjectHandler<>(mDiskInfo).putCache(key, value);
    }

    @Override
    public <T> T get(Class<T> clazz)
    {
        final String key = clazz.getName();
        return new ObjectHandler<T>(mDiskInfo).getCache(key, clazz);
    }

    @Override
    public boolean remove(Class clazz)
    {
        final String key = clazz.getName();
        return new ObjectHandler<>(mDiskInfo).removeCache(key);
    }
}
