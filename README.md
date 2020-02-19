## 젠킨스 빌드 후 지라 댓글 등록
- Jenkins Build
- Jenkins API를 이용하여 Git Change Log 추출
- Commit Message에 있는 Jira ID을 추출하여 댓글 등록 

## Jenkins Configuration
Build > Execute Groovy Script

## Request
~~~
curl -X POST https://$JENKINS_URL/jenkins/job/$JOB_NAME/buildWithParameters \
	--user $ID:API_KETY \
	--data-urlencode "token=jenkins" \
    --data-urlencode "ITEM_BUILD_NUMBER=$BUILD_NUMBER" \
    --data-urlencode "ITEM_BUILD_NUMBER=$BUILD_NUMBER" \
    --data-urlencode "JENKINS_AUTHORIZATION=$JENKINS_AUTHORIZATION" \
    --data-urlencode "JIRA_AUTHORIZATION=$JIRA_AUTHORIZATION"
~~~

## API

### Jenkins
마지막빌드 상태 조회 : `https://{JENKINS}/jenkins/{JOB_NAME}/lastBuild/api/json`
> https://wiki.jenkins.io/display/JENKINS/Remote+access+API

### Jira
댓글 등록 `https://{JIRA}/rest/api/2/issue/{JIRA_ID}/comment`
> https://developer.atlassian.com/server/jira/platform/rest-apis/

## GIt Commit Message
`[TASK_ID] 커밋내용`

[![git](https://github.com/jess-jang/Integration-Jira_Comment/blob/master/screenshot_git.png?raw=true "git")]
(https://github.com/jess-jang/Integration-Jira_Comment/blob/master/screenshot_git.png?raw=true "git")

## Jira
[![jira](https://github.com/jess-jang/Integration-Jira_Comment/blob/master/screenshot_jira.png?raw=true "jira")](https://github.com/jess-jang/Integration-Jira_Comment/blob/master/screenshot_jira.png?raw=true "jira")
