package ru.sbt.qatools.tests.stepdefs;

import cucumber.api.java.ru.*;
import org.junit.Assert;

public class CukeStepDefs {
	
	@Дано("^предусловие$")
	public void prereq1() throws Throwable {
		return;
	}
	
	@Когда("^действие$")
	public void action() throws Throwable {
		Assert.assertTrue(true);
	}
	
	@Тогда("^проверка(\\d+)$")
	public void check(String checkId) {
		switch(checkId) {
		case "1" : Assert.assertTrue(false);break;
		case "2" : Assert.assertTrue(true);break;
		}
	}
	
	
}
