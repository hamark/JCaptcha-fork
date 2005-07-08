package com.octo.captcha.engine.bufferedengine.manager;

import java.text.ParseException;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.octo.captcha.CaptchaException;
import com.octo.captcha.engine.bufferedengine.ContainerConfiguration;
import com.octo.captcha.engine.bufferedengine.QuartzBufferedEngineContainer;

/**
 * Manager of a Quartz Buffered Engine
 * 
 * @author Benoit Doumas
 */
public class QuartzBufferedEngineManager implements BufferedEngineContainerManager
{
    private static final Log log = LogFactory.getLog(QuartzBufferedEngineManager.class.getName());

    private static QuartzBufferedEngineManager helperInstance = null;

    Scheduler schduler = null;

    CronTrigger cronFeeder = null;

    CronTrigger cronSwapper = null;

    QuartzBufferedEngineContainer container = null;

    ContainerConfiguration config = null;

    JobDetail jobFeeder = null;

    JobDetail jobSwapper = null;

    /**
     * Constructor of the manager
     * 
     * @param container
     * The QuartzBufferedEngineContainer
     * @param factory
     * 	The scheduler Factory to manipulate Qua	rtz
     * @param cronFeeder
     * The CronTrigger that feed the persistent memory
     * @param cronSwapper
     * 	The CronTrigger that swap between the persistent memory and the volatile memory
     * @param jobFeeder
     * Job detail of the feeding job
     * @param jobSwapper
     * Job detail of the swapping job
     */
    public QuartzBufferedEngineManager(QuartzBufferedEngineContainer container,
        org.quartz.Scheduler factory, CronTrigger cronFeeder, CronTrigger cronSwapper,
        JobDetail jobFeeder, JobDetail jobSwapper)
    {
        this.cronFeeder = cronFeeder;
        this.cronSwapper = cronSwapper;
        this.jobFeeder = jobFeeder;
        this.jobSwapper = jobSwapper;
        this.schduler = factory;
        this.container = container;
        this.config = container.getConfig();
    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#startToFeedPersistantBuffer()
     */
    public synchronized void startToFeedPersistantBuffer()
    {
        try
        {
            String name = cronFeeder.getName();
            String groupeName = cronFeeder.getGroup();
            schduler.resumeTrigger(name, groupeName);
        }
        catch (SchedulerException e)
        {
            throw new CaptchaException(e);
        }
    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#stopToFeedPersistentBuffer()
     */
    public synchronized void stopToFeedPersistentBuffer()
    {
        try
        {
            String name = cronFeeder.getName();
            String groupeName = cronFeeder.getGroup();
            schduler.pauseTrigger(name, groupeName);
        }
        catch (SchedulerException e)
        {
            throw new CaptchaException(e);
        }
    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#startToSwapFromPersistentToVolatileMemory()
     */
    public synchronized void startToSwapFromPersistentToVolatileMemory()
    {
        try
        {
            String name = cronSwapper.getName();
            String groupeName = cronSwapper.getGroup();
            schduler.resumeTrigger(name, groupeName);
        }
        catch (SchedulerException e)
        {
            throw new CaptchaException(e);
        }

    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#stopToSwapFromPersistentToVolatileMemory()
     */
    public void stopToSwapFromPersistentToVolatileMemory()
    {
        try
        {
            String name = cronSwapper.getName();
            String groupeName = cronSwapper.getGroup();
            schduler.pauseTrigger(name, groupeName);
        }
        catch (SchedulerException e)
        {
            throw new CaptchaException(e);
        }
    }

    public void setFeedCronExpr(String feedCronExpr)
    {
        if (!cronFeeder.getCronExpression().equalsIgnoreCase(feedCronExpr))
        {
            log.info("set new cron expr for feed");
            try
            {
                String name = cronFeeder.getName();
                String groupeName = cronFeeder.getGroup();
                schduler.unscheduleJob(name, groupeName);
                cronFeeder.setCronExpression(feedCronExpr);
                schduler.scheduleJob(jobFeeder, cronFeeder);
            }
            catch (ParseException e)
            {
                throw new CaptchaException(e);
            }
            catch (SchedulerException e)
            {
                throw new CaptchaException(e);
            }
        }
    }

    public void setSwapCronExpr(String swapCronExpr)
    {
        if (!cronSwapper.getCronExpression().equalsIgnoreCase(swapCronExpr))
        {
            log.info("set new cron expr for swap");
            try
            {
                String name = cronSwapper.getName();
                String groupeName = cronSwapper.getGroup();
                schduler.unscheduleJob(name, groupeName);
                cronSwapper.setCronExpression(swapCronExpr);
                schduler.scheduleJob(jobSwapper, cronSwapper);

            }
            catch (ParseException e)
            {
                throw new CaptchaException(e);
            }
            catch (SchedulerException e)
            {
                throw new CaptchaException(e);
            }
        }
    }

    public String getFeedCronExpr()
    {
        return cronFeeder.getCronExpression();
    }

    public String getSwapCronExpr()
    {
        return cronSwapper.getCronExpression();
    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#pause()
     */
    public void pause()
    {
        try
        {
            schduler.pause();
        }
        catch (SchedulerException e)
        {
            throw new CaptchaException(e);
        }

    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#resume()
     */
    public void resume()
    {
        try
        {
            schduler.start();
        }
        catch (SchedulerException e)
        {
            throw new CaptchaException(e);
        }

    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#shutdown()
     */
    public void shutdown()
    {
        try
        {
            schduler.shutdown(true);

            while (!schduler.isShutdown())
            {
                ; //wait
            }
            container.getPersistentBuffer().dispose();
        }
        catch (SchedulerException e)
        {
            throw new CaptchaException(e);
        }

    }

    public Integer getPersistentFeedings()
    {
        return container.getPersistentFeedings();
    }

    public Integer getPersistentMemoryHits()
    {
        return container.getPersistentMemoryHits();
    }

    public Integer getPersistentToVolatileSwaps()
    {
        return container.getPersistentToVolatileSwaps();
    }

    public Integer getVolatileMemoryHits()
    {
        return container.getVolatileMemoryHits();
    }

    /**
     * @return
     */
    public Integer  getFeedSize()
    {
        return config.getFeedSize();
    }

    /**
     * @param feedSize
     */
    public void setFeedSize(Integer  feedSize)
    {
        config.setFeedSize(feedSize);
    }

    /**
     * @return
     */
    public HashedMap getLocaleRatio()
    {
        return config.getLocaleRatio();
    }

    /**
     * @param localeRatio
     */
public synchronized void setLocaleRatio(String localeName, double ratio)
    {
       
        Locale locale = getLocaleFromName(localeName);
        
        MapIterator it = config.getLocaleRatio().mapIterator();
        boolean isSet = false;
        double coef = ratio;
        double oldValue = 0.0f;
        
        if (config.getLocaleRatio().containsKey(locale))
        {
            oldValue = ((Double) config.getLocaleRatio().get(locale)).doubleValue();
            coef = ratio - oldValue;
        }

        while (it.hasNext())
        {
            Locale tempLocale = (Locale) it.next();
            double value = ((Double) it.getValue()).doubleValue();
            if (locale.equals(tempLocale))
            {
                it.setValue(new Double(coef + value));
                isSet = true;
            }
            else
            {
                if (coef < 0)
                {
                    it.setValue(new Double(value - ( coef * value / (1.0 - oldValue) )) );
                }
                else
                {
                    it.setValue(new Double(value - (value * coef)));
                }
            }
        }

        //if Locale is not register yet
        if (!isSet)
        {
            config.getLocaleRatio().put(locale, new Double(ratio));
        }
    }
    protected Locale getLocaleFromName(String localeName)
    {
        StringTokenizer tokenizer = new StringTokenizer(localeName, "_");
        int count = tokenizer.countTokens();
        switch (count)
        {
            case 2:
                return new Locale(tokenizer.nextToken(), tokenizer.nextToken());
            case 3:
                return new Locale(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken());
            default:
                return Locale.getDefault();
        }        
        /*String[] localeTab = localeName.split("_");
        switch (localeTab.length)
        {
            case 1:
                return  new Locale(localeTab[0]);
            case 2:
                return new Locale(localeTab[0], localeTab[1]);
            case 3:
                return new Locale(localeTab[0], localeTab[1], localeTab[2]);
            default:
                return Locale.getDefault();
        }
        */
    }

    public synchronized void removeLocaleRatio(String  localeName)
    {
        Locale locale = getLocaleFromName(localeName);
        //if it exist
        if (config.getLocaleRatio().containsKey(locale))
        {
            //first set at 0, so the other ratio are updated
            setLocaleRatio(localeName, 0.0);
            //then remove
            config.getLocaleRatio().remove(locale);
        }
    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#getMaxPersistentMemorySize
     */
    public Integer getMaxPersistentMemorySize()
    {
        return config.getMaxPersistentMemorySize();
    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#setMaxPersistentMemorySize
     */
    public void setMaxPersistentMemorySize(Integer maxPersistentMemorySize)
    {
        config.setMaxPersistentMemorySize(maxPersistentMemorySize);
    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#getMaxVolatileMemorySize
     */
    public Integer  getMaxVolatileMemorySize()
    {
        return config.getMaxVolatileMemorySize();
    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#setMaxVolatileMemorySize
     */
    public void setMaxVolatileMemorySize(Integer maxVolatileMemorySize)
    {
        config.setMaxVolatileMemorySize(maxVolatileMemorySize);
    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#getSwapSize
     */
    public Integer getSwapSize()
    {
        return config.getSwapSize();
    }

    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#setSwapSize
     */
    public void setSwapSize(Integer swapSize)
    {
        config.setSwapSize(swapSize);
    }
    
    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#getVolatileBufferSize
     */
    public int getVolatileBufferSize()
    {
        return container.getVolatileBuffer().size();
    }
    
    /**
     * @see com.octo.captcha.engine.bufferedengine.manager.BufferedEngineContainerManager#getVolatileBufferSizeByLocales
     */
    public HashedMap getVolatileBufferSizeByLocales()
    {
        HashedMap map = new HashedMap();
        
         Iterator it = container.getVolatileBuffer().getLocales().iterator();
         while (it.hasNext())
         {
             Locale locale  = (Locale) it.next();
             map.put(locale, new Integer(container.getVolatileBuffer().size(locale)));
         }
         return map;
    }
    
    /**
     * @return
     */
    public int getPersistentBufferSize()
    {
        return container.getPersistentBuffer().size();
    }
    
    /**
     * @return
     */
    public HashedMap getPersistentBufferSizesByLocales()
    {
        HashedMap map = new HashedMap();
        
         Iterator it = container.getPersistentBuffer().getLocales().iterator();
         while (it.hasNext())
         {
             Locale locale  = (Locale) it.next();
             map.put(locale, new Integer(container.getPersistentBuffer().size(locale)));
         }
         return map;
    }
    
    public void clearVolatileBuffer()
    {
        container.getVolatileBuffer().clear();
    }
    
    public void clearPersistentBuffer()
    {
        container.getPersistentBuffer().clear();
    }
    
}
