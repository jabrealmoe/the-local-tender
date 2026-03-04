import com.onresolve.scriptrunner.runner.rest.common.CustomEndpointDelegate
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.BaseScript
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseDecorator
import jakarta.ws.rs.core.MultivaluedMap
import jakarta.ws.rs.core.Response
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.POST

@BaseScript CustomEndpointDelegate delegate  // ← THIS was missing, causes all 5 errors

aiChat(httpMethod: "POST", groups: ["jira-users"]) { MultivaluedMap queryParams, String body ->
    def jsonSlurper = new JsonSlurper()
    def data = jsonSlurper.parseText(body)

    String userMessage = data?.message?.toString()
    String issueKey    = data?.issueKey?.toString()

    def issueManager = ComponentAccessor.issueManager
    def issue        = issueManager.getIssueObject(issueKey)

    String prompt     = constructPrompt(userMessage, issue)
    String aiResponse = callLLM(prompt)

    return Response.ok(new JsonBuilder([response: aiResponse]).toString()).build()
}

String constructPrompt(String userMessage, Issue issue) {
    if (!issue) return userMessage

    return """
Context: You are The Local Tender, a friendly AI bartender serving milk and cookies while helping users with their Jira issues.
Issue Key: ${issue.key}
Summary: ${issue.summary}
Description: ${issue.description ?: 'No description provided'}
User Message: ${userMessage}
Please provide a helpful response based on the issue context above.
"""
}

String callLLM(String prompt) {
    def apiUrl = "http://localhost:11434/api/generate"

    try {
        def http = new HTTPBuilder(apiUrl)

        def result = http.request(POST, JSON) { req ->
            body = [
                model : "llama3",
                prompt: prompt,
                stream: false
            ]

            response.success = { resp, json ->
                return json?.response?.toString()
            }

            response.failure = { resp ->
                return "Error calling Local AI API: ${resp?.statusLine}"
            }
        }

        return result.toString()

    } catch (Exception e) {
        return "Failed to communicate with Local AI: ${e.message}"
    }
}