package cn.linhome.lib.cache;

import android.content.Context;

import java.io.File;

import cn.linhome.lib.cache.converter.IEncryptConverter;
import cn.linhome.lib.cache.converter.IObjectConverter;

abstract class FAbstractDisk implements IDisk, IDiskInfo
{
    private File mDirectory;

    private static Context mContext;
    private static IEncryptConverter sGlobalEncryptConverter;
    private static IObjectConverter sGlobalObjectConverter;

    private boolean mEncrypt;
    private IEncryptConverter mEncryptConverter;
    private IObjectConverter mObjectConverter;

    private boolean mMemorySupport;

    protected FAbstractDisk(File directory)
    {
        mDirectory = directory;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public static void init(Context context)
    {
        mContext = context.getApplicationContext();
    }

    /**
     * 设置全局加解密转换器
     *
     * @param globalEncryptConverter
     */
    public static void setGlobalEncryptConverter(IEncryptConverter globalEncryptConverter)
    {
        sGlobalEncryptConverter = globalEncryptConverter;
    }

    /**
     * 设置全局对象转换器
     *
     * @param globalObjectConverter
     */
    public static void setGlobalObjectConverter(IObjectConverter globalObjectConverter)
    {
        sGlobalObjectConverter = globalObjectConverter;
    }

    //---------- IDisk start ----------

    @Override
    public FAbstractDisk setEncrypt(boolean encrypt)
    {
        mEncrypt = encrypt;
        return this;
    }

    @Override
    public FAbstractDisk setMemorySupport(boolean memorySupport)
    {
        mMemorySupport = memorySupport;
        return this;
    }

    @Override
    public FAbstractDisk setEncryptConverter(IEncryptConverter encryptConverter)
    {
        mEncryptConverter = encryptConverter;
        return this;
    }

    @Override
    public FAbstractDisk setObjectConverter(IObjectConverter objectConverter)
    {
        mObjectConverter = objectConverter;
        return this;
    }

    @Override
    public final long size()
    {
        return mDirectory.length();
    }

    @Override
    public final void delete()
    {
        synchronized (FDisk.class)
        {
            deleteFileOrDir(mDirectory);
        }
    }

    //---------- IDisk end ----------

    //---------- IDiskInfo start ----------

    @Override
    public final boolean isEncrypt()
    {
        return mEncrypt;
    }

    @Override
    public final boolean isMemorySupport()
    {
        return mMemorySupport;
    }

    @Override
    public final File getDirectory()
    {
        if (!mDirectory.exists())
        {
            mDirectory.mkdirs();
        }
        return mDirectory;
    }

    @Override
    public final IEncryptConverter getEncryptConverter()
    {
        if (mEncryptConverter != null)
        {
            return mEncryptConverter;
        } else
        {
            return sGlobalEncryptConverter;
        }
    }

    @Override
    public final IObjectConverter getObjectConverter()
    {
        if (mObjectConverter != null)
        {
            return mObjectConverter;
        } else
        {
            return sGlobalObjectConverter;
        }
    }

    //---------- IDiskInfo end ----------

    //---------- util method start ----------

    private static Context getContext()
    {
        return mContext;
    }

    private static void checkContext()
    {
        if (mContext == null)
        {
            throw new NullPointerException("you must invoke FDisk.init(Context) method before this");
        }
    }

    /**
     * 返回外部存储"Android/data/包名/files/dirName"目录
     *
     * @param dirName
     * @return
     */
    protected static final File getExternalFilesDir(String dirName)
    {
        checkContext();
        File dir = getContext().getExternalFilesDir(dirName);
        return dir;
    }

    /**
     * 返回外部存储"Android/data/包名/cache/dirName"目录
     *
     * @param dirName
     * @return
     */
    protected static final File getExternalCacheDir(String dirName)
    {
        checkContext();
        File dir = new File(getContext().getExternalCacheDir(), dirName);
        return dir;
    }

    /**
     * 返回内部存储"/data/包名/files/dirName"目录
     *
     * @param dirName
     * @return
     */
    protected static final File getInternalFilesDir(String dirName)
    {
        checkContext();
        File dir = new File(getContext().getFilesDir(), dirName);
        return dir;
    }

    /**
     * 返回内部存储"/data/包名/cache/dirName"目录
     *
     * @param dirName
     * @return
     */
    protected static final File getInternalCacheDir(String dirName)
    {
        checkContext();
        File dir = new File(getContext().getCacheDir(), dirName);
        return dir;
    }

    private static boolean deleteFileOrDir(File path)
    {
        if (path == null || !path.exists())
        {
            return true;
        }
        if (path.isFile())
        {
            return path.delete();
        }
        File[] files = path.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                deleteFileOrDir(file);
            }
        }
        return path.delete();
    }

    //---------- util method end ----------
}
