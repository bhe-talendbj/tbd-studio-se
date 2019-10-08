#!/usr/bin/groovy

/**
 * Build pipeline for the remote-engine project.
 *
 * The pod template definition is in the companion file agentPodTemplate.yaml
 *
 */

def podLabel = "tbd-studio-se"

pipeline {

    options {
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '5'))
        timeout(time: 30, unit: 'MINUTES')
        skipStagesAfterUnstable()
        disableConcurrentBuilds()
    }


    agent {
        kubernetes {
            label podLabel
            yamlFile 'podTemplate.yaml'
            activeDeadlineSeconds 60
        }
    }


    environment {
        GIT_USER = 'build-talend-bigdata'
        GIT_EMAIL = 'build-talend-bigdata@talend.com'
        GIT_CREDS = credentials('github-credentials')
    }


    stages {

        stage('Check Last git author is not CI') {
            // inject a var for skipping stages if the last user is the one used for the the 'release build'
            steps {
                script {
                    lastAuthor = sh(returnStdout: true, script: "git log -1 --pretty=format:'%an'").trim()
                    if (lastAuthor in ["${GIT_USER}", "jenkins-git"]) {
                        env.CONTINUE_BUILD = false
                    } else {
                        echo "Last author is ${lastAuthor}"
                        env.CONTINUE_BUILD = true
                    }
                }
            }
        }

        stage('Build Talend Opensource Big Data Studio plugins (tbd-studio-se)') {
            when {
                environment name: 'CONTINUE_BUILD', value: 'true'
            }

            stages {
                stage('git initialisation') {
                    steps {
                        container('maven3') {
                            sh '''
                             set +x
                             echo https://$GIT_CREDS_USR:$GIT_CREDS_PSW@github.com > /tmp/.git-credentials
                             git config credential.helper 'store --file /tmp/.git-credentials'
                             git config user.name $GIT_USER
                             git config user.email $GIT_EMAIL
                            '''

                            script {
                                // set the build date for the docker image label
                                env.BUILD_DATE = sh(script: 'date +%Y-%m-%dT%T%z',
                                        returnStdout: true).trim()
                            }
                        }
                    }
                }

                stage('sanity check before build') {
                    steps {
                        container('maven3') {
                            sh '''
                                pip install javaproperties
                                python ./tools/sanity-check.py
                                '''
                        }
                    }
                }

                stage('maven initialization') {
                    steps {
                        container('maven3') {
                            sh '''
                                mkdir ~/.m2
                                cp continuous-integration/settings.xml ~/.m2/settings.xml
                                '''
                        }
                    }
                }

                stage('main build') {

                    parallel {

                        stage('run local proxy update site') {
                            steps {
                                container('python3') {
                                    withCredentials([usernamePassword(credentialsId: 'nexus-artifacts-zl-credentials', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_LOGIN')]) {
                                        sh 'touch running.lock'
                                        sh 'python3 continuous-integration/proxy-update-site.py running.lock -u $NEXUS_LOGIN -p $NEXUS_PASSWORD'
                                    }
                                }
                            }
                        }

                        stage('run maven') {

                            stages {

                                stage('wait for proxy server') {
                                   steps {
                                       container('maven3') {
                                           sh 'sleep 5'
                                       }
                                   }
                                }

                                stage('package plugins') {
                                    steps {
                                        container('maven3') {
                                            sh 'mvn -f pom-quick-build.xml -Ptbd-studio-se-quick-build clean package'
                                        }
                                    }
                                    post {
                                        success {
                                            archiveArtifacts artifacts: 'tbd-studio-se-eclipse-repository/target/*.zip', onlyIfSuccessful: true
                                        }
                                    }
                                }
                                stage('test plugins') {
                                    steps {
                                        container('maven3') {
                                            sh 'mvn -f pom-quick-build.xml -Ptbd-studio-se-quick-build  -Dmaven.test.failure.ignore=true integration-test'
                                        }
                                    }
                                    post {
                                        always {
                                            junit 'test/plugins/*/target/surefire-reports/TEST-*.xml'
                                        }
                                    }
                                }
                            }
                            post {
                                always {
                                    container('maven3') {
                                        sh 'rm -f running.lock'
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@NonCPS
/**
 * Test if the given branch is a sort of release branch.
 */
static def isReleaseBranch(branch) {
    return isMasterBranch(branch) || isMaintenanceBranch(branch)
}

@NonCPS
static def isMasterBranch(branch) {
    branch == 'master'
}

@NonCPS
static def isMaintenanceBranch(branch) {
    branch.startsWith('maintenance-')
}