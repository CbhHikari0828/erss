import { defineConfig } from '@vben/vite-config';

import Components from 'unplugin-vue-components/vite';
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers';

export default defineConfig(async () => {
  return {
    application: {},
    vite: {
      plugins: [
        Components({
          dts: 'src/components.d.ts',
          resolvers: [AntDesignVueResolver({ importStyle: false })],
        }),
      ],
      server: {
        proxy: {
          '/api': {
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/api/, ''),
            // mock代理目标地址
            target: 'http://localhost:5320/api',
            ws: true,
          },
        },
      },
    },
  };
});
