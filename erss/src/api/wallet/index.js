import alova from '@/api/index.js'

export function getWalletSummary() {
  return alova.Get('/wallet/summary')
}

export function listTransactions() {
  return alova.Get('/wallet/transactions')
}

export function listWithdrawals() {
  return alova.Get('/wallet/withdrawals')
}

export function withdrawWallet(payload) {
  return alova.Post('/wallet/withdraw', payload)
}
