(function () {
  const launcher = document.getElementById("ai-chat-launcher");
  const container = document.getElementById("ai-chat-container");
  const closeBtn = document.getElementById("ai-chat-close");
  const input = document.getElementById("ai-chat-input");
  const sendBtn = document.getElementById("ai-chat-send");
  const messages = document.getElementById("ai-chat-messages");
  const typingIndicator = document.getElementById("ai-chat-typing");

  const issueKey = container.getAttribute("data-issue-key");
  const userName = container.getAttribute("data-user-name");

  launcher.addEventListener("click", () => {
    container.style.display = "flex";
    launcher.style.display = "none";
    input.focus();
  });

  closeBtn.addEventListener("click", () => {
    container.style.display = "none";
    launcher.style.display = "flex";
  });

  input.addEventListener("keydown", (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  });

  sendBtn.addEventListener("click", sendMessage);

  function sendMessage() {
    const text = input.value.trim();
    if (!text) return;

    appendMessage("user", text);
    input.value = "";
    input.style.height = "auto";

    showTyping(true);

    fetch("/rest/scriptrunner/latest/custom/ai-chat", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Atlassian-Token": "no-check",
      },
      body: JSON.stringify({
        message: text,
        issueKey: issueKey,
        user: userName,
      }),
    })
      .then((response) => {
        if (!response.ok) throw new Error("Network response was not ok");
        return response.json();
      })
      .then((data) => {
        showTyping(false);
        appendMessage("ai", data.response);
      })
      .catch((error) => {
        showTyping(false);
        appendMessage(
          "ai",
          "Sorry, I encountered an error. Please try again later.",
        );
        console.error("Error:", error);
      });
  }

  function appendMessage(sender, text) {
    const msgDiv = document.createElement("div");
    msgDiv.className = `message ${sender}`;

    const bubble = document.createElement("div");
    bubble.className = "bubble";
    bubble.textContent = text;

    msgDiv.appendChild(bubble);
    messages.appendChild(msgDiv);

    scrollToBottom();
  }

  function showTyping(show) {
    typingIndicator.style.display = show ? "flex" : "none";
    scrollToBottom();
  }

  function scrollToBottom() {
    messages.scrollTop = messages.scrollHeight;
  }

  // Auto-resize textarea
  input.addEventListener("input", function () {
    this.style.height = "auto";
    this.style.height = this.scrollHeight + "px";
  });
})();
