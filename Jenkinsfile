#!/usr/bin/groovy

/**
 * Build pipeline for the remote-engine project.
 *
 * The pod template definition is in the companion file agentPodTemplate.yaml
 *
 */

pipeline {

    options {
        buildDiscarder(logRotator(numToKeepStr: '20', artifactNumToKeepStr: '5'))
        timeout(time: 30, unit: 'MINUTES')
        skipStagesAfterUnstable()
        disableConcurrentBuilds()
    }


    agent {
        kubernetes {
            label "tbd-studio-se"
            yamlFile 'podTemplate.yaml'
            activeDeadlineSeconds 60
        }
    }

    stages {
        stage('Sanity check') {
            steps {
                container('python2') {
                    sh '''
                        pip install javaproperties
                        python ./tools/sanity-check.py
                        '''
                }
            }
        }
        stage('Commit check'){
            steps {
                container('python2') {
                    sh '''
                        python ./tools/commit-check.py
                        '''
                }
            }
        }
    }
}