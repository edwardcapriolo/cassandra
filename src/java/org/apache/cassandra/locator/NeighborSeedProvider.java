package org.apache.cassandra.locator;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.utils.FBUtilities;
/**
 * Adds ipranges relative to the address of the host as seeds.
 *
 */
public class NeighborSeedProvider extends SeedProvider
{
    /**
     * The range of ip addresses below and above the local address of this host
     */
    public static final String SCAN_DISTANCE = "scan.distance";
    /**
     * should we list ourself as a seed
     */
    public static final String SELF_AS_SEED = "self.as.seed";
    private final int DEFAULT_SCAN_DISTANCE = 50;
    private final boolean DEFAULT_SELF_AS_SEED = false;

    private int scanDistance;
    private boolean selfAsSeed;

    public NeighborSeedProvider(Map<String, String> args)
    {
        super(args);
        scanDistance = Integer.valueOf(args.getOrDefault(SCAN_DISTANCE, String.valueOf(DEFAULT_SCAN_DISTANCE)));
        selfAsSeed = Boolean.valueOf(args.getOrDefault(SELF_AS_SEED, String.valueOf(DEFAULT_SELF_AS_SEED)));
    }

    @Override
    public List<InetAddress> getSeeds()
    {
        InetAddress myself = FBUtilities.getLocalAddress();
        List<InetAddress> seeds = new ArrayList<InetAddress>();
        seeds.addAll(getLower(myself));
        if (selfAsSeed)
            seeds.add(myself);
        seeds.addAll(getHigher(myself));
        return Collections.unmodifiableList(seeds);
    }

    private List<InetAddress> getLower(InetAddress iter)
    {
        List<InetAddress> lowerRange = new ArrayList<>();
        int i = 0;
        while (i < scanDistance)
        {
            iter = com.google.common.net.InetAddresses.decrement(iter);
            lowerRange.add(iter);
            i++;
        }
        return lowerRange;
    }

    private List<InetAddress> getHigher(InetAddress iter)
    {
        List<InetAddress> higherRange = new ArrayList<>();
        int i = 0;
        while (i < scanDistance)
        {
            iter = com.google.common.net.InetAddresses.increment(iter);
            higherRange.add(iter);
            i++;
        }
        return higherRange;
    }

}
