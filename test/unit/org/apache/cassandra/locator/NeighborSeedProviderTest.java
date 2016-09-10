package org.apache.cassandra.locator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import junit.framework.Assert;

public class NeighborSeedProviderTest
{

    @Test
    public void checkLower() throws UnknownHostException
    {
        Map<String, String> props = new HashMap<>();
        props.put(NeighborSeedProvider.SCAN_DISTANCE, "3");
        NeighborSeedProvider n = new NeighborSeedProvider(props);
        Assert.assertEquals(Arrays.asList(InetAddress.getByName("127.0.0.0"), InetAddress.getByName("126.255.255.255"),
                InetAddress.getByName("126.255.255.254"), InetAddress.getByName("127.0.0.2"),
                InetAddress.getByName("127.0.0.3"), InetAddress.getByName("127.0.0.4")), n.getSeeds());
    }

    @Test
    public void checkSelf() throws UnknownHostException
    {
        Map<String, String> props = new HashMap<>();
        props.put(NeighborSeedProvider.SCAN_DISTANCE, "3");
        props.put(NeighborSeedProvider.SELF_AS_SEED, "true");
        NeighborSeedProvider n = new NeighborSeedProvider(props);
        Assert.assertEquals(Arrays.asList(InetAddress.getByName("127.0.0.0"), InetAddress.getByName("126.255.255.255"),
                InetAddress.getByName("126.255.255.254"), InetAddress.getByName("127.0.0.1"),
                InetAddress.getByName("127.0.0.2"), InetAddress.getByName("127.0.0.3"),
                InetAddress.getByName("127.0.0.4")), n.getSeeds());
    }

}
