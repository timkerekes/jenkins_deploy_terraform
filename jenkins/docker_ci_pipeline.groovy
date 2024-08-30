pipeline {
    agent any
    environment {
        CONTAINER_REGISTRY_URL = 'https://index.docker.io/v1/'
        IMAGE_NAME = "timkerekes/jenkins-deploy-terraform"
    }
    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }
        stage('Check Condition') {
            when {
                allOf {
                    branch 'main'
                    changeset "Dockerfile"
                }
            }
            stages {
                stage('Build Docker Image') {
                    steps {
                        script {
                            dockerImage = docker.Build("${env.IMAGE_NAME}:${env.BUILD_ID}")
                        }
                    }
                }
                stage('Push Docker Image') {
                    steps {
                        script {
                            docker.withRegistry("${env.CONTAINER_REGISTRY_URL}", 'dockerhub-timkerekes')
                            dockerImage.push()
                        }
                    }
                }       
            }
        }
    }
}