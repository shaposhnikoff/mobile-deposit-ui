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

}


