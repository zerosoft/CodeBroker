// package com.codebroker.util;
//
// import java.lang.management.ManagementFactory;
// import com.sun.management.OperatingSystemMXBean;
//
// public class PerformanceMonitor {
//
// /** The last system time. */
// private long lastSystemTime = 0L;
//
// /** The last process cpu time. */
// private long lastProcessCpuTime = 0L;
//
// /** The os mx bean. */
// OperatingSystemMXBean osMxBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
//
// /**
// * Gets the cpu usage.
// *
// * @return the cpu usage
// */
// public synchronized double getCpuUsage() {
// if (this.lastSystemTime == 0L) {
// baselineCounters();
// return 0.0D;
// }
// long systemTime = System.nanoTime();
// long processCpuTime = this.osMxBean.getProcessCpuTime();
//
// double cpuUsage = (processCpuTime - this.lastProcessCpuTime) / (systemTime - this.lastSystemTime);
//
// this.lastSystemTime = systemTime;
// this.lastProcessCpuTime = processCpuTime;
//
// return cpuUsage / this.osMxBean.getAvailableProcessors();
// }
//
// /**
// * Baseline counters.
// */
// private void baselineCounters() {
// this.lastSystemTime = System.nanoTime();
// this.lastProcessCpuTime = this.osMxBean.getProcessCpuTime();
// }
// }
