<template>
  <div class="app-container">
    <div class="sidebar">
      <div class="chat-id-list">
        <div class="chat-record-label">会话记录</div>
        <div 
          v-for="chatId in chatIds" 
          :key="chatId"
          class="chat-id-item"
          :class="{ 'active-chat': activeChat === chatId }"
          @click="selectChat(chatId)"
        >
          {{ chatId }}
        </div>
        <div 
          class="chat-id-item add-chat-button"
          @click="addChat"
        >
          新增会话
        </div>
      </div>
    </div>
    <div class="main-content">
      <div class="chat-container" v-for="chatId in chatIds" :key="chatId" v-show="activeChat === chatId">
        <div class="chat-header">
          <h2>Doro旅行助手 - 会话 {{ chatId }}</h2>
        </div>
        <div class="chat-body">
          <div class="message-list">
            <Message 
              v-for="(message, index) in messages[activeChat]" 
              :key="index"
              :content="message.text"
              :is-me="message.metadata.role === 'USER'"
            />
          </div>
        </div>
        <div class="chat-footer">
          <input 
            type="text" 
            placeholder="请输入消息..." 
            v-model="inputMessage"
            @keyup.enter="sendMessage"
          />
          <button @click="sendMessage">发送</button>
          <div class="file-upload">
            <label for="file-input" class="file-upload-label">
              <img v-if="previewImage" :src="previewImage" alt="Preview" class="preview-image" />
              <span v-else>选择文件</span>
            </label>
            <input
              type="file"
              id="file-input"
              style="display: none;"
              @change="handleFileSelect"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Message from './components/Message.vue';

export default {
  name: 'App',
  data() {
    return {
      activeChat: null,
      chatIds: [],
      messages: {},
      inputMessage: '',
      previewImage: null,
      selectedFiles: [],
      isUploading: false // 新增上传状态管理
    }
  },
  methods: {
    generateChatId() {
      return Math.floor(100000 + Math.random() * 900000)
    },
    addChat() {
      const newChatId = this.generateChatId();
      this.chatIds.push(newChatId);
      this.messages[newChatId] = [];
      this.activeChat = newChatId;
    },
    async fetchChatHistory(chatId) {
      try {
        const response = await fetch(`http://localhost:8080/chat/history/${chatId}`)
        const data = await response.json()
        this.messages[chatId] = data
      } catch (error) {
        console.error('获取聊天记录失败:', error)
      }
    },
    async selectChat(chatId) {
      this.activeChat = chatId
      await this.fetchChatHistory(chatId)
    },
    handleFileSelect(event) {
      const files = Array.from(event.target.files);
      this.selectedFiles = files.filter(file => {
        return file.type.startsWith('image/') ||
               file.type === 'audio/mp3' ||
               file.type === 'audio/mp4' ||
               file.type === 'video/mp4';
      });
      if (this.selectedFiles.length > 0) {
        this.isUploading = true;
        this.previewImage = URL.createObjectURL(this.selectedFiles[0]);
        const label = document.querySelector('.file-upload-label');
        if (label) {
          label.classList.add('uploading');
          label.classList.add('uploaded'); // 新增已上传样式
        }
        setTimeout(() => {
          this.isUploading = false;
          if (label) {
            label.classList.remove('uploading');
          }
        }, 2000);
      } else {
        this.previewImage = null;
      }
    },
    async sendMessage() {
      if (!this.inputMessage.trim() && (!this.selectedFiles || this.selectedFiles.length === 0)) return;

      const userMessage = {
        text: this.inputMessage,
        metadata: { role: 'USER' }
      };
      this.messages[this.activeChat].push(userMessage);

      try {
        const formData = new FormData();
        formData.append('question', this.inputMessage);
        if (this.selectedFiles && this.selectedFiles.length > 0) {
          this.selectedFiles.forEach((file, index) => {
            formData.append(`files`, file);
          });
        }

        const response = await fetch(`http://localhost:8080/chat/${this.activeChat}`, {
          method: 'POST',
          body: formData
        });
        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let assistantMessage = '';

        while (true) {
          const { done, value } = await reader.read();
          if (done) break;
          const chunk = decoder.decode(value, { stream: true });
          assistantMessage += chunk;
          const lastMessageIndex = this.messages[this.activeChat].length - 1;
          if (lastMessageIndex >= 0 && this.messages[this.activeChat][lastMessageIndex].metadata.role === 'ASSISTANT') {
            this.messages[this.activeChat][lastMessageIndex].text = assistantMessage;
          } else {
            this.messages[this.activeChat].push({
              text: assistantMessage,
              metadata: { role: 'ASSISTANT' }
            });
          }
        }
      } catch (error) {
        console.error('发送消息失败:', error);
      }

      this.inputMessage = '';
      this.selectedFiles = [];
    },
  },
  created() {
    const initialChatIds = Array.from({length: 5}, () => this.generateChatId())
    this.chatIds = initialChatIds
    initialChatIds.forEach(id => this.messages[id] = [])
    this.activeChat = initialChatIds[0]
    this.fetchChatHistory(initialChatIds[0])
  },
  components: {
    Message
  },
}
</script>

<style>
.app-container {
  display: flex;
  height: 100vh;
  background-color: #f9fafb;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}
.sidebar {
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.chat-id-list {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}
.main-content {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.chat-container {
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.chat-id-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.chat-id-item {
  padding: 12px;
  border-radius: 4px;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.2s;
}

.chat-id-item:hover {
  background-color: #f0f8ff;
}
.chat-id-item.active-chat {
  background-color: #cce5ff;
  font-weight: bold;
}

.main-content {
  flex: 1;
  padding: 16px;
}

.chat-container {
  width: 100%;
  height: 100%;
  border: 1px solid #ccc;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chat-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
}

.chat-footer {
  padding: 8px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 8px;
}

input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.add-chat-button {
  background-color: #007bff;
  color: white;
}
.chat-record-label {
  padding: 12px;
  text-align: center;
  font-weight: bold;
}
.add-chat-button:hover {
  background-color: #e0f7fa;
}
.preview-image {
  width: 36px;
  height: 36px;
  object-fit: cover;
  border-radius: 50%;
}
.file-upload-label {
  position: relative;
  display: inline-block;
}
.file-upload-label .loading-spinner {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 36px;
  height: 36px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  transform: translate(-50%, -50%);
  display: none; 
}
.file-upload-label.uploading .loading-spinner {
  display: block; 
}
@keyframes spin {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}
</style>