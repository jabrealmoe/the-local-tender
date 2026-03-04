<div align="center">
  <img src="assets/lunar_wizard.png" alt="The AI Bartender" width="400" />
  <h2>"Pull up a stool, what can I help you with today?"</h2>
  <p><i>Your friendly, local neighborhood AI Service Desk Assistant.</i></p>
</div>

---

# The Local Tender: Jira AI Assistant Integration

This project provides a modern, floating AI chat assistant for Jira Data Center, implemented via ScriptRunner.

<div align="center">
  <img src="assets/jira_integration_demo.png" alt="Jira Integration Demo" width="800" />
  <p><i>The UI integration seamlessly floating over your Jira issues.</i></p>
</div>
## Features

- **Contextual Intelligence**: Automatically includes Jira issue summary and description in prompts.
- **Modern UI**: Smooth animations, message bubbles, and typing indicators.
- **Secure**: All AI processing happens locally on your infrastructure.

## Integration Architecture

We have designed the backend to be flexible and secure by focusing on **Local LLM** deployments.

### 1. Direct Local LLM API (Current Implementation)

The default setup calls a local LLM service (e.g., Ollama, vLLM, or LocalAI) running within your firewalled environment.

```mermaid
sequenceDiagram
    participant User as Jira User (UI)
    participant SR as ScriptRunner Service
    participant LLM as Local LLM API (e.g., Ollama)

    User->>SR: POST /rest/scriptrunner/latest/custom/ai-chat
    SR->>SR: Fetch Issue Context
    SR->>LLM: POST /api/generate (with Context)
    LLM-->>SR: AI Response
    SR-->>User: JSON Response
```

### 2. Python Middleware Integration (Maximum Flexibility)

> [!TIP]
> **Recommended for Growth**: This approach allows you to run heavier Local LLMs on dedicated GPU servers while providing a clean API for ScriptRunner to consume.

```mermaid
sequenceDiagram
    participant User as Jira User (UI)
    participant SR as ScriptRunner Service
    participant PY as Python API (FastAPI)
    participant LLM as Local GPU Node

    User->>SR: POST /rest/scriptrunner/latest/custom/ai-chat
    SR->>PY: POST /process-issue (Issue Data)
    PY->>PY: Logic (RAG, Local Vector Store)
    PY->>LLM: Query Local Model
    LLM-->>PY: Result
    PY-->>SR: Final Processed Response
    SR-->>User: Rendered Response
```

### 3. UiPath Automation Integration

Ideal for triggering local automations using AI signals processed entirely on-premise.

```mermaid
sequenceDiagram
    participant User as Jira User (UI)
    participant SR as ScriptRunner Service
    participant Orch as UiPath Orchestrator
    participant Robot as Local UiPath Robot

    User->>SR: POST /rest/scriptrunner/latest/custom/ai-chat
    SR->>Orch: Start Job
    Orch->>Robot: Execute Local Workflow
    Robot-->>SR: Result
    SR-->>User: UI Update
```

---

## Technical Recommendation: High-Performance Local AI

For the best experience without exposing data, we recommend **Integration Path 2 (Python Middleware)** pointing to a dedicated local GPU instance running **vLLM** or **Ollama**.

- **Privacy**: Zero data leaves your network.
- **Performance**: High-speed inference using dedicated hardware.
- **Flexibility**: Easily swap Llama3, Mistral, or private fine-tuned models.

---

## Installation

Refer to the [walkthrough.md](file:///Users/jabrealj/.gemini/antigravity/brain/22d5c2b7-69a5-44bb-845f-4401a816d211/walkthrough.md) for detailed deployment steps.
