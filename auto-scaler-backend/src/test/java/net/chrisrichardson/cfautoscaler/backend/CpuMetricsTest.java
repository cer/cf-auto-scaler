package net.chrisrichardson.cfautoscaler.backend;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.junit.Test;


public class CpuMetricsTest {

  @Test
  public void tryJmx() {
    ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    long[] allThreadIds = threadMXBean.getAllThreadIds();
    System.out.println("Total JVM Thread count: " + allThreadIds.length);
    long nano = 0;
    for (long id : allThreadIds) {
        nano += threadMXBean.getThreadCpuTime(id);
    }
    
    System.out.println("ms=" + (nano / (1000 * 1000) )); 
  }
}
