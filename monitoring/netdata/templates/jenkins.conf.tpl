jobs:
  - name: jenkins_pipeline
    url: "__JENKINS_URL__"
    username: "__JENKINS_USER__"
    password: "__JENKINS_TOKEN__"
    update_every: 30
    timeout: 15
    collect_jobs: true
    jobs_include:
      - ".*"

