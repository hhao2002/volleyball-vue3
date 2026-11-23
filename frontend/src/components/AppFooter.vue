<script setup>
const year = new Date().getFullYear()
</script>

<template>
  <footer class="app-footer">
    <div class="container footer-inner">
      <div>
        <strong>排球比赛系统 3.0</strong>
        <span style="margin-left: 10px">让每一分都被看见</span>
      </div>
      <div>
        <span class="footer-ball">🏐</span>
        <span style="margin-left: 8px">Spring Boot · Vue 3 · 实时计分</span>
      </div>
      <div>© {{ year }} Volleyball 3.0</div>
    </div>
  </footer>
</template>
