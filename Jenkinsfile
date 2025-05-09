pipeline {
  agent any

  environment {
    // AWS & Docker
    AWS_REGION = credentials('AWS_REGION')
    AWS_ACCOUNT_ID = '897722676421'
    IMAGE_NAME = 'hipreader-ecr'
    REPO_URL = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${IMAGE_NAME}"

    // RDS
    SPRING_DATASOURCE_URL = credentials('SPRING_DATASOURCE_URL')
    SPRING_DATASOURCE_USERNAME = credentials('SPRING_DATASOURCE_USERNAME')
    SPRING_DATASOURCE_PASSWORD = credentials('SPRING_DATASOURCE_PASSWORD')

    // Redis
    SPRING_REDIS_HOST = credentials('SPRING_REDIS_HOST')
    SPRING_REDIS_PORT = credentials('SPRING_REDIS_PORT')

    // S3
    AWS_ACCESS_KEY_ID = credentials('AWS_ACCESS_KEY_ID')
    AWS_SECRET_ACCESS_KEY = credentials('AWS_SECRET_ACCESS_KEY')
    S3_BUCKET = credentials('S3_BUCKET')

    // RabbitMQ
    RABBITMQ_HOST = credentials('RABBITMQ_HOST')
    RABBITMQ_PORT = credentials('RABBITMQ_PORT')
    RABBITMQ_USERNAME = credentials('RABBITMQ_USERNAME')
    RABBITMQ_PASSWORD = credentials('RABBITMQ_PASSWORD')

    // Elastic Search
    ELASTICSEARCH_URIS = credentials('ELASTICSEARCH_URIS')
    ELASTIC_USERNAME   = credentials('ELASTIC_USERNAME')
    ELASTIC_PASSWORD   = credentials('ELASTIC_PASSWORD')

    // Google SMTP
    MAIL_USERNAME = credentials('MAIL_USERNAME')
    MAIL_PASSWORD = credentials('MAIL_PASSWORD')

    // JWT
    JWT_SECRET_KEY = credentials('JWT_SECRET_KEY')
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Push Docker Image') {
      steps {
        script {
          sh """
          docker build -t ${IMAGE_NAME} .
          docker tag ${IMAGE_NAME}:latest ${REPO_URL}:latest
          aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${REPO_URL}
          docker push ${REPO_URL}:latest
          """
        }
      }
    }

    stage('Register ECS Task Definition') {
      steps {
        script {
          def taskDef = """
          {
            "family": "hipreader-task",
            "networkMode": "awsvpc",
            "requiresCompatibilities": ["FARGATE"],
            "cpu": "512",
            "memory": "1024",
            "executionRoleArn": "arn:aws:iam::${AWS_ACCOUNT_ID}:role/ecsTaskExecutionRole",
            "containerDefinitions": [
              {
                "name": "hipreader-container",
                "image": "${REPO_URL}:latest",
                "portMappings": [
                  {
                    "containerPort": 8080,
                    "protocol": "tcp"
                  }
                ],
                "environment": [
                  { "name": "SPRING_DATASOURCE_URL", "value": "${SPRING_DATASOURCE_URL}" },
                  { "name": "SPRING_DATASOURCE_USERNAME", "value": "${SPRING_DATASOURCE_USERNAME}" },
                  { "name": "SPRING_DATASOURCE_PASSWORD", "value": "${SPRING_DATASOURCE_PASSWORD}" },
                  { "name": "SPRING_REDIS_HOST", "value": "${SPRING_REDIS_HOST}" },
                  { "name": "SPRING_REDIS_PORT", "value": "${SPRING_REDIS_PORT}" },
                  { "name": "AWS_ACCESS_KEY_ID", "value": "${AWS_ACCESS_KEY_ID}" },
                  { "name": "AWS_SECRET_ACCESS_KEY", "value": "${AWS_SECRET_ACCESS_KEY}" },
                  { "name": "S3_BUCKET", "value": "${S3_BUCKET}" },
                  { "name": "AWS_REGION", "value": "${AWS_REGION}" },
                  { "name": "RABBITMQ_HOST", "value": "${RABBITMQ_HOST}" },
                  { "name": "RABBITMQ_PORT", "value": "${RABBITMQ_PORT}" },
                  { "name": "RABBITMQ_USERNAME", "value": "${RABBITMQ_USERNAME}" },
                  { "name": "RABBITMQ_PASSWORD", "value": "${RABBITMQ_PASSWORD}" },
                  { "name": "ELASTICSEARCH_URIS", "value": "${ELASTICSEARCH_URIS}" },
                  { "name": "ELASTIC_USERNAME", "value": "${ELASTIC_USERNAME}" },
                  { "name": "ELASTIC_PASSWORD", "value": "${ELASTIC_PASSWORD}" },
                  { "name": "MAIL_USERNAME", "value": "${MAIL_USERNAME}" },
                  { "name": "MAIL_PASSWORD", "value": "${MAIL_PASSWORD}" },
                  { "name": "JWT_SECRET_KEY", "value": "${JWT_SECRET_KEY} }
                ],
                "essential": true
              }
            ]
          }
          """

          writeFile file: 'task-def.json', text: taskDef
          sh "aws ecs register-task-definition --cli-input-json file://task-def.json"
        }
      }
    }

    stage('Deploy to ECS') {
      steps {
        sh """
        aws ecs update-service \
          --cluster hipreader-cluster \
          --service hipreader-service \
          --task-definition hipreader-task \
          --region ${AWS_REGION} \
          --force-new-deployment
        """
      }
    }
  }
}
