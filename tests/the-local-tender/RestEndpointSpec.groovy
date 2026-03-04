import spock.lang.Specification
import com.atlassian.jira.issue.Issue

class RestEndpointSpec extends Specification {

    def "should construct prompt with proper issue context"() {
        setup:
        def scriptContent = new File("scripts/the-local-tender/restEndpoint.groovy").text
        
        // Remove the static AI endpoint execution parts to avoid execution during parse
        // In a real environment, you'd extract the pure functions to a separate class.
        def safeContent = scriptContent.replaceAll(/aiChat\(.*?\{.*?return Response\.ok.*?\n\}/, "// Endpoint definition removed for testing")
        
        def shell = new GroovyShell()
        def script = shell.parse(safeContent)
        
        // Mocking the Jira Issue
        def mockIssue = Mock(Issue)
        mockIssue.getKey() >> "PROJ-123"
        mockIssue.getSummary() >> "Server is down"
        mockIssue.getDescription() >> "The main server is not responding to ping requests."

        when:
        def result = script.invokeMethod("constructPrompt", ["How do I fix this?", mockIssue] as Object[])

        then:
        result.contains("Issue Key: PROJ-123")
        result.contains("Summary: Server is down")
        result.contains("Description: The main server is not responding to ping requests.")
        result.contains("User Message: How do I fix this?")
    }

    def "should return raw user message if issue context is missing"() {
        setup:
        def scriptContent = new File("scripts/the-local-tender/restEndpoint.groovy").text
        def safeContent = scriptContent.replaceAll(/aiChat\(.*?\{.*?return Response\.ok.*?\n\}/, "// Endpoint definition removed for testing")
        
        def shell = new GroovyShell()
        def script = shell.parse(safeContent)

        when:
        def result = script.invokeMethod("constructPrompt", ["Hello World", null] as Object[])

        then:
        result == "Hello World"
    }
}
