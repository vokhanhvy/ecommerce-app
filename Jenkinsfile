pipeline {
    agent any
     tools {
            maven 'maven 3'
        }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
        stage('Docker Build & Deploy') {
            steps {
                sh '''
                docker build -t ecommerce-app .
                docker run -d -p 8080:8080 ecommerce-app
                '''
            }
        }
    }
}
