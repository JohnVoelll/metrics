package com.yammer.metrics.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class InstrumentedAppenderTest {
    final LoggerContext lc = new LoggerContext();
    final Logger logger = lc.getLogger("abc.def");

    final InstrumentedAppender appender = new InstrumentedAppender();

    @BeforeEach
    public void setUp() throws Exception {
        appender.setContext(lc);
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.TRACE);
    }

    @Test
    public void maintainsAccurateCounts() throws Exception {
        assertThat(InstrumentedAppender.ALL_METER.count(), is(0L));
        assertThat(InstrumentedAppender.TRACE_METER.count(), is(0L));
        assertThat(InstrumentedAppender.DEBUG_METER.count(), is(0L));
        assertThat(InstrumentedAppender.INFO_METER.count(), is(0L));
        assertThat(InstrumentedAppender.WARN_METER.count(), is(0L));
        assertThat(InstrumentedAppender.ERROR_METER.count(), is(0L));

        logger.trace("trace");
        assertThat(InstrumentedAppender.ALL_METER.count(), is(1L));
        assertThat(InstrumentedAppender.TRACE_METER.count(), is(1L));
        assertThat(InstrumentedAppender.DEBUG_METER.count(), is(0L));
        assertThat(InstrumentedAppender.INFO_METER.count(), is(0L));
        assertThat(InstrumentedAppender.WARN_METER.count(), is(0L));
        assertThat(InstrumentedAppender.ERROR_METER.count(), is(0L));

        logger.trace("Test");
        logger.debug("Test");
        assertThat(InstrumentedAppender.ALL_METER.count(), is(3L));
        assertThat(InstrumentedAppender.TRACE_METER.count(), is(2L));
        assertThat(InstrumentedAppender.DEBUG_METER.count(), is(1L));
        assertThat(InstrumentedAppender.INFO_METER.count(), is(0L));
        assertThat(InstrumentedAppender.WARN_METER.count(), is(0L));
        assertThat(InstrumentedAppender.ERROR_METER.count(), is(0L));

        logger.info("Test");
        assertThat(InstrumentedAppender.ALL_METER.count(), is(4L));
        assertThat(InstrumentedAppender.TRACE_METER.count(), is(2L));
        assertThat(InstrumentedAppender.DEBUG_METER.count(), is(1L));
        assertThat(InstrumentedAppender.INFO_METER.count(), is(1L));
        assertThat(InstrumentedAppender.WARN_METER.count(), is(0L));
        assertThat(InstrumentedAppender.ERROR_METER.count(), is(0L));

        logger.warn("Test");
        assertThat(InstrumentedAppender.ALL_METER.count(), is(5L));
        assertThat(InstrumentedAppender.TRACE_METER.count(), is(2L));
        assertThat(InstrumentedAppender.DEBUG_METER.count(), is(1L));
        assertThat(InstrumentedAppender.INFO_METER.count(), is(1L));
        assertThat(InstrumentedAppender.WARN_METER.count(), is(1L));
        assertThat(InstrumentedAppender.ERROR_METER.count(), is(0L));

        logger.error("Test");
        assertThat(InstrumentedAppender.ALL_METER.count(), is(6L));
        assertThat(InstrumentedAppender.TRACE_METER.count(), is(2L));
        assertThat(InstrumentedAppender.DEBUG_METER.count(), is(1L));
        assertThat(InstrumentedAppender.INFO_METER.count(), is(1L));
        assertThat(InstrumentedAppender.WARN_METER.count(), is(1L));
        assertThat(InstrumentedAppender.ERROR_METER.count(), is(1L));
    }
}
