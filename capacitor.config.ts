import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.naitik.blockpuzzle',
  appName: 'Block Puzzle',
  webDir: 'dist',
  server: {
    androidScheme: 'https'
  }
};

export default config;
