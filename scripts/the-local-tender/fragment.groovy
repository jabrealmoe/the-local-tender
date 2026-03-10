import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.velocity.VelocityManager
import com.atlassian.jira.issue.Issue

def currentIssue = (binding.hasVariable("issue") ? binding.getVariable("issue") : null) as Issue

if (!currentIssue) {
    return "Issue context not found in bindings"
}

def scriptRoot = "/var/atlassian/application-data/jira/scripts/the-local-tender"
def cssHtml = new File("${scriptRoot}/chat.css").text
def jsHtml = new File("${scriptRoot}/chat.js").text

// Explicitly declare as Map<String, Object> to satisfy static type checker
Map<String, Object> params = [
    "issue": currentIssue,
    "user" : ComponentAccessor.jiraAuthenticationContext.loggedInUser,
    "actionUrl": "http://localhost:11434/api/generate",
    "cssContent": cssHtml,
    "jsContent": jsHtml
]

def velocityManager = ComponentAccessor.getComponent(VelocityManager)
def templatePath = "the-local-tender/chat.vm"

try {
    return velocityManager.getEncodedBody(templatePath, "", "UTF-8", params)
} catch (Exception e) {
    return "Error rendering template: ${e.message}"
}