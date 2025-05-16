<template>
  <div class="container">
    <div class="login-container">
      <div class="login-header">
        <h2>登录</h2>
      </div>
      <form @submit.prevent="login" class="login-form">
        <div class="input-group">
          <label for="username">用户名:</label>
          <input type="text" id="username" v-model="formData.username" required class="input-field" />
        </div>
        <div class="input-group">
          <label for="password">密码:</label>
          <input type="password" id="password" v-model="formData.password" required class="input-field" />
        </div>
        <button type="submit" class="submit-button">登录</button>
        <button >
          <a href="register">
            还未注册？立即注册
          </a>
        </button>
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
        password: ""
      }
    };
  },
  methods: {
    async login() {
      try {
        const response = await fetch("http://localhost:8080/user/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(this.formData)
        });
        const result = await response.json();
        if (result.code === 200) {
          console.log("token:",result.data);
          // const data = await response.json();
          if (result.data) {
            console.log("success");
            localStorage.setItem('token', result.data);
          }
          alert("登录成功！");
          // 登录成功后，跳转到聊天页面
          this.$router.push({ path:"/chat" });
        } else {
          alert("登录失败，请检查用户名或密码。");
        }
      } catch (error) {
        console.error("登录请求失败:", error);
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


.login-container {
  width: 100%;
  height: auto;
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background: rgba(131, 148, 197, 0.9);
  padding: 40px;
  max-width: 400px;

  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  color: #2c3e50;
  font-size: 28px;
  font-weight: 700;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.input-group {
  display: flex;
  flex-direction: column;
  margin-right:35px;
  gap: 12px;
}

.input-group label {
  color: #34495e;
  font-size: 16px;
  font-weight: 500;
}

.input-field {
  width: 100%;
  padding: 16px;
  border: 2px solid #ecf0f1;
  border-radius: 10px;
  font-size: 16px;
  transition: all 0.3s ease;
}

.input-field:focus {
  outline: none;
  border-color: #2196F3;
  box-shadow: 0 0 10px rgba(33, 150, 243, 0.2);
  transform: scale(1.02);
}



.submit-button:hover {
  background: linear-gradient(135deg, #1976D2 0%, #1565C0 100%);
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(33, 150, 243, 0.4);
}

.submit-button:active {
  transform: translateY(0);
  box-shadow: 0 3px 10px rgba(33, 150, 243, 0.3);
}

button {
  background-color: transparent;
  border: 1px solid white;
  color: white;
  padding: 10px 30px;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease, color 0.3s ease;
}
button:hover {
  background-color: white;
  color: #333;
}
button a {
  text-decoration: none;
  color: inherit;
}
</style>