package ru.sbt.qatools.tests.listeners;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/resources/features/"},glue = {"ru.sbt.qatools.tests.stepdefs"})
public class CucumberTest {

}
