#!/usr/bin/env groovy

def MAVEN_VERSION="maven-3.6.3"

pipeline {
    environment {
        imagewebgoat = "kleitonpimenta/webgoat-8.0:1.0.0"
        imagewebwolf = "kleitonpimenta/webwolf-8.0:1.0.0"
        registryCredential = 'kleitonpimenta'
        dockerImage = ''
    }
    agent any

    stages {

        stage('Checkout') {
            steps {
                script {
                    git branch: 'main',
                        url: 'https://github.com/kleitonpsld/webgoat.git'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh "ls"
                    sh "pwd"
                    withMaven(maven:MAVEN_VERSION){
                        sh "mvn clean install -DskipTests" 
                    }
                }
                
            }
        }
        stage('Test') {
            steps {
                echo 'Testing...'
            }
        }
        stage('Building image') {
            steps {
                echo 'Building image....'
                script {
                    sh "cd webgoat-server"
                    dockerImage = docker.build imagewebgoat
                    sh "cd ../webwolf/"
                    dockerImage = docker.build imagewebwolf
                }
            }
        }
        stage('Deploy Image') {
          steps{
             script {
                    docker.withRegistry( ":$PASSWORD", registryCredential ) {
                    dockerImage.push(imagewebgoat)
                    dockerImage.push(imagewebwolf)
                    }
                }
            }
        }
        stage('Pull imagem') {
            steps {
                script{
                    echo 'Pull imagem WebGoat'
		            def urlImage = "http://10.138.0.2:2376/images/create?fromImage=kleitonpimenta/webgoat-8.0:1.0.0";
                    def response = httpRequest url:"${urlImage}", httpMode:'POST', acceptType: 'APPLICATION_JSON', validResponseCodes:"200"
                    println("Status: " + response.status)
                    def pretty_json = writeJSON( returnText: true, json: response.content)
                    println pretty_json

                    echo 'Pull imagem WebWolf'
		            def urlImage = "http://10.138.0.2:2376/images/create?fromImage=kleitonpimenta/webwolf-8.0:1.0.0";
                    def response = httpRequest url:"${urlImage}", httpMode:'POST', acceptType: 'APPLICATION_JSON', validResponseCodes:"200"
                    println("Status: " + response.status)
                    def pretty_json = writeJSON( returnText: true, json: response.content)
                    println pretty_json
                    
                }
            }
        }
        stage('Criar container') {
            steps {
                script{
                    configFileProvider([configFile(fileId: '0d7d58cc-3e47-4be9-af81-f99b951f7392', targetLocation: 'container.json')]) {

                        
                    	echo 'Criando container webgoat'
			            def url = "http://10.138.0.2:2376/containers/webgoat?force=true"
                        def response = sh(script: "curl -v -X DELETE $url", returnStdout: true).trim()
                        echo response

                        url = "http://10.138.0.2:2376/containers/create?name=webgoat"
                        response = sh(script: "curl -v -X POST -H 'Content-Type: application/json' -d @container.json -s $url", returnStdout: true).trim()
                        echo response

                    	echo 'Criando container webwolf'
			            def url = "http://10.138.0.2:2376/containers/webwolf?force=true"
                        def response = sh(script: "curl -v -X DELETE $url", returnStdout: true).trim()
                        echo response

                        url = "http://10.138.0.2:2376/containers/create?name=webwolf"
                        response = sh(script: "curl -v -X POST -H 'Content-Type: application/json' -d @container.json -s $url", returnStdout: true).trim()
                        echo response
                    }
                }
            }
        } 
    }
}
