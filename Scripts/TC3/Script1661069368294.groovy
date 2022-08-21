import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import com.kms.katalon.core.configuration.RunConfiguration
import com.kazurayam.subprocessj.Subprocess
import com.kazurayam.subprocessj.OSType

/**
 * This test case script does the following:
 * 
 * 1. make a web service request to https://sample-web-service-aut.herokuapp.com to create a new user
 * 2. extract a JSON string from the web response from the URL
 * 3. save the JSON into a local file
 * 4. execute a OS command to run "node" with a script coded in JavaScript, and the path to the JSON file
 * 5. the JavaScript code calls "flat" package to flatten the nested JSON into a list of dot-annotions
 * 6. the test case reads the stdout from the Node process (which is the flattened JSON)
 * 7. the test case prints the flattened JSON into the console 
 */

int age = 25
String username = "mimi"
String password = "123456789"
String gender = "MALE"
ResponseObject response = WS.sendRequestAndVerify(findTestObject("Object Repository/POST a new user",
			["age": age, "username": username, "password": password, "gender": gender]))
//println(response.getResponseText())

// write Json into a file
Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path tmpDir = projectDir.resolve("tmp")
Files.createDirectories(tmpDir)
Path jsonFile = Files.createTempFile(tmpDir, "response_", ".json")
jsonFile.toFile() << response.getResponseText()


// execute Node command while specifying a JavaScipt script that fattens a JSON
Subprocess.CompletedProcess cp;
if (OSType.isMac() || OSType.isUnix()) {
	cp = new Subprocess().cwd(new File("."))
			.run(Arrays.asList("/usr/local/bin/node", "Include/scripts/node/flattenJsonResponse.js", jsonFile.toString()));
} else {
	// Windows
	cp = new Subprocess().cwd(new File("."))
			.run(Arrays.asList("/specify/your/path/to/node", "Include\\scripts\\node\\flattenJsonResponse.js", jsonFile.toString()));
}
assert 0 == cp.returncode();
assert cp.stdout().size() > 0;
assert cp.stdout().toString().contains("id")

// read the output from the Node process, which is flattened JSON, and print it
println "[TC3] "
cp.stdout().forEach({ println it });
