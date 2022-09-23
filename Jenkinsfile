pipeline {
    agent { label 'maven' }
    options {
        disableConcurrentBuilds()
        quietPeriod(1)
    }
    stages {
        stage('Checkout') {
            steps {
                echo 'cache checkout'
                script {
                    env.VERSION = newVersion()
                }
            }
        }
        stage('Build') {
            environment {
                PATH = '/usr/local/bin:/usr/bin:/bin:/home/jenkins/agent'
            }
            steps {
                /*
                conatner(name: 'dind'){
                    sh(label: 'prepare DinD', script: '''
                        docker info
                        cp $(which docker) /home/jenkins/agent/
                    ''')
                }*/
                withCredentials([
                    string(credentialsId: 'snyk.io', variable: 'SNYK_TOKEN'),
                    usernamePassword(credentialsId: 'docker.io',
                        passwordVariable: 'CONTAINER_REGISTRY_PASSWORD',
                        usernameVariable: 'CONTAINER_REGISTRY_USERNAME')]
                    ) {
                        sh (label: 'mvn deploy spring-boot:build-image',
                            script: '''
                                echo DOCKER_HOST=${DOCKER_HOST}
                                export OTEL_TRACES_EXPORTER="otlp" 
                                ./mvnw -V -B deploy -Dmaven.deploy.skip -Ddocker.host=${DOCKER_HOST}
                                #./mvnw -V -B spring-boot:build-image -Dmaven.deploy.skip -Ddocker.host=${DOCKER_HOST} || sleep 3600
                            ''')
                    }
                }
				/*
                withCredentials([string(credentialsId: 'github', variable: 'GITHUB_TOKEN')]) {
                    sh(label: 'security issue', script: 'echo "${GITHUB_TOKEN}" > file.txt')
                }
                sh(label: 'read password', script: 'cat file.txt')
				*/
            }
            post {
                failure {
                    notifyBuild('danger')
                }
            }
        }
        stage('Deploy') {
            steps {
                build(job: 'antifraud/deploy-antifraud',
                          parameters: [
                            string(name: 'PREVIOUS_VERSION', value: previousVersion()),
                            string(name: 'VERSION', value: newVersion())
                      ])
            }
        }
    }
}

def previousVersion() {
    def props = readProperties(file: 'versions.properties')
    return props.CURRENT
}

def newVersion() {
    def props = readProperties(file: 'versions.properties')
    return props.NEW
}

def notifyBuild(status) {
    def blocks =
    [
      [
        "type": "section",
        "text": [
          "type": "mrkdwn",
          "text": "The CI/CD build finished with status `${currentBuild.result}`\n\n<${env.OTEL_ELASTIC_URL}|View traces in OpenTelemetry>"
        ],
      "accessory": [
        "type": "image",
        "image_url": "https://raw.githubusercontent.com/open-telemetry/opentelemetry.io/main/static/img/logos/opentelemetry-logo-nav.png",
        "alt_text": "OpenTelemetry"
      ]
    ]
  ]
  slackSend(channel: "#cicd", blocks: blocks)
}
