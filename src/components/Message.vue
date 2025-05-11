<template>
  <div class="message" :class="{ 'is-me': isMe }">
    <div class="message-container">
      <div v-if="!isMe" class="avatar">
        <img src="../assets/doro_sea.jpg" alt="客服头像">
      </div>
      <div class="text-content">
        <div class="name">{{ isMe ? '柴郡' : 'Doro' }}</div>
        <div class="message-content" style="white-space: pre-wrap;">
          {{ content }}
        </div>
      </div>
      <div v-if="isMe" class="avatar">
        <img src="../assets/chaijun_night.jpg" alt="用户头像">
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Message',
  props: {
    content: {
      type: String,
      required: true
    },
    isMe: {
      type: Boolean,
      default: false
    }
  }
}
</script>

<style>
.message {
  display: flex;
  margin-bottom: 12px;
}
.message.is-me {
  flex-direction: row-reverse;
}
.message-container {
  display: flex;
  align-items: flex-start;
}
.avatar {
  width: 60px;
  height: 60px;
  margin: 0 12px;
}
.avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}
.text-content {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}
.name {
  font-size: 12px;
  color: #666;
  margin-bottom: 5px;
  text-align: left; 
}
.message-content {
  padding: 12px 16px;
  border-radius: 16px;
  background-color: #dcf8c6;
  word-wrap: break-word;
}
.message.is-me .message-content {
  background-color: #ececec;
  color: black;
}
.message.is-me .name {
  text-align: right; 
}
</style>