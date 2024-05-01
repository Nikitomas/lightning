package com.reporting.helpers;

import com.control.ControlCenter;
import com.enums.ContextConstant;
import com.enums.JiraStatus;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.ITestResult;

public class JiraHelper {

    public static void updateTestStatus(ITestResult t, JiraStatus status){
        String testExecutionId = ControlCenter.getInstance().getParameter(ContextConstant.JIRA_EXECUTION_ID);
        String testCaseId = getTestCaseKey(t.getName());
        if(testExecutionId != null && testCaseId != null){
            System.out.println("Updating Jira test case " + testCaseId + " with run id " + testExecutionId);
            RestAssured.given()
                .header("Authorization","Bearer MTk5NzA3MTAzMDc5OoPxuKdH2P6HONjcitTHKRee7+CN")
                .contentType(ContentType.JSON)
                .body(status.getValue())
                .post("https://jira.bnc.ca/rest/raven/1.0/testexec/"+testExecutionId+"/execute/"+testCaseId);
        }
    }

    private static String getTestCaseKey(String testName){
        String[] names = testName.split("_DBH_");
        return names.length == 2 ? "DBH-"+names[1] : null;
    }
}
