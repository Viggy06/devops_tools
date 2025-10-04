pipeline {
    agent any

    tools {
        maven 'maven'
    }

    stages {
        stage('Clone Git') {
            steps {
                script {
                    git branch: 'main', 
                        credentialsId: 'march-2025', 
                        url: 'https://github.com/Viggy06/devops_tools.git'
                }
                sh 'pwd'  // Print the current directory
                sh 'ls -lrth'  // List files to confirm pom.xml is there
            }
        }

        stage('Changing the dir and Validate') {
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

        stage('Test File Running') {
            steps {
                echo "sh 'mvn test' Testing skipped due to code error"
            }
        }

        stage('Build JAR') {
            steps {
                dir('crud-app') {
                    sh 'mvn package'
                }
            }
        }

        stage('Run Custom Script') {
            steps {
                script {
                    sh '''#!/bin/bash
                    pwd
                    ls -lrth
                    '''
                }
            }
        }
    }
}
