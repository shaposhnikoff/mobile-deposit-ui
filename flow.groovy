def dockerBuildTag = 'latest'
def buildVersion = null
stage 'build'
node('docker') {
    docker.withServer('tcp://127.0.0.1:1234'){
            docker.image('kmadel/maven:3.3.3-jdk-8').inside('-v /data:/data') {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/cloudbees/mobile-deposit-ui.git']]])
                sh 'mvn -s /data/mvn/settings.xml -Dmaven.repo.local=/data/mvn/repo clean package'

                stage 'functional-test'
                //sh 'mvn -s /data/mvn/settings.xml -Dmaven.repo.local=/data/mvn/repo verify'
                //step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

                stage 'release'
                def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                if (matcher) {
                    buildVersion = matcher[0][1]
                    echo "Releaed version ${buildVersion}"
                }
                matcher = null
                archive "target/mobile-deposit-ui.jar, target/Dockerfile"
            }
    }
    docker.withServer('tcp://54.173.235.97:2375') {
        unarchive mapping: ['target/mobile-deposit-api.jar': '.', 'target/Dockerfile': '.']
        stage 'build docker image'
        def mobileDepositApiImage = docker.build "mobile-deposit-ui:${dockerBuildTag}"

        stage 'deploy to production'
        try{
            sh "docker stop mobile-deposit-ui"
            sh "docker rm mobile-deposit-ui"
        } catch (Exception _) {
            echo "no container to stop"
        }
        sh "docker run -d --name mobile-deposit-ui -p 80:8080 mobile-deposit-ui:${dockerBuildTag}"
    }

}