def call (body) {
 
  def settings = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = settings
  body()
 
  container('kaniko') {
    sh '''
      REGISTRY="harbor.localhost.com/andrealbuquerqueme"
      REPOSITORY=${JOB_NAME%/*}
      IMAGE_TAG=""
      ENVIRONMENT=""

      if [ $(echo $GIT_BRANCH | grep -E "^develop$") ]; then
        IMAGE_TAG="dev-${GIT_COMMIT:0:10}"
        ENVIRONMENT="dev"
      elif [ $(echo $GIT_BRANCH | grep -E "^hotfix-.*") ]; then
        IMAGE_TAG="${GIT_BRANCH#*-}-${GIT_COMMIT:0:10}"
        ENVIRONMENT="stg"
      fi

      DESTINATION="${REGISTRY}/${REPOSITORY}:${IMAGE_TAG}"

      /kaniko/executor \
        --insecure \
        --destination "${DESTINATION}" \
        --context $(pwd)

      echo "${IMAGE_TAG}" > /artifacts/${ENVIRONMENT}.artifact
    '''
  }
 
}