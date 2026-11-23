<script setup>
import { computed, onMounted, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { liveApi, publicApi } from '@/api'
import { announcementTypeMap, formatTime } from '@/utils/format'
import EmptyState from '@/components/EmptyState.vue'

const auth = useAuthStore()
const announcements = ref([])
const results = ref([])
const liveMatches = ref([])
const loading = ref(true)

const featuredAnnouncements = computed(() => announcements.value.slice(0, 4))
const latestResults = computed(() => results.value.slice(0, 4))
const nextLive = computed(() => liveMatches.value[0])

onMounted(async () => {
  try {
    const [ann, res] = await Promise.all([publicApi.announcements(10), publicApi.results()])
    announcements.value = ann
    results.value = res
    if (auth.isLoggedIn) {
      try {
        liveMatches.value = await liveApi.matches()
      } catch {
        liveMatches.value = []
      }
    }
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="container">
    <section class="hero">
      <div class="hero-copy" v-reveal>
        <span class="eyebrow">Volleyball 3.0 · 全新出击</span>
        <h1>让每一分，<br /><em>都被看见。</em></h1>
        <p>
          从开赛公告到最后一分，实时比分、拖拽排阵、MVP / SVP 自动评选。
          计分员专注赛场，观众同步心跳。
        </p>
        <div class="hero-actions">
          <router-link v-if="nextLive" :to="`/live/${nextLive.id}`" class="btn-primary">
            <i class="live-dot" /> 正在直播 · {{ nextLive.leftTeamName }} VS {{ nextLive.rightTeamName }}
          </router-link>
          <router-link v-else-if="auth.isLoggedIn" to="/live" class="btn-primary">
            进入实时比赛
          </router-link>
          <router-link v-else to="/login" class="btn-primary">登录观赛</router-link>
          <router-link to="/results" class="btn-ghost">查看赛果</router-link>
        </div>
        <div class="hero-badges">
          <span>🏐 6 人制阵容校验</span>
          <span>⚡ WebSocket 实时比分</span>
          <span>🏆 MVP / SVP 自动评选</span>
          <span>🎯 出界得分单独记录</span>
        </div>
      </div>

      <div class="hero-stage" v-reveal="120">
        <div class="hero-court" />
        <div class="hero-shadow" />
        <div class="hero-ball" />
        <div class="hero-chip a">🏐 每局 25 分<small>领先 2 分</small></div>
        <div class="hero-chip b">⚡ 实时推送<small>零延迟观赛</small></div>
        <div class="hero-chip c">🏆 MVP 规则<small>得分 &gt; 连击</small></div>
      </div>
    </section>

    <div class="stat-strip" v-reveal="160">
      <div class="stat-tile">
        <strong>6<span style="font-size: 13px; color: var(--muted)"> 人</span></strong>
        <span>标准场上阵容</span>
      </div>
      <div class="stat-tile">
        <strong>{{ results.length }}</strong>
        <span>已完成的比赛</span>
      </div>
      <div class="stat-tile">
        <strong>{{ liveMatches.length }}</strong>
        <span>正在进行中</span>
      </div>
      <div class="stat-tile">
        <strong>{{ announcements.length }}</strong>
        <span>条公告与赛果</span>
      </div>
    </div>

    <section class="section">
      <div class="section-head">
        <div>
          <h2 class="section-title">实时比赛 <span>LIVE</span></h2>
          <p class="section-desc">计分员记录，观众同步观看，只有创建者可以修改比分。</p>
        </div>
        <router-link v-if="auth.isLoggedIn" to="/live" class="text-link">全部比赛 →</router-link>
      </div>

      <div v-if="!auth.isLoggedIn" class="card" style="padding: 34px; display: flex; justify-content: space-between; align-items: center; gap: 20px; flex-wrap: wrap">
        <div>
          <h3 style="font-size: 20px; font-weight: 800">实时比赛仅对登录用户开放</h3>
          <p style="color: var(--muted); margin-top: 8px; font-size: 13px">
            注册普通用户即可实时观看进行中的比赛，游客可以先浏览公告与赛果。
          </p>
        </div>
        <div style="display: flex; gap: 10px">
          <router-link to="/register" class="btn-primary">免费注册</router-link>
          <router-link to="/login" class="btn-ghost">已有账号</router-link>
        </div>
      </div>

      <div v-else-if="liveMatches.length" class="live-grid">
        <router-link
          v-for="(item, index) in liveMatches"
          :key="item.id"
          :to="`/live/${item.id}`"
          class="live-card"
          v-reveal="index * 80"
        >
          <div style="display: flex; justify-content: space-between; align-items: center">
            <span class="tag live"><i class="live-dot" /> LIVE</span>
            <span style="color: var(--night-muted); font-size: 11px">第 {{ item.currentSet }} 局</span>
          </div>
          <div class="teams">
            <span>{{ item.leftTeamName }}</span>
            <span class="score">{{ item.leftSets }} : {{ item.rightSets }}</span>
            <span style="text-align: right">{{ item.rightTeamName }}</span>
          </div>
          <div class="meta">
            <span>本局 {{ item.leftPoints }} : {{ item.rightPoints }}</span>
            <span>{{ item.rules }}</span>
          </div>
        </router-link>
      </div>

      <EmptyState
        v-else-if="!loading"
        title="当前没有进行中的比赛"
        description="新的比赛即将开始，先去公告区看看最近的安排吧。"
      >
        <router-link to="/results" class="btn-ghost">回看精彩赛果</router-link>
      </EmptyState>
    </section>

    <section class="section">
      <div class="section-head">
        <div>
          <h2 class="section-title">公告 <span>BOARD</span></h2>
          <p class="section-desc">管理员公告、开赛提醒与赛果速递都会出现在这里。</p>
        </div>
      </div>

      <div v-if="featuredAnnouncements.length" class="announcement-list">
        <article
          v-for="(item, index) in featuredAnnouncements"
          :key="item.id"
          class="announcement-card"
          :class="{ pinned: item.pinned }"
          v-reveal="index * 60"
        >
          <div class="announcement-top">
            <span
              class="tag"
              :class="{
                green: item.type === 'MATCH_END',
                blue: item.type === 'MATCH_START'
              }"
            >
              {{ (announcementTypeMap[item.type] || {}).label || '公告' }}
            </span>
            <span v-if="item.pinned" class="tag">置顶</span>
            <span style="margin-left: auto; color: var(--muted); font-size: 12px">
              {{ formatTime(item.createdAt) }}
            </span>
          </div>
          <h3 class="announcement-title">{{ item.title }}</h3>
          <p class="announcement-body">{{ item.content }}</p>
          <div class="announcement-meta">
            <span>{{ item.matchId ? '关联比赛 #' + item.matchId : '系统公告' }}</span>
            <router-link
              v-if="item.matchId && item.type === 'MATCH_END'"
              :to="`/results/${item.matchId}`"
              class="text-link"
            >
              查看赛果 →
            </router-link>
            <router-link
              v-else-if="item.matchId && item.type === 'MATCH_START' && auth.isLoggedIn"
              :to="`/live/${item.matchId}`"
              class="text-link"
            >
              进入直播 →
            </router-link>
          </div>
        </article>
      </div>

      <EmptyState v-else-if="!loading" title="公告栏还是空的" description="管理员发布公告后会显示在这里。" />
    </section>

    <section class="section">
      <div class="section-head">
        <div>
          <h2 class="section-title">最新赛果 <span>RESULTS</span></h2>
          <p class="section-desc">每一场结束的比赛，都会自动生成公告与 MVP / SVP。</p>
        </div>
        <router-link to="/results" class="text-link">全部赛果 →</router-link>
      </div>

      <div v-if="latestResults.length" class="grid-results">
        <router-link
          v-for="(item, index) in latestResults"
          :key="item.id"
          :to="`/results/${item.id}`"
          class="result-card"
          v-reveal="index * 70"
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

      <EmptyState v-else-if="!loading" title="还没有比赛结果" description="第一场比赛结束后，结果会自动出现在这里。" />
    </section>

    <section class="section">
      <div class="section-head">
        <div>
          <h2 class="section-title">不止计分 <span>FEATURES</span></h2>
          <p class="section-desc">为计分员和观众准备的细节体验。</p>
        </div>
      </div>
      <div class="feature-grid">
        <div class="feature-card" v-reveal>
          <div class="feature-icon">🎯</div>
          <h3>出界得分</h3>
          <p>对方出界时一键记分，不归属到任何球员，统计更干净。</p>
        </div>
        <div class="feature-card" v-reveal="80">
          <div class="feature-icon">🧩</div>
          <h3>拖拽排阵</h3>
          <p>把可用球员拖入 1-6 号位，六人齐整才能上场。</p>
        </div>
        <div class="feature-card" v-reveal="160">
          <div class="feature-icon">🏆</div>
          <h3>MVP / SVP</h3>
          <p>得分优先，其次最大连续得分，再次连续得分次数。</p>
        </div>
        <div class="feature-card" v-reveal="240">
          <div class="feature-icon">🔒</div>
          <h3>安全计分</h3>
          <p>只有创建比赛的计分员可以计分，比赛过程中锁定阵容。</p>
        </div>
      </div>
    </section>
  </div>
</template>
