import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.subprocessj.OSType
import com.kazurayam.subprocessj.Subprocess
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * This test case script does the following:
 * 
 * 1. make a web service request to https://ghibliapi.herokuapp.com/films to create a new user
 * 2. extract a JSON string from the web response from the URL
 * 3. save the JSON into a local file
 * 4. execute a OS command to run "node" with a script coded in JavaScript, and the path to the JSON file
 * 5. the JavaScript code calls "flat" package to flatten the nested JSON into a list of dot-annotions
 * 6. the test case reads the stdout from the Node process (which is the flattened JSON)
 * 7. the test case prints the flattened JSON into the console 
 */

// GET JSON from a URL
ResponseObject response = WS.sendRequest(findTestObject("Object Repository/GET Films of Studio Ghibli"))
//println(response.getResponseText())

// write Json into a file
Path projectDir = Paths.get(RunConfiguration.getProjectDir())
Path tmpDir = projectDir.resolve("tmp")
Files.createDirectories(tmpDir)
Path jsonFile = Files.createTempFile(tmpDir, "response_", ".json")
jsonFile.toFile() << response.getResponseText()


// execute Node command in a subprocess while specifying a JavaScipt script that fattens the JSON
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

// read the stdout from the subprocess, which is flattened JSON, and print it in the console in pretty-print format
println "[TC3] "
cp.stdout().forEach({
	def slurper = new JsonSlurper()
	def obj = slurper.parseText(it)
	def json = JsonOutput.toJson(obj)
	def pretty = JsonOutput.prettyPrint(json) 
	println pretty
});
