# The Local Tender (Jira AI Assistant) Walkthrough

The Jira AI Chatbot integration has been implemented using ScriptRunner for Jira Data Center. This integration provides a floating AI assistant within Jira issues that can answer questions based on the issue's context.

## Components Created

- `scripts/the-local-tender/fragment.groovy`: Handles the rendering logic for the chat UI.
- `scripts/the-local-tender/chat.vm`: The Velocity template defining the chat structure.
- `scripts/the-local-tender/chat.css`: Modern, floating ChatGPT-like styling.
- `scripts/the-local-tender/chat.js`: Client-side logic for message handling and asynchronous API calls.
- `scripts/the-local-tender/restEndpoint.groovy`: Backend REST endpoint that constructs prompts and communicates with the AI API.

## Deployment Instructions

### 1. File Installation

Ensure all files are located in your Jira server's ScriptRunner scripts directory:
`/var/atlassian/application-data/jira/scripts/the-local-tender/` (or your specific scripts path).

### 2. Configure Web Fragment

1. Navigate to **Administration > Add-ons > ScriptRunner > Web Fragments**.
2. Click **Add New Item** and select **Show a web panel**.
3. Configure the following:
   - **Note**: A descriptive note (e.g., "The Local Tender AI Chat Assistant").
   - **Location**: `atl.jira.view.issue.right.context`
   - **Key**: `ai-chat-panel`
   - **Menu text**: (Leave blank)
   - **Weight**: `100`
   - **Condition**: (Optional) e.g., `jiraHelper.project?.key == "PROJ"` to limit to specific projects.
   - **Provider class/script**: Click **Script File** and select `the-local-tender/fragment.groovy`.

### 3. Configure REST Endpoint

1. Navigate to **Administration > Add-ons > ScriptRunner > REST Endpoints**.
2. Click **Add New Item** and select **REST Endpoint**.
3. Select the script file: `the-local-tender/restEndpoint.groovy`.
4. Note: The endpoint will be available at `/rest/scriptrunner/latest/custom/ai-chat`.

### 4. Configuration (Local LLM)

The `restEndpoint.groovy` script is configured to call a local API (default: `http://localhost:11434/api/generate` for Ollama).

- Ensure your local LLM server is running and accessible from the Jira server.
- If using Ollama, ensure the model (e.g., `llama3`) is pulled and ready.
- You can modify the `apiUrl` and `model` name in `restEndpoint.groovy` to match your specific local setup (vLLM, LocalAI, Python Middleware, etc.).

## Usage

Once configured, a comment icon will appear in the bottom-right corner of any Jira issue. Clicking it opens the AI assistant. The assistant automatically receives the current issue's key, summary, and description as context for any questions asked.

### Features

- **Context-Aware**: AI knows which issue you are viewing.
- **Modern UI**: Smooth animations, message bubbles, and typing indicators.
- **Keyboard Shortcuts**: `Enter` to send, `Shift+Enter` for new line.
- **Auto-Scrolling**: Keeps the latest messages in view.
