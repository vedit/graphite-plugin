/*
 * Copyright 2015 dc-square GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hivemq.plugins.metrics.graphite.utils;

import com.hivemq.spi.config.SystemInformation;
import com.hivemq.spi.services.PluginExecutorService;
import com.hivemq.spi.services.configuration.ValueChangedCallback;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.Properties;

/**
 * This reads a property file and provides some utility methods for working with {@link Properties}
 *
 * @author Christoph Schäbel
 */
@Singleton
public class GraphiteConfiguration extends ReloadingPropertiesReader {

    private RestartListener listener;

    @Inject
    public GraphiteConfiguration(final PluginExecutorService pluginExecutorService,
                                 final SystemInformation systemInformation) {
        super(pluginExecutorService, systemInformation);

        final ValueChangedCallback callback = new ValueChangedCallback() {
            @Override
            public void valueChanged(final Object newValue) {
                if (listener != null) {
                    listener.restart();
                }
            }
        };

        addCallback("host", callback);
        addCallback("port", callback);
        addCallback("batchSize", callback);
        addCallback("batchMode", callback);
        addCallback("reportingInterval", callback);
        addCallback("prefix", callback);
    }

    public boolean isBatchMode() {
        return Boolean.parseBoolean(System.getenv("HIVEMQ_METRICS_BATCH_MODE"));
    }

    public String getHost() {
        return System.getenv("HOST");
    }

    public int getPort() {
        return Integer.parseInt(System.getenv("HIVEMQ_METRICS_PORT"));
    }

    public int getBatchSize() {
        return Integer.parseInt(System.getenv("HIVEMQ_METRICS_BATCH_SIZE"));
    }

    public int getReportingInterval() {
        return Integer.parseInt(System.getenv("HIVEMQ_METRICS_REPORTING_INTERVAL"));
    }

    public String getPrefix() {
        return System.getenv("HIVEMQ_METRICS_PREFIX");
    }

    @Override
    public String getFilename() {
        return "graphite-plugin.properties";
    }

    public void setRestartListener(final RestartListener listener) {
        this.listener = listener;
    }

    public static interface RestartListener {

        public void restart();

    }
}
