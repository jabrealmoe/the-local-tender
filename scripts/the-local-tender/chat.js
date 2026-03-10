(function() {
    const container = document.getElementById("ai-container");
    if (!container) return;
    
    // We use data-action-url to receive the URL from Velocity.
    // If it's missing, default exactly to the endpoint requested.
    const actionUrl = container.getAttribute("data-action-url") || "http://localhost:11434/api/generate";

    const chatWindow = document.getElementById("chatWindow")
    const textarea = document.getElementById("aiPrompt")
    const sendBtn = document.getElementById("sendBtn")

    function addMessage(text,type){

        const wrapper = document.createElement("div")
        wrapper.className="ai-message "+type

        const bubble=document.createElement("div")
        bubble.className="ai-bubble"
        bubble.innerText=text

        wrapper.appendChild(bubble)
        chatWindow.appendChild(wrapper)

        chatWindow.scrollTop=chatWindow.scrollHeight
    }

    function updateSendButton(){
        sendBtn.disabled = textarea.value.trim().length === 0
    }

    async function sendPrompt(){

        const prompt=textarea.value.trim()
        if(!prompt) return

        addMessage(prompt,"ai-user")

        textarea.value=""
        updateSendButton()

        const loading=document.createElement("div")
        loading.className="ai-message ai-bot"
        loading.innerHTML='<div class="ai-bubble">Thinking...</div>'
        chatWindow.appendChild(loading)

        chatWindow.scrollTop=chatWindow.scrollHeight

        try{

            const res=await fetch(actionUrl,{
                method:"POST",
                headers:{
                    "Content-Type":"application/json"
                },
                body:JSON.stringify({
                    model:"deepseek-r1:7b",
                    prompt:prompt,
                    stream:false
                })
            })

            const data=await res.json()

            loading.remove()

            addMessage(data.response,"ai-bot")

        }catch(e){

            loading.remove()

            addMessage("Error contacting AI service.","ai-bot")

            console.error(e)
        }
    }

    sendBtn.onclick=sendPrompt

    textarea.addEventListener("keydown",function(e){

        if(e.key==="Enter" && !e.shiftKey){
            e.preventDefault()
            sendPrompt()
        }

    })

    textarea.addEventListener("input",function(){

        this.style.height="auto"
        this.style.height=this.scrollHeight+"px"

        updateSendButton()

    })

    updateSendButton()

})();
