import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.testobject.ResponseObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import com.kms.katalon.core.configuration.RunConfiguration

/**
 * Sample code of calling a Web Service via Katalon Studio's WS API, and save JSON into a local file
 * 
 * @author kazurayam
 */

// create a new user
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
