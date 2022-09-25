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
            steps {
                container(name: 'ansible'){
                    sh(label: '', script: 'make -C .ci load-gen')
                }
            }
        }
    }
}