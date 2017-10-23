package com.codebroker.util.stats;

import java.util.List;

/**
 * 流量统计
 *
 * @author xl
 */
public interface ITrafficMeter {

    public int getMonitoredHours();

    public int getSamplingRateMinutes();

    public int getTrafficAverage();

    public int getTrafficAverage(int previousHours);

    public int getMaxTraffic();

    public int getMinTraffic();

    public List<Integer> getDataPoints();

    public List<Integer> getDataPoints(int howManyPoints);

    public long getLastUpdateMillis();

    public void onTick();
}
