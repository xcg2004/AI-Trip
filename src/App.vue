<template>
  <div id="app">
    <header>
      <img src="./assets/doro_sea.jpg" alt="logo" class="logo">
      <nav>
        <!-- <a href="#">皮肤库</a> -->
        <a href="chat">Ai客服助手</a>
        <a href="login">登录</a>
      </nav>
    </header>
    <section class="hero">
      
    </section>
    <router-view></router-view>
  </div>
</template>

<script>


export default {
  name: 'App',
  
}

</script>

<style>
#app {
  font-family: Arial, sans-serif;
  margin: 0;
  padding: 0;
  background-image: url('./assets/background.png');
  background-attachment: fixed;
  background-position: center;
  background-size: cover;
}
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
}
.logo {
  height: 40px;
  background-color: #48759f;
  border-radius: 45%;
}
nav a {
  margin-left: 1rem;
  text-decoration: none;
  color: #67ace8bf;
}
.hero {
  position: relative;
  text-align: center;
  color: white;
  
}
/* .hero-image {
  width: 100%;
  height: 50%;
} */
.hero-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
button {
  padding: 1rem 2rem;
  background-color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>