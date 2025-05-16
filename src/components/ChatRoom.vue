<template>
  <div class ="container">
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
            <div class="message-list" ref="messageList">
              <div 
                class="load-more-prompt"
                v-show="messages[activeChat]?.length"
                @click="loadMoreMessages(activeChat)"
              >
                点击加载历史记录
              </div>
              <Message 
                v-for="(message, index) in messages[activeChat]" 
                :key="index"
                :content="message.text || ''"
                :is-me="message.messageType === 'USER'"
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
  </div>
</template>

<script>
import Message from './Message.vue'

 export default {
  name: 'ChatRoom',
  components: {
    Message
  },
  data() {
    return {
      activeChat: null,
      chatIds: [],
      messages: {},
      inputMessage: '',
      previewImage: null,
      selectedFiles: [],
      isUploading: false, // 新增上传状态管理
    }
  },
  methods: {
    async addChat() {
      console.log('新增会话')
      console.log("token:",localStorage.getItem('token'));
      if (!localStorage.getItem('token')) {
        alert('请先登录');
        return;
      }
      const response = await fetch(`http://localhost:8080/session/create`,{
        method: 'POST',
        headers: {
          'token': `${localStorage.getItem('token')}`
        }
      });
      // 确保正确解析JSON数据
      let responseData;
      try {
        responseData = await response.json();
      } catch (error) {
        console.error('返回数据不是有效的JSON:', error);
        throw new Error('服务器返回的数据格式不正确，请稍后再试');
      }
      const id = responseData.data;
      this.chatIds.push(id);
      this.messages[id] = [];
      this.activeChat = id;
    },
    //获取历史聊天记录
    async fetchChatHistory(chatId, createAt) {
      try {
        let url = `http://localhost:8080/chat/history/${chatId}`
        if (createAt) {
          url += `?createAt=${encodeURIComponent(createAt)}`
        }
        const response = await fetch(url, {
          headers: {
            'token': `${localStorage.getItem('token')}`
          }
        })
        const data = await response.json()
        // 优化版（推荐）
        const newMessages = data.data.map(msg => ({
          ...msg,
          createAt: msg.createdAt ?? new Date().toISOString() 
        }));
        if (createAt) {
          this.messages[chatId] = newMessages.concat(this.messages[chatId])
        } else {
          this.messages[chatId] = newMessages
        }
      } catch (error) {
        console.error('获取聊天记录失败:', error)
      }
    },

    async loadMoreMessages(chatId) {
      if (!this.messages[chatId] || this.messages[chatId].length === 0) return
      const firstMessage = this.messages[chatId][0]
      console.log("first:",firstMessage);
      console.log('加载更多消息:', firstMessage.createAt)
      await this.fetchChatHistory(chatId, firstMessage.createAt)
    },

    handleScroll(event) {
      const container = event.target;
      const scrollTop = container.scrollTop;
      const scrollHeight = container.scrollHeight;
      const clientHeight = container.clientHeight;
      
      // 记录旧的滚动位置和高度
      const oldScrollHeight = scrollHeight;
      const oldScrollTop = scrollTop;

      // 滚动到顶部时加载更多（保留200px缓冲区域）
      if (scrollTop < 200 && this.messages[this.activeChat]?.length) {
        this.loadMoreMessages(this.activeChat).then(() => {
          // 保持滚动位置稳定
          this.$nextTick(() => {
            container.scrollTop = container.scrollHeight - oldScrollHeight + oldScrollTop;
          });
        });
      }
    },

    mounted() {
      this.$nextTick(() => {
        const container = this.$refs.messageList;
        if (container) {
          container.addEventListener('scroll', this.handleScroll);
          // 初始立即滚动到底部
          container.scrollTop = container.scrollHeight;
        }
      });
    },

    beforeDestroy() {
      const messageList = this.$refs.messageList;
      if (messageList) {
        messageList.removeEventListener('scroll', this.handleScroll);
      }
    },
    selectChat(chatId) {
      this.activeChat = chatId;
      this.fetchChatHistory(chatId);
    },

  async getSessionIds() {
  console.log('获取会话ID');
  try {
    const response = await fetch(`http://localhost:8080/session/ids`, {
      method: 'GET',
      headers: {
        'token': `${localStorage.getItem('token')}` // 确保token存在
      }
    });

    // 1. HTTP状态码验证
    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`HTTP错误: ${response.status} - ${errorText}`);
    }

    // 2. 解析响应体
    const responseData = await response.json();
    
    // 3. 业务状态码验证
    if (responseData.code !== 200) {
      throw new Error(`业务错误: ${responseData.msg} (代码: ${responseData.code})`);
    }

    // 4. 提取data字段
    const sessionIds = responseData.data || [];
    
    // 5. 数据格式验证
    if (!Array.isArray(sessionIds)) {
      throw new Error('数据格式错误：期望数组格式');
    }

    // 6. 更新响应式数据
    this.chatIds = sessionIds;

    // 7. 初始化messages结构（确保响应式）
    sessionIds.forEach(id => {
      if (!this.messages[id]) {
        this.messages[id] = [];
      }
    });

    // 8. 自动选择第一个会话（可选）
    if (sessionIds.length > 0 && !this.activeChat) {
      this.activeChat = sessionIds[0];
      this.fetchChatHistory(sessionIds[0]);
    }

  } catch(error) {
    console.error('获取会话ID失败:', error);
    // 这里可以添加用户提示，如：
    // this.$notify.error({ title: '错误', message: error.message });
  }
},
      handleFileSelect(event) {
        const files = event.target.files;
        if (files.length > 0) {
          this.previewImage = URL.createObjectURL(files[0]);
          this.selectedFiles = Array.from(files);
        }
      },
      async sendMessage() {
        const formData = new FormData();
        formData.append('question', this.inputMessage);
        if (this.selectedFiles.length > 0) {
          this.selectedFiles.forEach(file => {
            formData.append('files', file);
          });
        }

        const userMessage = { text: this.inputMessage, messageType: 'USER' };
        this.messages[this.activeChat].push(userMessage);
        this.inputMessage = '';

        const response = await fetch(`http://localhost:8080/chat/${this.activeChat}`, {
          method: 'POST',
          headers: {
            'token': `${localStorage.getItem('token')}`
          },
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

          const messages = this.messages[this.activeChat];
          const lastMessage = messages[messages.length - 1];
          if (lastMessage && lastMessage.messageType === 'ASSISTANT') {
            lastMessage.text = assistantMessage;
          } else {
            this.messages[this.activeChat].push({ text: assistantMessage, messageType: 'ASSISTANT' });
          }
        }
      }
},
  created() {
      this.getSessionIds();
  },
}
</script>

<style>

.container {
  display: flex;
  margin: 0; /* 去除默认的body边距 */
}

.app-container {
  display: flex;
  flex: auto;
  max-width: 100%;
  height: 87vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}
.sidebar {
  background-color: #ffffff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 280px;
  padding: 20px;
}
.chat-id-list {
  background-color: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
}
.chat-record-label {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}
.chat-id-item {
  padding: 14px;
  border-radius: 8px;
  text-align: left;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
}
.chat-id-item:hover {
  background-color: #e7f3ff;
  transform: translateX(4px);
}
.chat-id-item.active-chat {
  background-color: #2196F3;
  color: white;
  font-weight: 500;
}
.add-chat-button {
  color: #2196F3;
  font-weight: 500;
}
.main-content {

  flex: 1;
  padding: 20px;
  background-color: #fff;
  border-radius: 12px;
  margin-left: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.chat-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.chat-header {
  padding: 16px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #f8f9fa;
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
}
.chat-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}
.chat-footer {
  padding: 16px;
  border-top: 1px solid #e0e0e0;
  background-color: #f8f9fa;
  border-bottom-left-radius: 12px;
  border-bottom-right-radius: 12px;
  display: flex;
  gap: 12px;
}
.chat-footer input {
  flex: 1;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
}
.chat-footer button {
  padding: 12px 24px;
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}
.chat-footer button:hover {
  background-color: #1976D2;
}
.file-upload-label {
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 12px;
  border: 1px dashed #e0e0e0;
  border-radius: 8px;
  color: #666;
  transition: all 0.3s;
}
.file-upload-label:hover {
  border-color: #2196F3;
  color: #2196F3;
}
.preview-image {
  max-width: 100px;
  max-height: 100px;
}
.message-list {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  min-height: 100%;
}
.message {
  background-color: transparent;
  padding: 8px 12px;
  border-radius: 16px;
  max-width: 70%;
  margin-bottom: 8px;
}
.message.is-me {
  align-self: flex-end;
  background-color: transparent;
}

</style>