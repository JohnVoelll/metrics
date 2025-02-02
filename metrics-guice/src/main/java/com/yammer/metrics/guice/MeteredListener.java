package com.yammer.metrics.guice;

import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.yammer.metrics.core.MetricsRegistry;
import com.yammer.metrics.core.MeterMetric;

import java.lang.reflect.Method;

/**
 * A listener which adds method interceptors to metered methods.
 */
public class MeteredListener implements TypeListener {
    private final MetricsRegistry metricsRegistry;

    public MeteredListener(MetricsRegistry metricsRegistry) {
        this.metricsRegistry = metricsRegistry;
    }

    @Override
    public <T> void hear(TypeLiteral<T> literal,
                         TypeEncounter<T> encounter) {
        for (Method method : literal.getRawType().getMethods()) {
            final Metered annotation = method.getAnnotation(Metered.class);
            if (annotation instanceof Metered m) {
                final String name = m.name().isEmpty() ? method.getName() : m.name();
                final MeterMetric meter = metricsRegistry.newMeter(literal.getRawType(), name, m.eventType(), m.rateUnit());
                encounter.bindInterceptor(Matchers.only(method), new MeteredInterceptor(meter));
            }
        }
    }
}
