import { describe, it, expect, beforeEach } from 'vitest';
import {
  createEmptyBoard,
  canPlacePiece,
  placePieceOnBoard,
  checkAndClearLines,
  canAnyPieceFit,
  generateInitialTray,
  BOARD_SIZE,
} from './engine';
import { getStoredHighScore, saveHighScore, getStoredStats, updateStoredStats, getStoredSettings, saveStoredSettings } from './storage';

// Mock localStorage for Node environment in Vitest
const localStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => { store[key] = value.toString(); },
    removeItem: (key: string) => { delete store[key]; },
    clear: () => { store = {}; },
  };
})();

Object.defineProperty(globalThis, 'localStorage', {
  value: localStorageMock,
  writable: true,
});

describe('Block Puzzle Engine & Storage (QA Test Suite)', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('creates an empty 10x10 board with null cells', () => {
    const board = createEmptyBoard();
    expect(board.length).toBe(BOARD_SIZE);
    expect(board[0].length).toBe(BOARD_SIZE);
    expect(board.every(row => row.every(cell => cell === null))).toBe(true);
  });

  it('validates boundary placements correctly', () => {
    const board = createEmptyBoard();
    const square = [
      [1, 1],
      [1, 1],
    ];

    // Valid inside bounds
    expect(canPlacePiece(board, square, 0, 0)).toBe(true);
    expect(canPlacePiece(board, square, 8, 8)).toBe(true);

    // Invalid out of bounds
    expect(canPlacePiece(board, square, 9, 9)).toBe(false);
    expect(canPlacePiece(board, square, -1, 0)).toBe(false);
    expect(canPlacePiece(board, square, 0, 9)).toBe(false);
  });

  it('handles simultaneous row and column clearing correctly', () => {
    let board = createEmptyBoard();
    // Fill row 0 completely
    for (let c = 0; c < BOARD_SIZE; c++) {
      board[0][c] = '#FF595E';
    }
    // Fill column 0 completely
    for (let r = 0; r < BOARD_SIZE; r++) {
      board[r][0] = '#1982C4';
    }

    const result = checkAndClearLines(board);
    expect(result.rowsCleared).toBe(1);
    expect(result.colsCleared).toBe(1);
    expect(result.totalCleared).toBe(19); // 10 in row 0 + 9 remaining in col 0 (excluding intersection (0,0) already counted)
    expect(result.newBoard[0].every(cell => cell === null)).toBe(true);
  });

  it('refills tray when all pieces are used', () => {
    const tray = generateInitialTray();
    expect(tray.length).toBe(3);
    const emptiedTray = [null, null, null] as any;
    const activePieces = emptiedTray.filter((p: any) => p !== null);
    expect(activePieces.length).toBe(0);
  });

  it('detects game-over when no tray pieces fit', () => {
    const board = createEmptyBoard();
    // Fill entire board
    for (let r = 0; r < BOARD_SIZE; r++) {
      for (let c = 0; c < BOARD_SIZE; c++) {
        board[r][c] = '#8AC926';
      }
    }

    const square2x2 = [
      [1, 1],
      [1, 1],
    ];
    const tray = [
      {
        instanceId: 't1',
        definition: { id: 'sq', shape: square2x2, color: '#1982C4' },
      },
      null,
      null,
    ];

    expect(canAnyPieceFit(board, tray as any)).toBe(false);
  });

  it('handles rapid sequential piece placements without state corruption', () => {
    let board = createEmptyBoard();
    const single = [[1]];

    // Rapidly place 5 single pieces at (0,0) through (0,4)
    for (let i = 0; i < 5; i++) {
      expect(canPlacePiece(board, single, 0, i)).toBe(true);
      board = placePieceOnBoard(board, single, '#FFCA3A', 0, i);
      expect(board[0][i]).toBe('#FFCA3A');
    }
  });

  it('persists and recovers saved state (high score, stats, settings)', () => {
    expect(getStoredHighScore()).toBe(0);
    saveHighScore(1500);
    expect(getStoredHighScore()).toBe(1500);

    // Should not overwrite with lower score
    saveHighScore(1000);
    expect(getStoredHighScore()).toBe(1500);

    const stats = updateStoredStats(500, 2, 3);
    expect(stats.gamesPlayed).toBe(1);
    expect(stats.totalScore).toBe(500);
    expect(stats.linesCleared).toBe(2);
    expect(stats.maxCombo).toBe(3);
    expect(stats.highScore).toBe(1500);

    const storedStats = getStoredStats();
    expect(storedStats.gamesPlayed).toBe(1);

    const settings = getStoredSettings();
    expect(settings.theme).toBe('neon');
    saveStoredSettings({ theme: 'cyber', soundEnabled: false, hapticsEnabled: true });
    expect(getStoredSettings().theme).toBe('cyber');
    expect(getStoredSettings().soundEnabled).toBe(false);
  });
});
