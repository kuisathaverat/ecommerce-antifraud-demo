pipeline {
    agent { label 'maven' }
    options {
        disableConcurrentBuilds()
        quietPeriod(1)
    }
    triggers {
     cron '*/5 * * * *'
   }
    stages {
        stage('Load gen') {
            environment {
                OTEL_SERVICE_NAME = "load-generator"
            }
            steps {
                container(name: 'ansible'){
                    echo "${OTEL_EXPORTER_OTLP_ENDPOINT}"
                    sh(label: '', script: 'make -C .ci test_healthcheck')
                }
            }
        }
    }
}