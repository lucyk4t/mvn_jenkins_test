#!/usr/bin/env groovy

def call() {
  node() {
    checkout scm
   
    def testType
    stage('input') {
      def userInput = input message: 'Choose test type', parameters: [string(defaultValue: 'test', description: 'test or verify', name: 'testType', trim: false)], submitterParameter: 'store'
      testType = userInput.testType
    }
    
    stage('build') {
        //withEnv(["PATH+MAVEN=${tool 'mvn-3.3.9'}/bin"]) {
        //withEnv(["PATH+MAVEN=${tool 'mvn-3.6.2'}/bin"]) {
        withEnv(["PATH+MAVEN=${tool 'mvn-3.6.0'}/bin", 
                "JAVA_HOME=${tool 'openjdk8'}"]) {
            sh 'echo $JAVA_HOME'
            sh 'javac -version'
            sh 'mvn --version'
            sh "mvn clean ${params.testType} -Dmaven.test.failure.ignore=true"
        }
    }

    stage('report') {
        junit 'target/surefire-reports/*.xml'
        jacoco execPattern: 'target/**.exec'
    }
  }
}
