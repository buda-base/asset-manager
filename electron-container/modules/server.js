// server module

const { spawn } = require('child_process')

var childServer ;
function serverModule() {
    this.start = function() {
        console.info("starting server vroom vroom")

        childServer =  spawn ('java', ['-jar', '/Users/jimk/dev/tmp/spring-boot-gen-asset-manager/target/AssetManager-0.0.1-SNAPSHOT.jar']);

        childServer.stdout.on('data', (data) => {
            console.log(`stdout: ${data}`);
        });

        childServer.stderr.on('data', (data) => {
            console.log(`stderr: ${data}`);
        });

        childServer.on('close', (code, signal) => {
            console.log(` child process exited with code ${code} received ${signal}`);

        });
//    process.start("java -jar /Users/jimk/dev/tmp/spring-boot-gen-asset-manager/target/AssetManager-0.0.1-SNAPSHOT.jar")
    }

    this.stop = function() {
        if (childServer){

            // cleanly terminate tomcat
            childServer.kill("SIGINT")
        }

    }
}


module.exports = serverModule;