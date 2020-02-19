import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URL
import java.net.URLConnection
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import hudson.model.*;
import hudson.util.*;
import hudson.scm.*;
import hudson.plugins.accurev.*
import java.util.*
  
println "ITEM_BUILD_NUMBER : ${System.getenv("ITEM_BUILD_NUMBER")}"
println "ITEM_JOB_NAME : ${System.getenv("ITEM_JOB_NAME")}"
println "JENKINS_AUTHORIZATION : ${System.getenv("JENKINS_AUTHORIZATION")}"
println "JIRA_AUTHORIZATION : ${System.getenv("JIRA_AUTHORIZATION")}"
  
/**
 * 시작
 **/
reqChangeCommit()

/** 
 * 빌드넘버
 */
def getBuildNumber() {
	def number = System.getenv("ITEM_BUILD_NUMBER")
	return number
}

/**
 * Jenkin Job URL
 **/
def getJenkinsUrl() {
	def url = "%sjob/%s/lastBuild/api/json"
	url = String.format(url, System.getenv("JENKINS_URL"), URLEncoder.encode(System.getenv("ITEM_JOB_NAME"), "UTF-8"))
	println "JENKIN_URL : ${url}" 
	return url
}

/**
 * Jira URL
 **/
def getJriUrl() {
    return "https://${JIRA_URL}/rest/api/2/issue/%s/comment"
}


/**
 * Jenkins Authentication
 * ID:API_KEY
 **/
def getJenkinsAuth() {
	def jenkinsAuth = System.getenv("JENKINS_AUTHORIZATION")
	return "${jenkinsAuth}".bytes.encodeBase64().toString()
}

/**
 * Jira Authentication
 * ID:PASSWORD
 **/
def getJiraAuth() {
	def jiraAuth = System.getenv("JIRA_AUTHORIZATION")
	return "${jiraAuth}".bytes.encodeBase64().toString()
}

/**
 * Jenkin Build 조회
 */
def reqChangeCommit() {
 	println "reqChangeCommit"
	
	try {
		// Jenkins REST API 
		def response = reqApi(getJenkinsAuth(), getJenkinsUrl())
      
		// json으로 변환
		def jsonSlurper = new JsonSlurper()
		def json = jsonSlurper.parseText(response.toString()) 

		// jira ID Map
		def jiraIdMap = new HashMap<String, Boolean>()
    
		// 커밋 정보 파싱
		def items = json.changeSet.items.reverse() // 최신 정보가 0번째 올라오게 소팅 
		for(item in items) {
			
			def author = item.author.fullName // author 정보
			def comments = getComments(item.comment) // 커밋 메세지 정보
			
			if(comments.size() < 1) {
				continue				
			}
          
            		for(comment in comments) {
				
				if(comment.length() < 1) {
					continue
              			}
              
             		   	def jiraId = getJiraId(comment) // 커밋 메세지 정보
				def template = getTemplate(format, author, comment)
				println template
				
				// 댓글작성 API
				reqJiraWriteComment(task, template)
				
			}
		}
  	} catch(Exception e) {
		println e.getMessage()
	}
}

/**
 * 커멘트 가져오기
 */
def getComments(comments) {
	try {
	      	return comments.split("\n")
	} catch(Exception e) {
		println e.getMessage()
	}
	return ""
}

/**
 * 지라 아이디 가져오기 
 */
def getJiraId(msg) {
	try {
		def matches = msg =~ "^\\[.*?\\]"
		return matches[0].replaceAll("\\[","").replaceAll("\\]","")  
	} catch(Exception e) {
		// println e.getMessage()
	}
	return ""
}

/**
 * 템플릿 만들기
 **/
def getTemplate(jiraId, author, msg) {
	try {
		def commit = msg.replaceAll("\\[${jiraId}\\]","")
		def message = "AppCenter #${getBuildNumber()} 반영\n\n"
		message += "작업자 : ${author}\n"
        	message += "작업내용 : ${commit.trim()}"
        	// println message
        	return message
	} catch(Exception e) {
		println e.getMessage()
	}
	return ""
}

/**
 *  지라 댓글 작성 
 */
def reqJiraWriteComment(jiraId, msg) {
  	try {
		if(jiraId == null || jiraId.size() < 1) {
		    return
		}

		// API URL
	        def apiUrl = String.format("${getJriUrl()}", jiraId)
		def jsonData = [
		    body: msg
		]    
		def data = JsonOutput.toJson(jsonData)

		// 통신
		reqApi(getJiraAuth(), apiUrl, data)
	} catch(Exception e) {
		println e.getMessage()
	}
}

/**
 * API 통신
 */
def reqApi(auth, apiUrl, data = "") {
	try {
		// HTTP Request
		def url = new URL(apiUrl)
		def conn = url.openConnection()
		conn.setDoOutput(true)
		conn.setRequestMethod('POST')
		conn.setRequestProperty("Accept", 'application/json')
		conn.setRequestProperty("Content-Type", 'application/json')  	
		conn.setRequestProperty("Authorization", "Basic ${auth}")

		def writer = new OutputStreamWriter(conn.getOutputStream())

		if(data != "") {
		    writer.write(data)
		    writer.flush()
		}

		def reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))
		def response = reader.readLine()

		writer.close()
		reader.close()  
		return response
	} catch(Exception e) {
		println e.getMessage()
	}
	return ""
}
