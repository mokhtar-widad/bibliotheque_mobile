pipeline {
    agent any

    environment {
        // Chemin SDK Android (à adapter si nécessaire)
        ANDROID_HOME = "C:\\Users\\widad\\AppData\\Local\\Android\\Sdk"
        PATH = "${env.ANDROID_HOME}\\platform-tools;${env.ANDROID_HOME}\\tools;${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/mokhtar-widad/bibliotheque_mobile.git'
            }
        }

        stage('Build APK') {
            steps {
                bat 'gradlew.bat clean assembleDebug'
            }
        }

        stage('Run Tests') {
            steps {
                bat 'gradlew.bat test'
            }
        }

        stage('Archive APK') {
            steps {
                archiveArtifacts artifacts: 'app\\build\\outputs\\apk\\debug\\app-debug.apk', fingerprint: true
            }
        }

        stage('Dockerize APK') {
            steps {
                script {
                    writeFile file: 'Dockerfile', text: """
                    FROM openjdk:11
                    WORKDIR /app
                    COPY app/build/outputs/apk/debug/app-debug.apk /app/app-debug.apk
                    CMD ["echo", "APK prêt dans le conteneur"]
                    """
                    bat 'docker build -t android-apk:latest .'
                }
            }
        }
    }

    post {
        success {
            echo '✅ Build réussi ! APK généré et stocké.'
        }
        failure {
            echo '❌ Le build a échoué.'
        }
    }
}
