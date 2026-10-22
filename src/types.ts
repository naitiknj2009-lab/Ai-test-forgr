export type CellValue = string | null; // null or color/id string
export type BoardState = CellValue[][]; // 10x10

export type Shape = number[][]; // 2D matrix of 0 and 1

export interface PieceDefinition {
  id: string;
  shape: Shape;
  color: string;
}

export type TrayPiece = {
  instanceId: string;
  definition: PieceDefinition;
} | null;

export type TrayState = [TrayPiece, TrayPiece, TrayPiece];

export interface GameStats {
  score: number;
  highScore: number;
  linesCleared: number;
  combos: number;
  gameOver: boolean;
}

export type GameMode = 'classic';
