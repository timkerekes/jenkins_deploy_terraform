pipeline {
    agent any
    environment {
        CONTAINER_REGISTRY_URL = 'https://index.docker.io/v1/'
        IMAGE_NAME = "timkerekes/jenkins-deploy-terraform"
    }
    stages {
        stage('Checkout Code') {
            steps {
                sleep(10)  // Wait for 10 seconds to ensure Jenkins has time to fetch changes
                checkout scm
            }
        }
        // stage('Check Condition') {
        //     when {
        //         allOf {
        //             branch 'main'
        //             changeset "*/Dockerfile"
        //         }
        //     }
            stage('Check Dockerfile Changes') {
                steps {
                    script {
                        def changes = sh(script: "git diff --name-only HEAD~1 | grep Dockerfile || true", returnStdout: true).trim()
                        if (changes) {
                            echo "Dockerfile has changed. Proceeding with the build."
                        } else {
                            echo "No changes to Dockerfile. Skipping build."
                            currentBuild.result = 'SUCCESS'
                            error("Skipping further stages")
                        }
                    }
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
        // }
    }
}