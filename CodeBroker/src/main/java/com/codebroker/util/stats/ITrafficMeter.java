package com.codebroker.util.stats;

import java.util.List;

/**
 * 流量统计
 *
 * @author xl
 */
public interface ITrafficMeter {

    int getMonitoredHours();

    int getSamplingRateMinutes();

    int getTrafficAverage();

    int getTrafficAverage(int previousHours);

    int getMaxTraffic();

    int getMinTraffic();

    List<Integer> getDataPoints();

    List<Integer> getDataPoints(int howManyPoints);

    long getLastUpdateMillis();

    void onTick();
}
