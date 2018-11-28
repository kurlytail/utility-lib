pipeline {

    agent none
    
    parameters {
        string(defaultValue: "0.0", description: 'Build version prefix', name: 'BUILD_VERSION_PREFIX')
        string(defaultValue: "", description: 'Build number offset', name: 'BUILDS_OFFSET')
    }

    triggers {
        snapshotDependencies()
    }

    stages {
        stage('Prepare env') {
            agent {
                label 'master'
            }
            
            steps {
                script {
                    loadLibrary()
                    env['MAVEN_VERSION_NUMBER'] = getMavenVersion 'kurlytail/utility-lib/master', params.BUILD_VERSION_PREFIX, params.BUILDS_OFFSET
                	currentBuild.displayName = env['MAVEN_VERSION_NUMBER']
                }
            }
        }
        
        stage ('Build') {
            agent {
                label 'mvn'
            }
            
            steps {
                sh 'rm -rf *'
     
                checkout scm
                withMaven (
                 	maven: "Maven",
                 	options: [
	                	dependenciesFingerprintPublisher(disabled: false),
	                	concordionPublisher(disabled: false),
	                	pipelineGraphPublisher(disabled: false, lifecycleThreshold: "install")
                	]
                ) {
                    sh 'mvn --batch-mode release:update-versions -DautoVersionSubmodules=true -DdevelopmentVersion=$MAVEN_VERSION_NUMBER'
                    sh 'mvn -s settings.xml clean deploy --update-snapshots'
                }
            }
        }
    }
}

