def dockerBuildTag = 'latest'
def buildVersion = null
def mobileDepositUiImage = null
stage 'build'
node('master') {
            docker.withServer('tcp://192.168.1.10:4243'){
                docker.image('kmadel/maven:3.3.3-jdk-8').inside('-v /data:/data') { 
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/shaposhnikoff/mobile-deposit-ui.git']]])
                sh 'mvn -Dmaven.repo.local=/data/mvn/repo clean package'

                def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                if (matcher) {
                    buildVersion = matcher[0][1]
                    echo "Released version ${buildVersion}"
                }
                matcher = null
            }
    }

    //build image and deploy to staging
    docker.withServer('tcp://192.168.1.4:5555', 'beedemo-swarm-cert') { //run following steps on our staging server
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
        mobileDepositUiImage.run("--name mobile-deposit-ui-stage -p 82:8080 --env='constraint:node==beedemo-swarm-master'")
    }
    docker.image('kmadel/maven:3.3.3-jdk-8').inside('-v /data:/data') {
        stage 'functional-test'
        sh 'mvn -Dmaven.repo.local=/data/mvn/repo verify'
    }
}


