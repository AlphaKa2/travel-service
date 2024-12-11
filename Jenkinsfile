pipeline {
    agent any

    tools {
        jdk 'jdk17'
    }

    environment {
        // 서비스명
        SERVICE_NAME = 'travel-service'

        // 도커 이미지명
        DOCKER_IMAGE = "alphaka/${env.SERVICE_NAME}"

        // 도커 이미지 태그 (젠킨스 빌드 번호를 사용)
        DOCKER_TAG = "${env.BUILD_NUMBER}"

        // 도커 허브 및 깃허브 자격증명
        DOCKERHUB_CREDENTIAL = 'dockerhub-credential-alphaka'
        GITHUB_CREDENTIAL = 'git-credential-hojun'

        // 매니페스트 저장소
        MANIFEST_REPO = 'github.com/AlphaKa2/k8s-manifest.git'
        MANIFEST_REPO_DIR = 'k8s-manifest'

        // kustomize overlay 경로
        OVERLAY_PATH = "overlay/dev/${env.SERVICE_NAME}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare Certificates') {
            steps {
                sh 'cp /var/jenkins_home/elastic-stack-ca.pem ./elastic-stack-ca.pem'
            }
        }

        stage('Build & Test') {
            steps {
                sh './gradlew clean build -Dspring.profiles.active=develop --no-daemon -x test'
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${env.DOCKERHUB_CREDENTIAL}", usernameVariable: 'DOCKERHUB_USER', passwordVariable: 'DOCKERHUB_PASS')]) {
                    sh '''
                        docker login -u $DOCKERHUB_USER -p $DOCKERHUB_PASS
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                        docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker logout
                    '''
                }
            }
        }

        stage('Clean Up Docker Image') {
            steps {
                sh 'docker rmi ${DOCKER_IMAGE}:${DOCKER_TAG}'
            }
        }

        stage('Update Manifest') {
            steps {
                withCredentials([usernamePassword(credentialsId: "${env.GITHUB_CREDENTIAL}", usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                    sh '''
		                # 기존 디렉토리 삭제
				        if [ -d "${MANIFEST_REPO_DIR}" ]; then
				             rm -rf ${MANIFEST_REPO_DIR}
				        fi
                    
                        # 매니페스트 저장소 클론
                        git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@${MANIFEST_REPO} ${MANIFEST_REPO_DIR}
                        cd ${MANIFEST_REPO_DIR}
                        
                        # 원격의 develop 브랜치가 있는지 확인하고 체크아웃
                        git fetch origin develop
                        git checkout develop || git checkout -b develop origin/develop

                        # 이미지 태그 업데이트 (첫 빌드인지 여부를 확인하여 처리)
                        if grep -q '\\${IMAGE_TAG}' ${OVERLAY_PATH}/deployment-patch.yaml; then
                            # 첫 빌드일 경우 ${IMAGE_TAG}를 DOCKER_TAG로 대체
                            sed -i 's/\\${IMAGE_TAG}/'"${DOCKER_TAG}"'/g' ${OVERLAY_PATH}/deployment-patch.yaml
                        else
                            # 이후 빌드일 경우 기존 태그를 새로운 DOCKER_TAG로 대체
                            sed -i 's/alphaka\\/travel-service:[^ ]*/alphaka\\/travel-service:'"${DOCKER_TAG}"'/g' ${OVERLAY_PATH}/deployment-patch.yaml
                        fi

                        # Git 설정
                        git config user.email "ghwnsdla8094@gmail.com"
                        git config user.name "hojun-IM"

                        # 변경 사항 커밋 및 푸시
                        git add ${OVERLAY_PATH}/deployment-patch.yaml
                        git commit -m "Build ${SERVICE_NAME} image with new image tag: ${DOCKER_TAG}"
                        git push origin develop
                    '''
                }
            }
        }
    }
}
