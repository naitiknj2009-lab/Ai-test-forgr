import type { BoardState, Shape, TrayState } from './types';
import { generateRandomPiece } from './pieces';

export const BOARD_SIZE = 10;

export function createEmptyBoard(): BoardState {
  return Array.from({ length: BOARD_SIZE }, () => Array(BOARD_SIZE).fill(null));
}

export function canPlacePiece(
  board: BoardState,
  shape: Shape,
  targetRow: number,
  targetCol: number
): boolean {
  const rowLen = shape.length;
  const colLen = shape[0].length;

  if (targetRow < 0 || targetCol < 0 || targetRow + rowLen > BOARD_SIZE || targetCol + colLen > BOARD_SIZE) {
    return false;
  }

  for (let r = 0; r < rowLen; r++) {
    for (let c = 0; c < colLen; c++) {
      if (shape[r][c] === 1) {
        const boardR = targetRow + r;
        const boardC = targetCol + c;
        if (board[boardR][boardC] !== null) {
          return false;
        }
      }
    }
  }

  return true;
}

export function placePieceOnBoard(
  board: BoardState,
  shape: Shape,
  color: string,
  targetRow: number,
  targetCol: number
): BoardState {
  const newBoard = board.map(row => [...row]);
  const rowLen = shape.length;
  const colLen = shape[0].length;

  for (let r = 0; r < rowLen; r++) {
    for (let c = 0; c < colLen; c++) {
      if (shape[r][c] === 1) {
        newBoard[targetRow + r][targetCol + c] = color;
      }
    }
  }

  return newBoard;
}

export interface ClearResult {
  newBoard: BoardState;
  rowsCleared: number;
  colsCleared: number;
  totalCleared: number;
}

export function checkAndClearLines(board: BoardState): ClearResult {
  const newBoard = board.map(row => [...row]);
  const fullRows: number[] = [];
  const fullCols: number[] = [];

  // Check full rows
  for (let r = 0; r < BOARD_SIZE; r++) {
    if (newBoard[r].every(cell => cell !== null)) {
      fullRows.push(r);
    }
  }

  // Check full columns
  for (let c = 0; c < BOARD_SIZE; c++) {
    let isFull = true;
    for (let r = 0; r < BOARD_SIZE; r++) {
      if (newBoard[r][c] === null) {
        isFull = false;
        break;
      }
    }
    if (isFull) {
      fullCols.push(c);
    }
  }

  // Clear cells in full rows and columns
  for (const r of fullRows) {
    for (let c = 0; c < BOARD_SIZE; c++) {
      newBoard[r][c] = null;
    }
  }

  for (const c of fullCols) {
    for (let r = 0; r < BOARD_SIZE; r++) {
      newBoard[r][c] = null;
    }
  }

  let clearedCount = 0;
  for (let r = 0; r < BOARD_SIZE; r++) {
    for (let c = 0; c < BOARD_SIZE; c++) {
      if (board[r][c] !== null && newBoard[r][c] === null) {
        clearedCount++;
      }
    }
  }

  return {
    newBoard,
    rowsCleared: fullRows.length,
    colsCleared: fullCols.length,
    totalCleared: clearedCount,
  };
}

export function countPieceBlocks(shape: Shape): number {
  let count = 0;
  for (const row of shape) {
    for (const cell of row) {
      if (cell === 1) count++;
    }
  }
  return count;
}

export function canAnyPieceFit(board: BoardState, tray: TrayState): boolean {
  const activePieces = tray.filter((p): p is NonNullable<TrayState[number]> => p !== null);
  if (activePieces.length === 0) return true;

  for (const trayPiece of activePieces) {
    const shape = trayPiece.definition.shape;
    let canFitAnywhere = false;

    for (let r = 0; r < BOARD_SIZE; r++) {
      for (let c = 0; c < BOARD_SIZE; c++) {
        if (canPlacePiece(board, shape, r, c)) {
          canFitAnywhere = true;
          break;
        }
      }
      if (canFitAnywhere) break;
    }

    if (canFitAnywhere) {
      return true;
    }
  }

  return false;
}

export function generateInitialTray(): TrayState {
  return [
    { instanceId: 'tray_0', definition: generateRandomPiece(0) },
    { instanceId: 'tray_1', definition: generateRandomPiece(1) },
    { instanceId: 'tray_2', definition: generateRandomPiece(2) },
  ];
}
