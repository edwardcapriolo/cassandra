package org.apache.cassandra.locator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;


public class PropertyOrEnvironmentSeedProviderTest
{

    @Test
    public void propertyBasedTest() throws UnknownHostException{
        System.getProperties().put(PropertyOrEnvironmentSeedProvider.CASSANDRA_SEED_LIST_PROP, "4.4.4.3,4.4.4.5");
        PropertyOrEnvironmentSeedProvider  p = new PropertyOrEnvironmentSeedProvider (new HashMap<>());
        Assert.assertEquals(
                Arrays.asList(
                        InetAddress.getByName("4.4.4.3"), 
                        InetAddress.getByName("4.4.4.5")), p.getSeeds());
    }
}
