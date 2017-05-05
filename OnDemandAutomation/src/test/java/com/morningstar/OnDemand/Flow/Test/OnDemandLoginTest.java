package com.morningstar.OnDemand.Flow.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
//import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterTest;
//import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.morningstar.OnDemand.Page.Test.*;
import com.morningstar.OnDemand.Util.Utils;

public class OnDemandLoginTest {
	public Date startDate = null;
	public Date endDate = null;
	public Utils utils = new Utils();
	private OnDemandPageClass onDemandPage;
	private WebDriver driver;
	String qaLoginURL = "http://ld-wb-qa.morningstar.com";
	String liveLoginURL = "http://edw.morningstar.com";
	String uatLoginURL = "http://ld-wb-uat.morningstar.com";

	HashMap<String, String> mapHashLive = new HashMap<String, String>();
	HashMap<String, String> mapHashUat = new HashMap<String, String>();
	HashMap<String, String> liveMissing = new HashMap<String, String>();
	HashMap<String, String> uatMissing = new HashMap<String, String>();
	HashMap<String, String> valuesIncorrectLive = new HashMap<String, String>();
	HashMap<String, String> valuesIncorrectUat = new HashMap<String, String>();

//  Records = 203  Time = 2 min 13 sec
	String XmlUrl = "/GetUniverseXML.aspx?ClientId=QAStdTop10&CountryId=&LegalStructureId=&FundShareClassId=&ISIN=&Name=Fidelity&RegionId=EUR&OldestShareClass=1&Offshore=&StartDate=&EndDate=&SearchInPrivateList=0&NameSearch=&ActiveStatus=1&InvestorType=0";
	
//	Records = 520  Time = 16 min 18 sec
//	String XmlUrl = "/GetUniverseXML.aspx?ClientId=QAStdTop10&CountryId=SWE&LegalStructureId=&FundShareClassId=&ISIN=&Name=fidelity&RegionId=&OldestShareClass=&Offshore=&StartDate=&EndDate=&SearchInPrivateList=0&NameSearch=&ActiveStatus=1";

//	Records = 6500
//	String XmlUrl = "/GetUniverseXML.aspx?ClientId=QAStdTop10&CountryId=&LegalStructureId=&FundShareClassId=&ISIN=&Name=fidelity";
	
	@BeforeTest
	public void testSetUp() {
		startDate = Calendar.getInstance().getTime();
		System.out.println("Execution startDate :" + startDate);
		// driver = new FirefoxDriver();
		System.setProperty("webdriver.chrome.driver", utils.getClassPath() + "\\driver\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		onDemandPage = new OnDemandPageClass(driver);
	}

	@Test(priority=1)
	public void verifyLive() throws InterruptedException {
		driver.navigate().to(liveLoginURL);
		verifyLoginToApplication("QAStdTop10@morningstar.com", "happyQA");
		Assert.assertEquals(driver.getTitle(), "OnDemand");

		System.out.println("Navigating to API of Live.");
		navigateToXmlURL(liveLoginURL);

		readLiveXmlData();
//		for (Entry<String, Integer> entry : mapHashLive.entrySet()) {
//			System.out.println("UAT values are : Key = " + entry.getKey() + ", Value = " + entry.getValue());
//		}
//		System.out.println(mapHashLive);
		System.out.println("Live XML data reading done...");
		
		driver.navigate().back();
		System.out.println("Browser back...");
		Thread.sleep(5000);
		onDemandPage.clickOnLogOut().click();
		System.out.println("Log Out successful");
	}

	@Test(priority=2)
	public void verifyUAT() throws InterruptedException {
		driver.navigate().to(uatLoginURL);
		verifyLoginToApplication("QAStdTop10@morningstar.com", "happyQA");
		Assert.assertEquals(driver.getTitle(), "OnDemand");

		System.out.println("Navigating to API of UAT.");
		navigateToXmlURL(uatLoginURL);

		readUATXmlData();
//		for (Entry<String, Integer> entry : mapHashUat.entrySet()) {
//			System.out.println("UAT values are : Key = " + entry.getKey() + ", Value = " + entry.getValue());
//		}
//		System.out.println(mapHashUat);
		System.out.println("UAT XML data reading done...");

		driver.navigate().back();
		System.out.println("Browser back...");
		Thread.sleep(10000);
		onDemandPage.clickOnLogOut().click();
		System.out.println("Log Out successful");
	}

	@Test(priority=3)
	public void validateUATwithLIVE() {
		int liveSize = mapHashLive.size();
		int uatSize = mapHashUat.size();
		if (liveSize == uatSize) {
			for (Entry<String, String> entryLive : mapHashLive.entrySet()) {
				if (mapHashUat.containsKey(entryLive.getKey())) {
					if (mapHashUat.get(entryLive.getKey()).equals(entryLive.getValue())) {
						System.out.println("PASS!!! UAT ShareClassID: " + entryLive.getKey()
								+ ", details matched with LIVE ShareClassID: " + entryLive.getKey());
					} else {
						System.out.println("FAIL!!! UAT ShareClassID: " + entryLive.getKey()
								+ ", details not matched with LIVE ShareClassID: " + entryLive.getKey());
						valuesIncorrectLive.put(entryLive.getKey(), entryLive.getValue());
						valuesIncorrectUat.put(entryLive.getKey(), mapHashUat.get(entryLive.getKey()));
					}
				} else {
					uatMissing.put(entryLive.getKey(), entryLive.getValue());
				}
			}
			for (Entry<String, String> entryUat : mapHashUat.entrySet()) {
				if (mapHashLive.containsKey(entryUat.getKey())) {
					if (mapHashLive.get(entryUat.getValue()).equals(entryUat.getValue())) {
						System.out.println("PASS!!! UAT ShareClassID: " + entryUat.getKey()
						+ ", details matched with LIVE ShareClassID: " + entryUat.getKey());
					} else {
						System.out.println("FAIL!!! UAT ShareClassID: " + entryUat.getKey()
						+ ", details not matched with LIVE ShareClassID: " + entryUat.getKey());
						valuesIncorrectLive.put(entryUat.getKey(), mapHashLive.get(entryUat.getKey()));
						valuesIncorrectUat.put(entryUat.getKey(), entryUat.getValue());
					}
				} else {
					liveMissing.put(entryUat.getKey(), entryUat.getValue());
				}
			}
		} else {
			if (uatSize > liveSize) {
				for (Entry<String, String> entryLive : mapHashLive.entrySet()) {
					if (mapHashUat.containsKey(entryLive.getKey())) {
						if (mapHashUat.get(entryLive.getKey()).equals(entryLive.getValue())) {
							System.out.println("PASS!!! UAT ShareClassID: " + entryLive.getKey()
									+ ", details matched with LIVE ShareClassID: " + entryLive.getKey());
						} else {
							System.err.println("FAIL!!! UAT ShareClassID: " + entryLive.getKey()
									+ ", details not matched with LIVE ShareClassID: " + entryLive.getKey());
							valuesIncorrectLive.put(entryLive.getKey(), entryLive.getValue());
							valuesIncorrectUat.put(entryLive.getKey(), mapHashUat.get(entryLive.getKey()));
						}
					} else {
						uatMissing.put(entryLive.getKey(), entryLive.getValue());
					}
				}
				for (Entry<String, String> entryUat : mapHashUat.entrySet()) {
					if (mapHashLive.containsKey(entryUat.getKey())) {
						if (mapHashUat.get(entryUat.getKey()).equals(entryUat.getValue())) {
							System.out.println("PASS!!! UAT ShareClassID: " + entryUat.getKey()
									+ ", details matched with LIVE ShareClassID: " + entryUat.getKey());
						} else {
							System.err.println("FAIL!!! UAT ShareClassID: " + entryUat.getKey()
									+ ", details not matched with LIVE ShareClassID: " + entryUat.getKey());
							valuesIncorrectLive.put(entryUat.getKey(), mapHashLive.get(entryUat.getKey()));
							valuesIncorrectUat.put(entryUat.getKey(), entryUat.getValue());
						}
					} else {
						liveMissing.put(entryUat.getKey(), entryUat.getValue());
					}
				}
			} else {
				for (Entry<String, String> entryUat : mapHashUat.entrySet()) {
					if (mapHashLive.containsKey(entryUat.getKey())) {
						if (mapHashUat.get(entryUat.getKey()).equals(entryUat.getValue())) {
							System.out.println("PASS!!! UAT ShareClassID: " + entryUat.getKey()
									+ ", details matched with LIVE ShareClassID: " + entryUat.getKey());
						} else {
							System.err.println("FAIL!!! UAT ShareClassID: " + entryUat.getKey()
									+ ", details not matched with LIVE ShareClassID: " + entryUat.getKey());
							valuesIncorrectLive.put(entryUat.getKey(), mapHashLive.get(entryUat.getKey()));
							valuesIncorrectUat.put(entryUat.getKey(), entryUat.getValue());
						}
					} else {
						liveMissing.put(entryUat.getKey(), entryUat.getValue());
					}
				}
				for (Entry<String, String> entryLive : mapHashLive.entrySet()) {
					if (mapHashUat.containsKey(entryLive.getKey())) {
						if (mapHashUat.get(entryLive.getKey()).equals(entryLive.getValue())) {
							System.out.println("PASS!!! UAT ShareClassID: " + entryLive.getKey()
									+ ", details matched with LIVE ShareClassID: " + entryLive.getKey());
						} else {
							System.err.println("FAIL!!! UAT ShareClassID: " + entryLive.getKey()
									+ ", details not matched with LIVE ShareClassID: " + entryLive.getKey());
							valuesIncorrectLive.put(entryLive.getKey(), entryLive.getValue());
							valuesIncorrectUat.put(entryLive.getKey(), mapHashUat.get(entryLive.getKey()));
						}
					} else {
						uatMissing.put(entryLive.getKey(), entryLive.getValue());
					}
				}
			}
		}
		String liveDataFile = utils.getClassPath() + "\\ResultOutput\\MissingLiveData_" + System.currentTimeMillis()
				+ ".txt";
		writeToFile(liveDataFile, liveMissing);
		String uatDataFile = utils.getClassPath() + "\\ResultOutput\\MissingUATData_" + System.currentTimeMillis()
				+ ".txt";
		writeToFile(uatDataFile, uatMissing);
		String incorrectLiveDataFile = utils.getClassPath() + "\\ResultOutput\\IncorrectLiveData_" + System.currentTimeMillis()
		+ ".txt";
		writeToFile(incorrectLiveDataFile, valuesIncorrectLive);
		String incorrectUatDataFile = utils.getClassPath() + "\\ResultOutput\\IncorrectUATData_" + System.currentTimeMillis()
		+ ".txt";
		writeToFile(incorrectUatDataFile, valuesIncorrectUat);
	}

	@AfterTest
	public void tearDown() {
		driver.quit();
		endDate = Calendar.getInstance().getTime();
		System.out.println("Excution endDate :" + endDate);
		System.out.println("Total Time: " + getExcutiontimeStr());
	}
	
	public void writeToFile(String dataFile, HashMap<String, String> missingDetails) {
		File file = new File(dataFile);
		try {
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			int count = 0;
			int records = missingDetails.size();
			Iterator<Entry<String, String>> it = missingDetails.entrySet().iterator();
			while (it.hasNext() && count < records) {
				// the key/value pair is stored here in pairs
				Entry<String, String> pairs = it.next();
				out.write("ShareClass ID: "+pairs.getKey()+ " has number of nodes = "+pairs.getValue() + "\n\n ");
				count++;
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void verifyLoginToApplication(String loginId, String loginPwd) {
		onDemandPage.getEmaliTextBox().sendKeys(loginId);
		onDemandPage.getPasswordTextBox().sendKeys(loginPwd);
		onDemandPage.getLoginButton().click();
	}

	public void navigateToXmlURL(String url) throws InterruptedException {
		driver.navigate().to(url + XmlUrl);
		Thread.sleep(10000);
	}

	public void readLiveXmlData() {
		int numberOfShareClasses;
		numberOfShareClasses = onDemandPage.getShareClassNodes().size();
		System.out.println("Number of Share Classes: "+numberOfShareClasses);
		for (int j = 0; j < numberOfShareClasses; j++) {
			String tags = null;
			List<WebElement> stringIDInSharedClassElements = onDemandPage.getIDForShareClass();
			List<WebElement> noOfShareClass = onDemandPage.getShareClassNodes();
			List<WebElement> noOfNodeInEachShareClass = Utils.waitForAllElementsPresent(driver,
					By.cssSelector("div.collapsible-content>div"), noOfShareClass.get(j), "");
			List<String> nodes = parseString(noOfShareClass.get(j).getText(), "<[^>]+>");
			for (String s : nodes) {
				tags += s; 
			}
			tags = tags.replaceAll("null", " ");
			
//			nodes += onDemandPage.getStartNodesInEachShareClass(noOfNodeInEachShareClass.size());
//			nodes += onDemandPage.getEndNodesInEachShareClass(noOfNodeInEachShareClass.size());
//			System.out.println(stringIDInSharedClassElements.get(j).getText());
//			System.out.println(onOfNodeInEachShareClass.size());
//			mapHashLive.put(stringIDInSharedClassElements.get(j).getText(), noOfNodeInEachShareClass.size());
			mapHashLive.put(stringIDInSharedClassElements.get(j).getText(), tags);
//			System.out.println(j+":  Live Hash Map added: " + stringIDInSharedClassElements.get(j).getText());
		}
	}

	public void readUATXmlData() {
		int numberOfShareClasses;
		numberOfShareClasses = onDemandPage.getShareClassNodes().size();
		System.out.println("Number of Share Classes: "+numberOfShareClasses);
		for (int j = 0; j < numberOfShareClasses; j++) {
			String tags = null;
			List<WebElement> stringIDInSharedClassElements = onDemandPage.getIDForShareClass();
			List<WebElement> noOfShareClass = onDemandPage.getShareClassNodes();
			List<WebElement> noOfNodeInEachShareClass = Utils.waitForAllElementsPresent(driver,
					By.cssSelector("div.collapsible-content>div"), noOfShareClass.get(j), "");
			List<String> nodes = parseString(noOfShareClass.get(j).getText(), "<[^>]+>");
			for (String s : nodes) {
				tags += s; 
			}
			tags = tags.replaceAll("null", " ");
//			nodes += onDemandPage.getStartNodesInEachShareClass(noOfNodeInEachShareClass.size());
//			nodes += onDemandPage.getEndNodesInEachShareClass(noOfNodeInEachShareClass.size());
//			System.out.println(stringIDInSharedClassElements.get(j).getText());
//			System.out.println(onOfNodeInEachShareClass.size());
//			mapHashUat.put(stringIDInSharedClassElements.get(j).getText(), noOfNodeInEachShareClass.size());
			mapHashUat.put(stringIDInSharedClassElements.get(j).getText(), tags);
//			System.out.println(j+":  UAT Hash Map added: " +stringIDInSharedClassElements.get(j).getText());
		}
	}
	
	public List<String> parseString(String raw, String regex) {
		List<String> list = new ArrayList<>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(raw);
		while (m.find()) {
			list.add(m.group());
		} 
		return list;
	}
	
	private String getExcutiontimeStr() {
		String result = "";
		long distance = endDate.getTime() - startDate.getTime();
		long min = distance / (1000 * 60);
		if (min > 0) {
			result = min + "  min  ";
		}

		long s = (distance - min * 60 * 1000) / 1000;
		if (s > 0) {
			result += s + "  s";
		}

		if (result.isEmpty()) {
			result = "0 s";
		}

		return result;
	}

}
