package cn.linhome.lib.cache.handler;

import java.io.File;
import java.io.Serializable;

import cn.linhome.lib.cache.IDiskInfo;
import cn.linhome.lib.cache.converter.IEncryptConverter;
import cn.linhome.lib.cache.handler.impl.SerializableHandler;

/**
 * 缓存可以和字符串互相转换的处理类
 */
public abstract class StringConverterHandler<T> extends CacheHandler<T>
{
    private SerializableHandler<CacheModel> mSerializableHandler;

    public StringConverterHandler(IDiskInfo diskInfo)
    {
        super(diskInfo);
    }

    private final SerializableHandler<CacheModel> getSerializableHandler()
    {
        if (mSerializableHandler == null)
        {
            mSerializableHandler = new SerializableHandler(getDiskInfo())
            {
                @Override
                protected String getKeyPrefix()
                {
                    return StringConverterHandler.this.getKeyPrefix();
                }
            };
        }
        return mSerializableHandler;
    }

    @Override
    protected final boolean putCacheImpl(String key, T value, File file)
    {
        final boolean encrypt = getDiskInfo().isEncrypt();
        final IEncryptConverter converter = getDiskInfo().getEncryptConverter();
        if (encrypt && converter == null)
        {
            throw new RuntimeException("you must provide an IEncryptConverter instance before this");
        }

        final String data = valueToString(value);
        if (data == null)
        {
            throw new RuntimeException("valueToString(String) method can not return null");
        }

        final CacheModel model = new CacheModel();
        model.data = encrypt ? converter.encrypt(data) : data;
        model.isEncrypted = encrypt;

        if (model.data == null)
        {
            throw new RuntimeException("IEncryptConverter.encrypt(String) method can not return null");
        }

        return getSerializableHandler().putCache(key, model);
    }

    @Override
    protected final T getCacheImpl(String key, Class<T> clazz, File file)
    {
        final CacheModel model = getSerializableHandler().getCache(key, CacheModel.class);
        if (model == null)
        {
            return null;
        }

        final boolean isEncrypted = model.isEncrypted;
        final IEncryptConverter converter = getDiskInfo().getEncryptConverter();
        if (isEncrypted && converter == null)
        {
            throw new RuntimeException("content is encrypted but IEncryptConverter not found when try decrypt");
        }

        if (isEncrypted)
        {
            // 需要解密
            model.data = converter.decrypt(model.data);
        }

        return stringToValue(model.data, clazz);
    }

    /**
     * 把缓存转为字符串，默认返回对象toString()方法的返回值，可以重写改变规则
     *
     * @param value
     * @return
     */
    protected String valueToString(T value)
    {
        return value.toString();
    }

    /**
     * 把字符串转为缓存
     *
     * @param string
     * @param clazz
     * @return
     */
    protected abstract T stringToValue(String string, Class<T> clazz);


    private static final class CacheModel implements Serializable
    {
        static final long serialVersionUID = 0L;

        public boolean isEncrypted = false;
        public String data = null;
    }
}
