const flatten = require('flat');
const fs = require('fs');

let myArgs = process.argv.slice(2);
let jsonFile = myArgs[0];
//console.log("[flattenJsonResponse.js] reading a json file ", jsonFile);

fs.readFile(jsonFile, 'utf8', (err, data) => {
    if (err) {
        console.log("[flattenJsonResponse.js] Error reading file from disk: ${err}");
    } else {
        // parse JSON string to JSON object
        let dt = JSON.parse(data);
        // flatten it
        let result = flatten(dt);
        // stringify the result and print it into the stdout
        process.stdout.write(JSON.stringify(result));
    }
});