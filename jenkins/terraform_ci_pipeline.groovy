pipeline {
    agent {
        docker { image 'timkerekes/jenkins-deploy-terraform:latest' }
    }
    stages {
        stage('Test') {
            steps {
                sh ''
            }
        }
    }
}

post {
    
}