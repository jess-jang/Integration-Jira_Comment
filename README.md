## 지라 댓글 작성
- 빌드결과, 빌드번호
- 작업자  
- 작업 내용 및 반영내용  
![jira](https://github.com/jess-jang/Integration-Jira_Comment/blob/master/screenshot_jira.png?raw=true "jira")

## 방법
- Jenkins API > Git Diff List 
- Commit Message의 Jira ID 파싱
- 커밋 메세지
`[JIRA_ID] 커밋내용`
![git](https://github.com/jess-jang/Integration-Jira_Comment/blob/master/screenshot_git.png?raw=true "git")

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

## APIs
### Jenkins
마지막빌드 상태 조회 : `https://{JENKINS}/jenkins/{JOB_NAME}/lastBuild/api/json`
> https://wiki.jenkins.io/display/JENKINS/Remote+access+API

### Jira
댓글 등록 `https://{JIRA}/rest/api/2/issue/{JIRA_ID}/comment`
> https://developer.atlassian.com/server/jira/platform/rest-apis/

