export interface GameStats {
  gamesPlayed: number;
  totalScore: number;
  linesCleared: number;
  maxCombo: number;
  highScore: number;
}

export interface GameSettings {
  theme: 'neon' | 'cyber' | 'midnight' | 'sunset';
  soundEnabled: boolean;
  hapticsEnabled: boolean;
}

const HIGHSCORE_KEY = 'block_puzzle_high_score';
const STATS_KEY = 'block_puzzle_stats';
const SETTINGS_KEY = 'block_puzzle_settings';

export function getStoredHighScore(): number {
  try {
    const val = localStorage.getItem(HIGHSCORE_KEY);
    return val ? parseInt(val, 10) : 0;
  } catch {
    return 0;
  }
}

export function saveHighScore(score: number): void {
  try {
    const current = getStoredHighScore();
    if (score > current) {
      localStorage.setItem(HIGHSCORE_KEY, score.toString());
    }
  } catch {
    // ignore
  }
}

export function getStoredStats(): GameStats {
  try {
    const val = localStorage.getItem(STATS_KEY);
    if (val) {
      const stats = JSON.parse(val);
      const highScore = Math.max(stats.highScore || 0, getStoredHighScore());
      return { ...stats, highScore };
    }
  } catch {
    // fallback
  }
  return {
    gamesPlayed: 0,
    totalScore: 0,
    linesCleared: 0,
    maxCombo: 0,
    highScore: getStoredHighScore(),
  };
}

export function updateStoredStats(gameScore: number, lines: number, combo: number): GameStats {
  const current = getStoredStats();
  saveHighScore(gameScore);
  const highScore = Math.max(current.highScore, gameScore, getStoredHighScore());
  const updated: GameStats = {
    gamesPlayed: current.gamesPlayed + 1,
    totalScore: current.totalScore + gameScore,
    linesCleared: current.linesCleared + lines,
    maxCombo: Math.max(current.maxCombo, combo),
    highScore,
  };

  try {
    localStorage.setItem(STATS_KEY, JSON.stringify(updated));
  } catch {
    // ignore
  }

  return updated;
}

export function getStoredSettings(): GameSettings {
  try {
    const val = localStorage.getItem(SETTINGS_KEY);
    if (val) {
      return JSON.parse(val);
    }
  } catch {
    // fallback
  }
  return {
    theme: 'neon',
    soundEnabled: true,
    hapticsEnabled: true,
  };
}

export function saveStoredSettings(settings: GameSettings): void {
  try {
    localStorage.setItem(SETTINGS_KEY, JSON.stringify(settings));
  } catch {
    // ignore
  }
}
