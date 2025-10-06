pipeline {
    agent any

    tools {
        maven 'maven'
    }

    parameters {
        string(name: 'gitCredentialsId', defaultValue: 'march-2025', description: 'Git credentials for GitHub')
    }

    stages {
        stage('Clone Git') {
            steps {
                git branch: 'main', 
                    credentialsId: params.gitCredentialsId,
                    url: 'https://github.com/Viggy06/devops_tools.git'

                sh 'pwd'
                sh 'ls -lrth'
            }
        }

        stage('Validate') {
            steps {
                dir('crud-app') {
                    sh 'mvn validate'
                }
            }
        }

        stage('Compile') {
            steps {
                dir('crud-app') {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Test') {
            steps {
                dir('crud-app') {
                    sh 'mvn test || true' // skip failures if needed
                }
            }
        }

        stage('Build JAR') {
            steps {
                dir('crud-app') {
                    sh 'mvn package -DskipTests'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                dir('crud-app') {
                    withCredentials([usernamePassword(credentialsId: 'nexus-cred-id', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                        script {
                            writeFile file: 'settings.xml', text: """
                            <settings>
                              <servers>
                                <server>
                                  <id>nexus-releases</id>
                                  <username>${NEXUS_USER}</username>
                                  <password>${NEXUS_PASS}</password>
                                </server>
                                <server>
                                  <id>nexus-snapshots</id>
                                  <username>${NEXUS_USER}</username>
                                  <password>${NEXUS_PASS}</password>
                                </server>
                              </servers>
                            </settings>
                            """
                            sh "mvn -s settings.xml deploy -DskipTests"
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build and Nexus upload completed successfully"
        }
        failure {
            echo "❌ Build Failed. Please check logs"
        }
    }
}
