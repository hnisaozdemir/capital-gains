pipeline {

    agent any

    tools {
        gradle 'gradle8.13'
    }

    stages {
        stage('checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], extensions: [], userRemoteConfigs: [[credentialsId: 'jenkins', url: 'https://github.com/thiagorigonatti/capital-gains']])
            }
        }

        stage('clean'){
            steps{
                sh 'gradle clean'
            }
        }

        stage('test'){
            steps{
                sh 'gradle test'
            }
        }

        stage('shadowJar'){
            steps{
                sh 'gradle shadowJar'
            }
        }

        stage('javadoc'){
            steps{
                sh 'gradle javadoc'
            }
        }

        stage('updateJavadoc') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'jenkins', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    sh '''
                        git config user.name "jenkins"
                        git config user.email "jenkins@ci"

                        if [ ! -d ".git" ]; then
                            git clone https://$GIT_USER:$GIT_PASS@github.com/thiagorigonatti/capital-gains.git .
                        fi

                        git checkout main || git checkout -b main
                        git config pull.rebase true
                        git pull origin main

                        rm -rf docs
                        mkdir docs
                        cp -r build/docs/javadoc/* docs/

                        # Copia o favicon da raiz do projeto para a pasta docs
                        if [ -f favicon-32.png ]; then
                            cp favicon-32.png docs/
                        fi

                        # Adiciona o link do favicon em todos os arquivos HTML gerados
                        find docs -name "*.html" -exec sed -i 's|<head>|<head>\\n<link rel="icon" href="https://thiagorigonatti.github.io/capital-gains/favicon-32.png" type="image/png">|' {} +;

                        # Edita a cor da navbar no CSS
                        find docs -name stylesheet.css -exec sed -i 's|--navbar-background-color: #[0-9A-Fa-f]\\{6\\};|--navbar-background-color: #820ad1;|' {} +;

                        git add docs
                        git commit -m "Update javadoc [skip ci]" || echo "No javadoc changes"
                        git push https://$GIT_USER:$GIT_PASS@github.com/thiagorigonatti/capital-gains.git main
                    '''
                }
            }
        }

        stage('archive') {
            steps {
                archiveArtifacts artifacts: '**/CapitalGainsCalculator.jar', followSymlinks: false
            }
        }
    }
}
