<template>
  <div id="container">
    <div class="hero-text">
        <h1 class="hero-title">Ai-Trip-Assistant</h1>
        <p class="hero-subtitle">快速、准确、合理的旅行规划助手</p>
        <button class="register-button">
          <a href="register" id="register">现在注册</a>
        </button>
    </div>
  </div>
</template>

<script>

export default {
  name: 'index',
  
}

</script>

<style>

#container {
  width: 100%;
  height: 100vh;
  text-align: center;
  background-image: url('./assets/doro_sea.jpg');
}

/* 全局样式 */
body {
  margin: 0;
  padding: 0;
  font-family: 'Inter', sans-serif;
}

/* 英雄区域样式 */
.hero {
  position: relative;
  height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  overflow: hidden;
}

.hero-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: -1;
}

.hero-text {
  max-width: 800px;
  padding: 2rem;
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* 标题样式 */
.hero-title {
  font-size: clamp(2.5rem, 8vw, 5rem); /* 响应式字体大小 */
  font-weight: 800;
  margin-bottom: 1rem;
  letter-spacing: -2px;
  animation: fadeInUp 1s ease-out;
}

/* 副标题样式 */
.hero-subtitle {
  font-size: clamp(1.2rem, 3vw, 1.8rem);
  font-weight: 300;
  margin-bottom: 2.5rem;
  line-height: 1.5;
  opacity: 0.9;
  animation: fadeInUp 1s ease-out 0.2s;
  animation-fill-mode: backwards;
}

/* 注册按钮样式 */
.register-button {
  background: transparent;
  border: 2px solid white;
  border-radius: 50px;
  padding: 1.2rem 3.5rem;
  font-size: 1.1rem;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  animation: fadeInUp 1s ease-out 0.4s;
  animation-fill-mode: backwards;
}

.register-button:hover {
  background: white;
  color: #333;
  transform: translateY(-3px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.2);
}

.register-button a {
  text-decoration: none;
  color: inherit;
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

</style>
