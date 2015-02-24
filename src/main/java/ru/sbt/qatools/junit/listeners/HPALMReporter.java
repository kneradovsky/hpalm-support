package ru.sbt.qatools.junit.listeners;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import org.apache.commons.lang3.builder.ToStringStyle;

import ru.sbt.qatools.junit.annotations.AlmTest;
import ru.sbt.qatools.junit.annotations.AlmTestStep;


class TestCase {
	Map<String, TestCaseStep> steps;
	String folder,project,parentid,testid;
	String name;
	String status;
	
	public TestCase(AlmTest ann) {
		folder=ann.folder();
		project=ann.project();
		parentid=ann.parentId();
		testid=ann.id();
		name=ann.name();
		steps = new HashMap<>();
	}
	
	public TestCase(String name) {
		this.name = name;
		testid=name;
		steps = new HashMap<>();
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
}

class TestCaseStep {

	String name;
	String status;
	String failure;
	public TestCaseStep(String name) {
		this.name = name;
	}
}

public class HPALMReporter extends RunListener {

	Logger logger = LoggerFactory.getLogger(HPALMReporter.class);
	Map<String,TestCase> tests;
	TestCase currentTest;
	boolean isStepFailed,isCaseFailed;

	public HPALMReporter() {
		logger.debug("HPALM reporter constructed");
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		logger.debug("AssumptionFailure:"
			+ ReflectionToStringBuilder.toString(failure));
		failTest(failure);
		super.testAssumptionFailure(failure);
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		logger.debug("TestFailure:"+failure.getTrace());
		failTest(failure);
		super.testFailure(failure);
	}

	@Override
	public void testFinished(Description description) throws Exception {
		logger.debug("testFinished:"+description.getDisplayName());
		passTest(description);
		super.testFinished(description);
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		logger.debug("testIgnored:"+description.getDisplayName());
		ignoreTest(description);
		super.testIgnored(description);
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		logger.debug("Test run finished");
		logger.debug(ReflectionToStringBuilder.toString(tests.values().toArray(),ToStringStyle.SHORT_PREFIX_STYLE));
		super.testRunFinished(result);
	}

	@Override
	public void testRunStarted(Description description) throws Exception {
		logger.debug("Test run started");
		logger.debug(description.getDisplayName());
		tests = new HashMap<>();
		super.testRunStarted(description);
	}

	@Override
	public void testStarted(Description description) throws Exception {
		logger.debug("TestStart:"+description.getDisplayName());
		Class<?> cls = description.getClass();
		Field uniqId = cls.getDeclaredField("fUniqueId");
		AccessibleObject.setAccessible(new Field[]{uniqId}, true);
		Object objUniqId = uniqId.get(description);
		String className = objUniqId.getClass().getName();
		if (className.equals("gherkin.formatter.model.Scenario")) {
			TestCase tc = new TestCase(((gherkin.formatter.model.Scenario) objUniqId).getName());
			tests.put(tc.testid,tc);
			currentTest = tc;
			isCaseFailed=false;
		} else if (className.equals("gherkin.formatter.model.Step")) {
			if (currentTest != null) {
				isStepFailed=false;
				TestCaseStep ts = new TestCaseStep(((gherkin.formatter.model.Step) objUniqId).getName());
				currentTest.steps.put(ts.name, ts);
			} else {
				logger.debug("Gherkin. Expected not null testcase, but got null");
			}
		} else {
			AlmTest testann = description.getTestClass().getAnnotation(AlmTest.class);
			AlmTestStep teststepann = description.getAnnotation(AlmTestStep.class);
			if(testann==null) {
				logger.debug("Test "+description.getDisplayName()+" is not a member of AlmTest class");
				return;
			}
			if(teststepann==null) {
				logger.debug("Test "+description.getDisplayName()+" does not have AlmTestStep annotation");
				return;
			}
			TestCase tc = tests.get(testann.id());
			if(tc==null) {
				tc=new TestCase(testann);
				tests.put(tc.testid, tc);
			}
			TestCaseStep ts = new TestCaseStep(teststepann.value());
			tc.steps.put(ts.name, ts);
		}
		logger.debug("Start, testClass:" + className);
		super.testStarted(description);
	}
	
	public Object getDescUniqueId(Description d) {
		try {
			Class<?> cls = d.getClass();
			Field uniqId = cls.getDeclaredField("fUniqueId");
			AccessibleObject.setAccessible(new Field[]{uniqId}, true);
			Object objUniqId = uniqId.get(d);
			return objUniqId;
		} catch(NoSuchFieldException | IllegalAccessException e) {
			logger.debug("UniqId, gotException", e);
			return null;
		}
 	}

	protected void passTest(Description desc) {
		finishTest(desc,"passed",null);
	}
	protected void failTest(Failure fail) {
		finishTest(null,"failed",fail);
	}
	protected void ignoreTest(Description desc) {
		finishTest(desc,"ignored",null);
	}
	protected void finishTest(Description desc,String status,Failure fail) {
		Description d = desc!=null ? desc : fail.getDescription(); 
		Object objUniqId = getDescUniqueId(d);
		if(objUniqId==null) objUniqId="String";
		String className = objUniqId.getClass().getName();
		AlmTest testann = d.getTestClass().getAnnotation(AlmTest.class);
		AlmTestStep teststepann = d.getAnnotation(AlmTestStep.class);
		//check if it is the testcase event
		if(testann!=null) 
			currentTest = tests.get(testann.id());
		if(currentTest==null) {
			logger.debug("Testcase "+d.getDisplayName()+" is finished already");
			return;
		}
		if(className.equals("gherkin.formatter.model.Scenario")) {
			if(currentTest.status==null) currentTest.status=status;
			currentTest=null;
			return;
		}
		//it is the teststep event
		String testStepName="";
		if(className.equals("gherkin.formatter.model.Step")) testStepName=((gherkin.formatter.model.Step) objUniqId).getName();
		else if(teststepann!=null) testStepName=teststepann.value();
		if(currentTest.steps.containsKey(testStepName)) {
			TestCaseStep ts = currentTest.steps.get(testStepName);
			if(fail!=null) {
				ts.failure=fail.getMessage()+";"+fail.getTrace();
				currentTest.status=status;
			}
			ts.status=status;
		} else {
			logger.debug("Test "+d.getDisplayName()+" has unknown type");
		}
	}
}
