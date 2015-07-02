def dockerBuildTag = 'latest'
def buildVersion = null
stage 'build'
node('docker') {
    def mobileDepositUiImage
    docker.withServer('tcp://127.0.0.1:1234'){ //run the following steps on this Docker host
            docker.image('kmadel/maven:3.3.3-jdk-8').inside('-v /data:/data') { //use this image as the build environment
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/cloudbees/mobile-deposit-ui.git']]])
                sh 'mvn -s /data/mvn/settings.xml -Dmaven.repo.local=/data/mvn/repo clean package'

                //get new version of application from pom
                def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                if (matcher) {
                    buildVersion = matcher[0][1]
                    echo "Releaed version ${buildVersion}"
                }
                matcher = null
            }
    }

    //build image and deploy to staging
    docker.withServer('tcp://54.165.201.3:2376', 'slave-docker-us-east-1-tls') { //run following steps on our staging server
        stage 'build docker image'
        dir('target') {
            mobileDepositUiImage = docker.build "mobile-deposit-ui:${buildVersion}"
        }
        try {
            sh "docker stop mobile-deposit-ui-stage"
            sh "docker rm mobile-deposit-ui-stage"
        } catch (Exception _) {
            echo "no container to stop"
        }
        stage 'deploy to staging'
        mobileDepositUiImage.run("--name mobile-deposit-ui-stage -p 82:8080")
    }
    docker.image('kmadel/maven:3.3.3-jdk-8').inside('-v /data:/data') {
        stage 'functional-test'
        sh 'mvn -s /data/mvn/settings.xml -Dmaven.repo.local=/data/mvn/repo verify'
    }

    input 'UI Staged at http://54.165.201.3:82/deposit - Proceed with Production Deployment?'
    docker.withServer('tcp://54.165.201.3:2376', 'slave-docker-us-east-1-tls'){

        stage 'deploy to production'
        try{
            sh "docker stop mobile-deposit-ui"
            sh "docker rm mobile-deposit-ui"
        } catch (Exception _) {
            echo "no container to stop"
        }
        mobileDepositUiImage.run("--name mobile-deposit-ui -p 80:8080")
        sh 'curl http://jenkins.beedemo.net/mobile-team/docker-traceability/submitContainerStatus --data-urlencode inspectData="$(docker inspect mobile-deposit-ui)"'
    }

}