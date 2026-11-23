<script setup>
import { onMounted, ref } from 'vue'
import { publicApi } from '@/api'
import { formatTime } from '@/utils/format'
import EmptyState from '@/components/EmptyState.vue'

const results = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    results.value = await publicApi.results()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="container page">
    <div class="page-head">
      <div>
        <span class="eyebrow">Results Archive</span>
        <h1 class="page-title">比赛结果</h1>
        <p class="page-sub">每一场已结束的比赛，包含完整局分、得分记录与 MVP / SVP。</p>
      </div>
      <router-link to="/" class="btn-ghost">返回首页</router-link>
    </div>

    <div v-if="loading" v-loading="loading" style="min-height: 240px" />

    <div v-else-if="results.length" class="grid-results">
      <router-link
        v-for="(item, index) in results"
        :key="item.id"
        :to="`/results/${item.id}`"
        class="result-card"
        v-reveal="index * 60"
      >
        <div class="result-head">
          <span class="result-name">{{ item.name }}</span>
          <span class="tag green">已结束</span>
        </div>
        <div class="result-score">
          <div class="result-team">
            <span>{{ item.leftTeamName }}</span>
            <small>{{ item.winnerTeamId === item.leftTeamId ? '胜者' : '挑战者' }}</small>
          </div>
          <div class="result-sets">
            {{ item.leftSets }}<em>:</em>{{ item.rightSets }}
          </div>
          <div class="result-team right">
            <span>{{ item.rightTeamName }}</span>
            <small>{{ item.winnerTeamId === item.rightTeamId ? '胜者' : '挑战者' }}</small>
          </div>
        </div>
        <div class="result-footer">
          <span class="mvp-line">MVP {{ item.mvpName || '暂无' }} · SVP {{ item.svpName || '暂无' }}</span>
          <span>{{ formatTime(item.endTime) }}</span>
        </div>
      </router-link>
    </div>

    <EmptyState
      v-else
      title="还没有比赛结果"
      description="比赛结束后，赛果会自动生成公告并展示在这里。"
    >
      <router-link to="/live" class="btn-primary">去看实时比赛</router-link>
    </EmptyState>
  </div>
</template>
