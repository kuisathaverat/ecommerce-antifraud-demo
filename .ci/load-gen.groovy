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
                OTEL_EXPORTER_OTLP_ENDPOINT = "http://otel-collector-contrib.default.svc.cluster.local:4318"
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