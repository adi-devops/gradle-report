package com.abc.automate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogFileUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogFileUtil.class);

  private static final String LOG_PATH = "build/reports/tests/test/logs/";
  private static final String TEMPLATE = "src/main/resources/template.html";

  private LogFileUtil() {}

  public static synchronized String createLogFile(String fileName, List<ILoggingEvent> logEvents)
      throws IOException {

    File logFile = new File(LOG_PATH + fileName + ".log");
    String fileAbsName = logFile.getAbsolutePath();
    FileUtils.forceMkdirParent(logFile);

    try (PrintWriter printWriter = new PrintWriter(new FileWriter(logFile))) {

      long startTime = logEvents.get(0).getTimeStamp();
      logEvents.forEach(event -> {

        Date date = new Date(event.getTimeStamp());
        printWriter.printf("%s- %ssec [%s] [%s] [%s] %s %n", date,
            TimeUnit.MILLISECONDS.toSeconds(event.getTimeStamp() - startTime),
            event.getThreadName(), event.getLevel(), event.getLoggerName(),
            event.getFormattedMessage());

      });
    }

    return fileAbsName;
  }

  public static void addLogHtml() {

    try {
      String template = new String(Files.readAllBytes(Paths.get(TEMPLATE)));

      StringBuilder tableToAdd = new StringBuilder();
      try (Stream<Path> walk = Files.walk(Paths.get(LOG_PATH))) {
        List<Path> paths = walk.filter(Files::isRegularFile).collect(Collectors.toList());
        for (Path path : paths) {
          String fileName = path.getFileName().toString();
          if (fileName.contains(".log")) {
            tableToAdd.append("<tr><td><a href='" + fileName + "'>" + fileName + "</a></td></tr>");
          }
        }
      }

      String finalString = template.replaceFirst("</thead>", "</thead>" + tableToAdd);
      finalString = finalString.replace("template", "Logs");
      try (PrintWriter out = new PrintWriter(LOG_PATH + "log.html")) {
        out.println(finalString);
      }
    } catch (IOException e) {
      LOGGER.error("could not create screenshot.html", e);
    }
  }
}

