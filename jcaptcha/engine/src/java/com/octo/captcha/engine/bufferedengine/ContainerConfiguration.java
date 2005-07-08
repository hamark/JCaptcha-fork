package com.octo.captcha.engine.bufferedengine;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

/**
 * Class that contains informations to configure the BufferedEngineContainer.
 * 
 * @author Benoit Doumas
 */
public class ContainerConfiguration
{
    protected Integer feedSize = new Integer(40);

    protected Integer swapSize = new Integer(10);

    protected Integer maxVolatileMemorySize = new Integer(100);

    protected Integer maxPersistentMemorySize = new Integer(1000);

    protected HashedMap localeRatio = new HashedMap();

    /**
     * Contructs a ContainerConfiguration with default feed size and sawp size
     * 
     * @param localeRatio
     * @param maxMemorySize
     * @param maxDiskSize
     */
    public ContainerConfiguration(Map localeRatio, int maxVolatileMemorySize, int maxPersistentMemorySize)
    {
        this(localeRatio, maxVolatileMemorySize, maxPersistentMemorySize, 40, 50);
    }

    /**
     * Contructs a ContainerConfiguration with custom feed size and sawp size
     * 
     * @param localeRatio
     * @param maxMemorySize
     * @param maxDiskSize
     * @param swapSize
     * @param feedSize
     */
    public ContainerConfiguration(Map localeRatio, int maxVolatileMemorySize, int maxPersistentMemorySize,
        int swapSize, int feedSize)
    {
        this.localeRatio.putAll(localeRatio);
        this.maxVolatileMemorySize = new Integer(maxVolatileMemorySize);
        this.maxPersistentMemorySize = new Integer(maxPersistentMemorySize);
        this.feedSize = new Integer(feedSize);
        this.swapSize = new Integer(swapSize);
    }

    /**
     * @return
     */
    public Integer getFeedSize()
    {
        return feedSize;
    }

    /**
     * @param feedSize
     */
    public void setFeedSize(Integer feedSize)
    {
        this.feedSize = feedSize;
    }

    /**
     * @return
     */
    public HashedMap getLocaleRatio()
    {
        return localeRatio;
    }

    /**
     * @param localeRatio
     */
    public void setLocaleRatio(HashedMap localeRatio)
    {
        this.localeRatio = localeRatio;
    }

    /**
     * @return
     */
    public Integer getMaxPersistentMemorySize()
    {
        return maxPersistentMemorySize;
    }

    /**
     * @param maxDiskSize
     */
    public void setMaxPersistentMemorySize(Integer maxPersistentMemorySize)
    {
        this.maxPersistentMemorySize = maxPersistentMemorySize;
    }

    /**
     * @return
     */
    public Integer getMaxVolatileMemorySize()
    {
        return maxVolatileMemorySize;
    }

    /**
     * @param maxMemorySize
     */
    public  void setMaxVolatileMemorySize(Integer maxVolatileMemorySize)
    {
        this.maxVolatileMemorySize = maxVolatileMemorySize;
    }

    /**
     * @return
     */
    public Integer getSwapSize()
    {
        return swapSize;
    }

    /**
     * @param swapSize
     */
    public void  setSwapSize(Integer swapSize)
    {
        this.swapSize = swapSize;
    }
}
