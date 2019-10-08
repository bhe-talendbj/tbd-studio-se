#!/usr/bin/groovy

/**
 * Build pipeline for the remote-engine project.
 *
 * The pod template definition is in the companion file agentPodTemplate.yaml
 *
 */

def podLabel = "dakar"

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
            yamlFile 'agentPodTemplate.yaml'
            activeDeadlineSeconds 60
        }
    }


    environment {
        GIT_USER = 'build-talend-bigdata'
        GIT_EMAIL = 'build-talend-bigdata@talend.com'
        GIT_CREDS = credentials('github-credentials')
    }


    stages {

        stage('Check Last git author') {
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

        stage('Build Talend Opensource Big Data Studio  (tbd-studio-se)') {
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

                stage('download update-site') {
                    steps {
                        container('python3') {
                            withCredentials([usernamePassword(credentialsId: 'newbuild-credentials', passwordVariable: 'PASSWORD', usernameVariable: 'LOGIN')]) {
                                sh '''
                                    python ./continuous-integration/download-last-p2-repository.py -l %LOGIN% -p %PASSWORD% -e update-site
                                '''
                            }
                        }
                    }
                }

                parallel {

                    stage('run local update site') {
                        steps {
                            container('python3') {
                                sh 'touch running.lock'
                                sh 'cd update-site && python ../continuous-integration/updates-sites-server.py ../running.lock'
                            }
                        }
                    }

                    stage('test & package plugins') {
                        steps {
                            container('maven3') {
                                sh 'mvn -f pom-quick-build.xml -Ptbd-studio-se-quick-build  -Dmaven.test.failure.ignore=true integration-test'
                                sh 'rm -f running.lock'
                            }
                        }
                        post {
                            always {
                                junit 'junit.xml'
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