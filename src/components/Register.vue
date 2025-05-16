<template>
  <div class="container">
    <div class="register-container">
      <div class="register-header">
        <h2>注册</h2>
      </div>
      <form @submit.prevent="register" class="register-form">
        <div class="input-group">
          <label for="username">用户名:</label>
          <input type="text" id="username" v-model="formData.username" required class="input-field" />
        </div>
        <div class="input-group">
          <label for="password">密码:</label>
          <input type="password" id="password" v-model="formData.password" required class="input-field" />
        </div>
        <div class="input-group">
          <label for="email">邮箱:</label>
          <input type="email" id="email" v-model="formData.email" required class="input-field" />
        </div>
        <button type="submit" class="submit-button">注册</button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      formData: {
        username: "",
        password: "",
        nickname: "",
        email: ""
      }
    };
  },
  methods: {
    async register() {
      try {
        const response = await fetch("http://localhost:8080/user/register", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(this.formData)
        });
        const res = await response.json();
        console.log("响应码："+res.code);
        if (res.code === 200) {
          alert("注册成功！");
        } else {
          alert("注册失败，请检查输入信息。");
        }
      } catch (error) {
        console.error("注册请求失败:", error);
      }
    }
  }
};
</script>

<style scoped>

.container {
  min-height: 100vh;
  display: flex;
  justify-content: center; /* 水平方向居中 */
  align-items: center; /* 垂直方向居中 */
  margin: 0; /* 去除默认的body边距 */
}
.register-container {
  width: 100%;
  height: auto; /* 这里高度设为auto，让内容撑开 */
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background: rgba(152, 167, 173, 0.9);
  padding: 40px;
  max-width: 400px;
}
.register-header {
  text-align: center;
  margin-bottom: 30px;
}
.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-right:25px;
}
.input-field {
  width: 100%;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s;
}
.input-field:focus {
  outline: none;
  border-color: #2196F3;
  box-shadow: 0 0 0 2px rgba(33, 150, 243, 0.1);
}
.submit-button {
  width: 100%;
  padding: 14px;
  background-color: #1785e6;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: background-color 0.3s, transform 0.1s;
}
.submit-button:hover {
  background-color: #1976D2;
  transform: translateY(-1px);
}
.submit-button:active {
  transform: translateY(0);
}

</style>