def dockerBuildTag = 'latest'
def buildVersion = null
def mobileDepositUiImage = null
stage 'build'


node("master") {
    docker.withRegistry('tcp://192.168.1.10:4342') {
        git url: "https://github.com/shaposhnikoff/mobile-deposit-ui"
        sh "git rev-parse HEAD > .git/commit-id"
        def commit_id = readFile('.git/commit-id').trim()
        println commit_id
        stage "build"
        def app = docker.build "your-project-name"
        stage "publish"
        app.push 'master'
        app.push "${commit_id}"
    }
}







