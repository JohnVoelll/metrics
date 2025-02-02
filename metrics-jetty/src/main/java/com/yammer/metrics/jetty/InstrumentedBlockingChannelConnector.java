package com.yammer.metrics.jetty;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.CounterMetric;
import com.yammer.metrics.core.MeterMetric;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.core.TimerMetric;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.server.nio.BlockingChannelConnector;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class InstrumentedBlockingChannelConnector extends BlockingChannelConnector {
    private final TimerMetric duration;
    private final MeterMetric accepts, connects, disconnects;
    private final CounterMetric connections;

    public InstrumentedBlockingChannelConnector(int port) {
        this(Metrics.defaultRegistry(), port);
    }

    public InstrumentedBlockingChannelConnector(MetricsRegistry registry,
                                                int port) {
        super();
        setPort(port);
        this.duration = registry.newTimer(BlockingChannelConnector.class,
                                          "connection-duration",
                                          String.valueOf(port),
                                          TimeUnit.MILLISECONDS,
                                          TimeUnit.SECONDS);
        this.accepts = registry.newMeter(BlockingChannelConnector.class,
                                         "accepts",
                                         String.valueOf(port),
                                         "connections",
                                         TimeUnit.SECONDS);
        this.connects = registry.newMeter(BlockingChannelConnector.class,
                                          "connects",
                                          String.valueOf(port),
                                          String.valueOf(port),
                                          TimeUnit.SECONDS);
        this.disconnects = registry.newMeter(BlockingChannelConnector.class,
                                             "disconnects",
                                             String.valueOf(port),
                                             "connections",
                                             TimeUnit.SECONDS);
        this.connections = registry.newCounter(BlockingChannelConnector.class,
                                               "active-connections",
                                               String.valueOf(port));
    }

    @Override
    public void accept(int acceptorID) throws IOException, InterruptedException {
        super.accept(acceptorID);
        accepts.mark();
    }

    @Override
    protected void connectionOpened(Connection connection) {
        connections.inc();
        super.connectionOpened(connection);
        connects.mark();
    }

    @Override
    protected void connectionClosed(Connection connection) {
        super.connectionClosed(connection);
        disconnects.mark();
        long duration = System.currentTimeMillis() - connection.getTimeStamp();
        this.duration.update(duration, TimeUnit.MILLISECONDS);
        connections.dec();
    }
}
