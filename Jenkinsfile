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
                PUBLISH_DOCKER = 'true'
            }
            steps {
                container(name: 'jnlp'){
                    withCredentials([
                        string(credentialsId: 'snyk.io', variable: 'SNYK_TOKEN'),
                        usernamePassword(credentialsId: 'docker.io',
                            passwordVariable: 'CONTAINER_REGISTRY_PASSWORD',
                            usernameVariable: 'CONTAINER_REGISTRY_USERNAME')
                        ]) {
                            sh (label: 'mvn deploy spring-boot:build-image',
                                script: '''
                                    export OTEL_TRACES_EXPORTER="otlp" 
                                    ./mvnw -V -B deploy -Dmaven.deploy.skip
                                ''')
                    }
                }
            }
            post {
                failure {
                    notifyBuild('danger')
                }
            }
        }
        stage('Deploy') {
            environment {
                VAULT_AUTH_METHOD = approle
                VAULT_AUTHTYPE = approle
            }
            steps {
                container(name: 'ansible'){
                     withCredentials([
                        string(credentialsId: 'vault-addr', variable: 'VAULT_ADDR'),
                        string(credentialsId: 'vault-role-id', variable: 'VAULT_ROLE_ID'),
                        string(credentialsId: 'vault-secret-id', variable: 'VAULT_SECRET_ID')
                     ]){
                        sh(label: 'Deploy App', script: 'make -C .ci/otel-ci-demo deploy')
                     }
                }
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
