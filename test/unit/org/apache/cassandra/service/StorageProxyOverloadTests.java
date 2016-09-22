package org.apache.cassandra.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import org.apache.cassandra.OrderedJUnit4ClassRunner;
import org.apache.cassandra.config.DatabaseDescriptor;
import org.apache.cassandra.db.Keyspace;
import org.apache.cassandra.exceptions.ConfigurationException;
import org.apache.cassandra.exceptions.OverloadedException;
import org.apache.cassandra.locator.IEndpointSnitch;
import org.apache.cassandra.locator.PropertyFileSnitch;
import org.apache.cassandra.metrics.StorageMetrics;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(OrderedJUnit4ClassRunner.class)
public class StorageProxyOverloadTests
{

    private static InetAddress me;
    private static InetAddress someoneElse;
    
    @BeforeClass
    public static void setUp() throws ConfigurationException, UnknownHostException
    {
        DatabaseDescriptor.daemonInitialization();
        IEndpointSnitch snitch = new PropertyFileSnitch();
        DatabaseDescriptor.setEndpointSnitch(snitch);
        Keyspace.setInitialized();
        me = InetAddress.getLocalHost();
        someoneElse = InetAddress.getByName("127.0.0.5");
    }

    private void resetSingletonCounter(InetAddress address) throws ExecutionException
    {
        StorageMetrics.getHintsInProgress().get(address)
                .dec(StorageMetrics.getHintsInProgress().get(address).getCount());
    }
    
    @After
    public void clearUpSingletonMetrics() throws ExecutionException
    {
        StorageMetrics.getHintsInProgress().invalidateAll();
        resetSingletonCounter(me);
        resetSingletonCounter(someoneElse);
    }
    
    @Test(expected = OverloadedException.class)
    public void testOverloadedExceptionThrownWithHintsInProgress() throws UnknownHostException
    {
        StorageProxy.instance.setMaxHintsInProgress(-1);
        Assert.assertEquals(0, StorageProxy.getHintsInProgressFor(me).getCount());
        StorageProxy.getHintsInProgressFor(me).inc();
        StorageProxy.getHintsInProgressFor(me).dec();
        StorageProxy.getHintsInProgressFor(me).inc();
        Assert.assertEquals(1, StorageProxy.getHintsInProgressFor(me).getCount());
        Assert.assertTrue(StorageProxy.shouldHint(me));
        StorageProxy.checkHintOverload(me);
    }
    
    @Test
    public void ensureInetLookup() throws UnknownHostException, InterruptedException
    {
        StorageProxy.getHintsInProgressFor(me).inc();
        StorageProxy.getHintsInProgressFor(someoneElse).inc();
        Assert.assertEquals(1, StorageProxy.getHintsInProgressFor(me).getCount());
        Assert.assertEquals(1, StorageProxy.getHintsInProgressFor(someoneElse).getCount());
    }
    
}
