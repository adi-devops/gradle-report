package com.abc.automate;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenShotUtil {
	
  private static final Logger LOGGER = LoggerFactory.getLogger(ScreenShotUtil.class);

  private static final String SCREENSHOT_PATH = "build/reports/tests/test/screenshot/";
  private static final String TEMPLATE = "src/main/resources/template.html";

  private ScreenShotUtil() {}

  public static synchronized String takeScreenshot(WebDriver driver, String fileName) {

    File screenshot = new File(SCREENSHOT_PATH + fileName + ".png");
    File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    try {
      FileUtils.copyFile(srcFile, screenshot);

    } catch (IOException e) {

      LOGGER.error("could not capture screenshot fileName={}, data:image/png;base64{}", fileName,
          ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64), e);
    }
    return screenshot.getAbsolutePath();

  }

  public static void addScreenShotHtml() {

    try {
      String template = new String(Files.readAllBytes(Paths.get(TEMPLATE)));

      StringBuilder tableToAdd = new StringBuilder();
      try (Stream<Path> walk = Files.walk(Paths.get(SCREENSHOT_PATH))) {
        List<Path> paths = walk.filter(Files::isRegularFile).collect(Collectors.toList());
        for (Path path : paths) {
          String fileName = path.getFileName().toString();
          if (fileName.contains(".png")) {
            tableToAdd.append("<tr><td><a href='" + fileName + "'>" + fileName + "</a></td></tr>");
          }
        }
      }

      String finalString = template.replaceFirst("</thead>", "</thead>" + tableToAdd);
      finalString = finalString.replace("template", "Screenshot");
      try (PrintWriter out = new PrintWriter(SCREENSHOT_PATH + "screenshot.html")) {
        out.println(finalString);
      }
    } catch (IOException e) {
      LOGGER.error("could not create screenshot.html", e);
    }
  }

}
