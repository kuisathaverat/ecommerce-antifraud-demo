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
                        setEnvVar('APP_VERSION', sh('mvn help:evaluate -q -DforceStdout -Dexpression=project.version', returnStdout: true).trim())
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
                HOME = "${env.WORKSPACE}"
                VAULT_AUTH_METHOD = "token"
                VAULT_AUTHTYPE = "token"
            }
            steps {
                container(name: 'ansible'){
                    withVaultToken(path: "${env.HOME}", tokenFile: '.vault-token') {
                        sh(label: 'Deploy App', script: 'make -C .ci deploy')
                    }
                }
            }
            post {
                failure {
                    notifyBuild('danger')
                }
                success {
                    notifyBuild('good')
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
    def message = "The CI/CD build finished with status `${currentBuild.result}`\n\n[View traces in OpenTelemetry](${env.OTEL_ELASTIC_URL})"
    slackSend(channel: "#cicd", message: message)
}
