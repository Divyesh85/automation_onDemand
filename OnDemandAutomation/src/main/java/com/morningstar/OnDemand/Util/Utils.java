package com.morningstar.OnDemand.Util;

import java.util.HashMap;
import java.util.List;
//import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.google.common.base.Function;

public class Utils {
	String str = "abc";

	public void getXMLData() {
		HashMap<String, String> xmlData = new HashMap<>();	
	}
	
	/**
	 * Get Project directory path
	 * 
	 * @return
	 */
	public String getClassPath() {
		String workingDir = System.getProperty("user.dir");
		return workingDir;
	}
	
	/**
	 * Wait for element present in DOM, may not visible
	 * 
	 * @param driver
	 * @param locator
	 * @param errorMessage
	 * @return
	 */
	public static List<WebElement> waitForAllElementsPresent(WebDriver driver, final By locator, String errorMessage) {
		Function<WebDriver, List<WebElement>> waitFn = new Function<WebDriver, List<WebElement>>() {
			@Override
			public List<WebElement> apply(WebDriver driver) {
				return driver.findElements(locator);
			}
		};
		WebDriverWait wait = createWait(driver);
		if (errorMessage != null && !errorMessage.equals("")) {
			wait.withMessage(errorMessage);
		} else {
			wait.withMessage("Cannot find Elements: " + locator.toString());
		}
		return wait.until(waitFn);
	}


	/////Create Wait with different Parameters
	public static WebDriverWait createWait(WebDriver driver) {
		return new WebDriverWait(driver,60,250);
	}
	
	public static List<WebElement> waitForAllElementsPresent(WebDriver driver, final By locator,
			final WebElement context, String errorMessage) {
		Function<WebDriver, List<WebElement>> waitFn = new Function<WebDriver, List<WebElement>>() {
			@Override
			public List<WebElement> apply(WebDriver driver) {
				try {
					List<WebElement> els = context.findElements(locator);
					if (els.size() > 0) {
						return els;
					}
					return null;

				} catch (Exception e) {
				}
				return null;
			}
		};
		WebDriverWait wait = createWait(driver);
		if (errorMessage != null && !errorMessage.equals("")) {
			wait.withMessage(errorMessage);
		} else {
			wait.withMessage("Cannot find Elements: " + locator.toString());
		}
		return wait.until(waitFn);
	}

	public static List<WebElement> waitForAllElementsPresent(WebDriver driver, final By locator,
			final WebElement context) {
		return waitForAllElementsPresent(driver, locator, context, "");

	}
	
	
}
