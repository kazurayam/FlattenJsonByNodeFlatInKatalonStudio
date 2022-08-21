import com.kazurayam.subprocessj.Subprocess
import com.kazurayam.subprocessj.OSType

/**
 * Sample code of invokig the node command in command line.
 *  Uses Subprocess class which wraps java.lang.ProcessBuilder class
 */
Subprocess.CompletedProcess cp;

if (OSType.isMac() || OSType.isUnix()) {
	cp = new Subprocess().cwd(new File("."))
			.run(Arrays.asList("/usr/local/bin/node", "Include/scripts/node/sample_flattenJson.js")
			);
} else {
	// Windows
	cp = new Subprocess().cwd(new File("."))
			.run(Arrays.asList("/specify/your/path/to/node", "Include\\scripts\\node\\sample_flattenJson.js")
			);
}
assert 0 == cp.returncode();
assert cp.stdout().size() > 0;
cp.stdout().forEach({ println it });
cp.stderr().forEach({ println it });
assert cp.stdout().toString().contains("valueI")
