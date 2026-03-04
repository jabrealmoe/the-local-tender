import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.velocity.VelocityManager
import org.apache.velocity.app.VelocityEngine
import com.atlassian.jira.issue.Issue

// Get the current issue from context
def issue = context.issue as Issue

if (!issue) {
    return "Issue context not found"
}

// Prepare the model for Velocity
def params = [
    "issue": issue,
    "user": ComponentAccessor.jiraAuthenticationContext.loggedInUser
]

// Render the Velocity template
def velocityManager = ComponentAccessor.getComponent(VelocityManager)
def templatePath = "scripts/the-local-tender/chat.vm"

try {
    // Note: In ScriptRunner Data Center, the path is relative to the scripts root
    def writer = new StringWriter()
    velocityManager.getEncodedBody(templatePath, "", "UTF-8", params)
} catch (Exception e) {
    return "Error rendering template: ${e.message}"
}
