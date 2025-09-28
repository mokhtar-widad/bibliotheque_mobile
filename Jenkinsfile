pipeline {
    agent any

    // tools {
    //     gradle 'Gradle-7'  // SUPPRIMÉ car on utilise gradlew
    // }

    environment {
        ANDROID_HOME = "/opt/android-sdk"   // Chemin SDK Android (à adapter selon ta machine)
        PATH = "$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$PATH"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/mokhtar-widad/bibliotheque_mobile.git'

            }
        }

        stage('Build APK') {
            steps {
                sh './gradlew clean assembleDebug'
            }
        }

        stage('Run Tests') {
            steps {
                sh './gradlew test'
            }
        }

        stage('Archive APK') {
            steps {
                archiveArtifacts artifacts: 'app/build/outputs/apk/debug/app-debug.apk', fingerprint: true
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
                    sh 'docker build -t android-apk:latest .'
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
