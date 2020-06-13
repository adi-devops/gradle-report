package com.abc.automate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abc.automate.appender.JunitAppender;

import io.github.bonigarcia.wdm.WebDriverManager;

@Execution(ExecutionMode.CONCURRENT)
@TestInstance(Lifecycle.PER_CLASS)
public class GoogleSearchTest {
	
	  private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSearchTest.class);
	
	
	@ParameterizedTest(name = "{0}")
	@CsvFileSource(resources="/test-data.csv",numLinesToSkip=1)
	public void testSearch(String keyword,String title) throws InterruptedException, IOException {
		
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
		
		driver.get("https://www.google.com/");
		LOGGER.info("navigate to google.com");
		
		driver.findElement(By.xpath(".//input[@name='q']")).sendKeys(keyword);
		LOGGER.info("search keyword={}",keyword);
		Thread.sleep(500);
		driver.findElement(By.xpath(".//input[@name='btnK']")).click();
		LOGGER.info("click search button");
		
		String firstTitle=driver.findElement(By.xpath(".//div[@class='g'][1]/div/div/a/h3")).getText();
		LOGGER.info("search link title={}",firstTitle);
		assertEquals(title,firstTitle);
		
		LOGGER.info("added scrrenshot={}",ScreenShotUtil.takeScreenshot(driver, keyword));
		LOGGER.info("created log file",LogFileUtil.createLogFile(keyword, JunitAppender.getEventMap(Thread.currentThread().getName())));
 		driver.quit();
	}
	
	@AfterAll
	public void afterTest() {
		
		ScreenShotUtil.addScreenShotHtml();
		LogFileUtil.addLogHtml();
		
	}

}
