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
                            /* TODO enable again
                            sh (label: 'mvn deploy spring-boot:build-image',
                                script: '''
                                    export OTEL_TRACES_EXPORTER="otlp" 
                                    ./mvnw -V -B deploy -Dmaven.deploy.skip
                                ''')
                            */
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
                        sh(script: """
                            VAULT_TOKEN=\$(cat ${env.HOME}/.vault-token)
                            python -c "print(' '.join('${env.VAULT_ADDR}'))"
                            python -c "print(' '.join('${env.VAULT_ROLE_ID}'))"
                            python -c "print(' '.join('${env.VAULT_SECRET_ID}'))"
                            python -c "print(' '.join('\${VAULT_TOKEN}'))"
                        """)
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
