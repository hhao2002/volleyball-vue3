<script setup>
defineProps({
  icon: { type: String, default: '🏐' },
  title: { type: String, default: '这里还空空的' },
  description: { type: String, default: '新的故事正在路上，稍后再来看看吧。' }
})
</script>

<template>
  <div class="empty">
    <div class="empty-icon">{{ icon }}</div>
    <h3>{{ title }}</h3>
    <p>{{ description }}</p>
    <slot />
  </div>
</template>
