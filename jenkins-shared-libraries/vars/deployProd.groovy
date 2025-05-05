def call (body) {
 
  def settings = [:]
  body.resolveStrategy = Closure.DELEGATE_FIRST
  body.delegate = settings
  body()
 
  container('alpine') {
    sh '''
      # deploy steps

      apk add openssh git

      mkdir $HOME/.ssh
      cp $JENKINS_SSH_PRIVATE_KEY $HOME/.ssh/id_rsa
      chmod 400 $HOME/.ssh/id_rsa

      ssh-keyscan gitea.localhost.com > $HOME/.ssh/known_hosts

      git config --global user.email "jenkins@andrealbuquerque.me"
      git config --global user.name "Jenkins CI"

      if [! -d helm-applications]; then
        git clone git@gitea.localhost.com:andrealbuquerqueme/helm-applications.git
      fi
        cd helm-applications/${JOB_NAME%/*}

      IMAGE_TAG="$(cat /artifacts/prod.artifact)"

      sed -i -E "s/v[0-9]{1,2}\\.[0-9]{1,3}\\.[0-9]{1,3}/${IMAGE_TAG}/g" values-prod.yaml
      git add values-prod.yaml
      git commit -m "[${JOB_NAME%/*}|prod] - deploy ${IMAGE_TAG}" --allow-empty
      git push
    '''
  }
 
}