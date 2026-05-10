import { loadEnv } from 'vite'

const envMap = {
  development: 'dev',
  production: 'prod',
}

export function loadMapEnv(command, mode) {
  const envMode = command === 'serve' ? 'dev' : envMap[mode] || 'dev'
  const env = loadEnv(envMode, process.cwd())
  return env
}
