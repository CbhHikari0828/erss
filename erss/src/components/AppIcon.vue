<script setup>
import InlineIcon from './InlineIcon.vue'

defineProps({
  name: {
    type: String,
    required: true,
  },
  active: {
    type: Boolean,
    default: false,
  },
  size: {
    type: String,
    default: '34rpx',
  },
  opacity: {
    type: Number,
    default: 0.72,
  },
})

const mpIconMap = {
  home: 'i-carbon-home',
  category: 'i-carbon-categories',
  publish: 'i-carbon-send-alt',
  message: 'i-carbon-chat',
  mine: 'i-carbon-user-avatar',
}
</script>

<template>
  <!-- #ifdef MP-WEIXIN -->
  <view
    class="app-icon"
    :class="[mpIconMap[name], active ? 'app-icon--active' : '']"
    :style="{
      width: size,
      height: size,
      opacity: active ? 1 : opacity,
    }"
  />
  <!-- #endif -->

  <!-- #ifndef MP-WEIXIN -->
  <InlineIcon :name="name" :active="active" :size="size" :opacity="opacity" />
  <!-- #endif -->
</template>

<style lang="scss" scoped>
.app-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2c2c2c;
  font-size: v-bind(size);

  &--active {
    color: #ffffff;
  }
}
</style>
