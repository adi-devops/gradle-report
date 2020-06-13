package com.abc.automate.appender;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class JunitAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

  private static ConcurrentMap<String, List<ILoggingEvent>> eventMap = new ConcurrentHashMap<>();

  @Override
  protected void append(ILoggingEvent event) {

    List<ILoggingEvent> logEventList = eventMap.get(Thread.currentThread().getName());
    if (logEventList == null) {
      List<ILoggingEvent> newLogEventList = new ArrayList<>();
      newLogEventList.add(event);
      eventMap.put(Thread.currentThread().getName(), newLogEventList);
    } else {
      logEventList.add(event);
    }

  }

  public static List<ILoggingEvent> getEventMap(String treadName) {
    return eventMap.get(treadName);
  }

}
