pipeline {
    agent any

    tools {
        maven 'maven'
    }

    environment {
        MAVEN_SETTINGS = "${WORKSPACE}/.maven-settings.xml"
        GROUP_ID = "com.example"
        ARTIFACT_ID = "LaundryApp"
        VERSION = "1.0-SNAPSHOT" // Change to 1.0 for release
        PACKAGING = "jar"
        NEXUS_SNAPSHOT_URL = "http://nexus:8081/repository/maven-snapshots"
        NEXUS_RELEASE_URL = "http://nexus:8081/repository/maven-releases"
    }

    parameters {
        string(name: 'gitCredentialsId', defaultValue: 'march-2025', description: 'Git credentials for GitHub')
        credentials(name: 'nexusCredentialsId', defaultValue: 'nexus-cred-id', description: 'Credentials for Nexus')
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

        stage('Build & Validate') {
            steps {
                dir('crud-app') {
                    sh 'mvn clean validate compile -DskipTests'
                }
            }
        }

        stage('Package') {
            steps {
                dir('crud-app') {
                    sh "mvn package -DskipTests"
                }
            }
        }

        stage('Check Nexus Connection') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexusCredentialsId', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    sh 'curl -u $NEXUS_USER:$NEXUS_PASS -I $NEXUS_RELEASE_URL || echo "Check if Nexus is running"'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                dir('crud-app') {
                    withCredentials([usernamePassword(credentialsId: 'nexusCredentialsId', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                        script {
                            def isSnapshot = VERSION.endsWith("-SNAPSHOT")
                            def repoUrl = isSnapshot ? env.NEXUS_SNAPSHOT_URL : env.NEXUS_RELEASE_URL
                            def repoId = isSnapshot ? "nexus-snapshots" : "nexus-releases"

                            // Generate temporary settings.xml
                            writeFile file: env.MAVEN_SETTINGS, text: """
                            <settings>
                              <servers>
                                <server>
                                  <id>${repoId}</id>
                                  <username>${NEXUS_USER}</username>
                                  <password>${NEXUS_PASS}</password>
                                </server>
                              </servers>
                            </settings>
                            """

                            // Deploy artifact
                            sh "mvn -s $MAVEN_SETTINGS deploy -DskipTests -DaltDeploymentRepository=${repoId}::default::${repoUrl}"
                        }
                    }
                }
            }
        }

        stage('Download from Nexus') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'nexusCredentialsId', usernameVariable: 'NEXUS_USER', passwordVariable: 'NEXUS_PASS')]) {
                    script {
                        def repoUrl = VERSION.endsWith("-SNAPSHOT") ? env.NEXUS_SNAPSHOT_URL : env.NEXUS_RELEASE_URL
                        def artifactUrl = "${repoUrl}/${GROUP_ID.replace('.', '/')}/${ARTIFACT_ID}/${VERSION}/${ARTIFACT_ID}-${VERSION}.${PACKAGING}"

                        echo "Downloading artifact from: ${artifactUrl}"

                        sh """
                            mkdir -p /tmp/nexus-artifacts
                            curl -u ${NEXUS_USER}:${NEXUS_PASS} -o /tmp/nexus-artifacts/${ARTIFACT_ID}-${VERSION}.${PACKAGING} ${artifactUrl}
                            ls -lh /tmp/nexus-artifacts
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build, Nexus deploy, and download completed successfully"
        }
        failure {
            echo "❌ Build or deployment failed! Check logs."
        }
    }
}
