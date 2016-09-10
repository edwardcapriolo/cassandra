package org.apache.cassandra.locator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.exceptions.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyOrEnvironmentSeedProvider extends SeedProvider
{

    private static final Logger logger = LoggerFactory.getLogger(PropertyOrEnvironmentSeedProvider.class);
    public static final String CASSANDRA_SEED_LIST_PROP = "cassandra.seed.list";
    public static final String CASSANDRA_SEED_LIST_ENV = "CASSANDRA_SEED_LIST";

    public PropertyOrEnvironmentSeedProvider(Map<String, String> args)
    {
        super(args);
    }

    @Override
    public List<InetAddress> getSeeds()
    {
        String value = System.getProperty(CASSANDRA_SEED_LIST_PROP);
        if (value == null)
        {
            value = System.getenv(CASSANDRA_SEED_LIST_ENV);
        }
        if (value == null)
        {
            throw new ConfigurationException(String.format(
                    "You must specify either a property named %s or an environment variable named %s "+
                    "with seeds specified as a comma seperated list.",
                    CASSANDRA_SEED_LIST_PROP, CASSANDRA_SEED_LIST_ENV), false);
        }
        String[] hosts = value.split(",", -1);
        List<InetAddress> seeds = new ArrayList<InetAddress>(hosts.length);
        for (String host : hosts)
        {
            try
            {
                seeds.add(InetAddress.getByName(host.trim()));
            } catch (UnknownHostException ex)
            {
                logger.warn("Seed provider couldn't lookup host {}", host);
            }
        }
        return Collections.unmodifiableList(seeds);
    }

}