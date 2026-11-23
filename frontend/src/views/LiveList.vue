<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { liveApi } from '@/api'
import EmptyState from '@/components/EmptyState.vue'

const matches = ref([])
const loading = ref(true)
let timer = null

const load = async (silent = false) => {
  if (!silent) loading.value = true
  try {
    matches.value = await liveApi.matches()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  load()
  timer = window.setInterval(() => load(true), 15000)
})

onUnmounted(() => window.clearInterval(timer))
</script>

<template>
  <div class="container page">
    <div class="page-head">
      <div>
        <span class="eyebrow">Live Now</span>
        <h1 class="page-title">实时比赛</h1>
        <p class="page-sub">比分每 15 秒自动刷新，进入比赛后通过 WebSocket 实时同步。</p>
      </div>
      <router-link to="/" class="btn-ghost">返回首页</router-link>
    </div>

    <div v-if="loading" v-loading="true" style="min-height: 240px" />

    <div v-else-if="matches.length" class="live-grid">
      <router-link
        v-for="(item, index) in matches"
        :key="item.id"
        :to="`/live/${item.id}`"
        class="live-card"
        v-reveal="index * 70"
      >
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span class="tag live"><i class="live-dot" /> LIVE</span>
          <span style="color: var(--night-muted); font-size: 11px">
            第 {{ item.currentSet }} 局 · {{ item.rules }}
          </span>
        </div>
        <div class="teams">
          <span>{{ item.leftTeamName }}</span>
          <span class="score">{{ item.leftSets }} : {{ item.rightSets }}</span>
          <span style="text-align: right">{{ item.rightTeamName }}</span>
        </div>
        <div class="meta">
          <span>本局 {{ item.leftPoints }} : {{ item.rightPoints }}</span>
          <span>计分员：{{ item.creatorName }}</span>
        </div>
      </router-link>
    </div>

    <EmptyState
      v-else
      title="暂时没有进行中的比赛"
      description="计分员开始一场比赛后，这里会出现实时入口。"
    >
      <router-link to="/results" class="btn-ghost">先看看赛果</router-link>
    </EmptyState>
  </div>
</template>
