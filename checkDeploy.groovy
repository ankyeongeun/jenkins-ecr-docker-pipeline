def checkDeploy(applicationName, environmentName, timeoutMin, region) {
    def checkScript = "aws elasticbeanstalk describe-environments --region ${region} " +
            "    --application-name ${applicationName} --environment-name ${environmentName} |" +
            "    jq -r '.Environments | .[]?' | jq -r '.Status'"
    def sleepSeconds = 5
    def maxIterCnt = (timeoutMin * 60) / sleepSeconds
    def currentIterCnt = 0

    def status = sh(script: checkScript, returnStdout: true).trim()

    while (status != "Ready" && currentIterCnt < maxIterCnt) {
        status = sh(script: checkScript, returnStdout: true).trim()
        if (status != "Ready" && status != "Updating") {
            println "************** EB ERROR **************"
            println "${status}"
            println "************** EB ERROR **************"
            return false
        }
        currentIterCnt++
        println "Waiting for a maximum of ${currentIterCnt}/${maxIterCnt} count for ${environmentName} to become ready (status:${status})"

        sleep(sleepSeconds)
    }

    if (status == "Ready") {
        return true
    }
    return false
}
