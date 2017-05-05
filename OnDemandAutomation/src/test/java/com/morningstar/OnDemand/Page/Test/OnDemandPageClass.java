package com.morningstar.OnDemand.Page.Test;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.morningstar.OnDemand.Util.Utils;

public class OnDemandPageClass {
	
	public OnDemandPageClass(WebDriver driver) {
		this.driver = driver;
	}
	
	WebDriver driver; 
	public WebElement getEmaliTextBox() {
		return driver.findElement(By.xpath(".//*[@id='loginform']//input[contains(@name, 'email')]"));
	}
	
	public WebElement getPasswordTextBox() {
		return driver.findElement(By.xpath(".//*[@id='loginform']//input[contains(@name, 'password')]"));
	}
	
	public WebElement getLoginButton() {
		return driver.findElement(By.xpath(".//input[@id='submitButton']"));
	}
		
	public List<WebElement> getShareClassNodes() {
		return driver.findElements(By.cssSelector("div.pretty-print>div.collapsible>div.expanded>div.collapsible-content>div.collapsible>div.expanded"));
	}
	
	public List<WebElement> getIDForShareClass() {
		return driver.findElements(By.cssSelector("div.pretty-print>div.collapsible>div.expanded>div.collapsible-content>div.collapsible>div.expanded>div.collapsible-content>div:nth-child(1)>span:nth-child(2)"));
	}
	
	//Currently not used in OnDemandLoginTest.java class
	public List<WebElement> getNodesInEachShareClass() {
		return driver.findElements(By.cssSelector("div.collapsible-content>div"));
	}

	public WebElement clickOnLogOut() {
		return driver.findElement(By.xpath("//a[text()='LOG OUT']"));
	}
	
	public String getStartNodesInEachShareClass(int size) {
		String startNode = null;
		for (int i = 4; i <= size; i++) {
			startNode += driver.findElement(By.cssSelector("div.pretty-print>div.collapsible>div.expanded>div.collapsible-content>div.collapsible>div.expanded>div.collapsible-content>div:nth-child("+i+")>span:nth-child(1)")).getText();
		}
		return startNode;
	}
	
	public String getEndNodesInEachShareClass(int size) {
		String endNode = null;
		for (int i = 4; i <= size; i++) {
			endNode += driver.findElement(By.cssSelector("div.pretty-print>div.collapsible>div.expanded>div.collapsible-content>div.collapsible>div.expanded>div.collapsible-content>div:nth-child("+i+")>span:nth-child(3)")).getText();
		}
		return endNode;
	}
}
