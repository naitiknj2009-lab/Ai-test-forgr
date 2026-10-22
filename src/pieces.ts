import type { PieceDefinition, Shape } from './types';

// Standard block puzzle shapes
export const SHAPES: { name: string; shape: Shape }[] = [
  // 1x1
  { name: 'single', shape: [[1]] },

  // Dominoes
  { name: 'domino_h', shape: [[1, 1]] },
  { name: 'domino_v', shape: [[1], [1]] },

  // Trominoes
  { name: 'tromino_h', shape: [[1, 1, 1]] },
  { name: 'tromino_v', shape: [[1], [1], [1]] },
  { name: 'tromino_l1', shape: [[1, 0], [1, 1]] },
  { name: 'tromino_l2', shape: [[0, 1], [1, 1]] },
  { name: 'tromino_l3', shape: [[1, 1], [1, 0]] },
  { name: 'tromino_l4', shape: [[1, 1], [0, 1]] },

  // Tetrominoes
  { name: 'tetromino_i_h', shape: [[1, 1, 1, 1]] },
  { name: 'tetromino_i_v', shape: [[1], [1], [1], [1]] },
  { name: 'tetromino_square', shape: [[1, 1], [1, 1]] },
  { name: 'tetromino_t1', shape: [[1, 1, 1], [0, 1, 0]] },
  { name: 'tetromino_t2', shape: [[0, 1, 0], [1, 1, 1]] },
  { name: 'tetromino_l1', shape: [[1, 0], [1, 0], [1, 1]] },
  { name: 'tetromino_l2', shape: [[0, 1], [0, 1], [1, 1]] },
  { name: 'tetromino_z1', shape: [[1, 1, 0], [0, 1, 1]] },
  { name: 'tetromino_z2', shape: [[0, 1, 1], [1, 1, 0]] },

  // Pentominoes & larger favorites
  { name: 'pentomino_i_h', shape: [[1, 1, 1, 1, 1]] },
  { name: 'pentomino_i_v', shape: [[1], [1], [1], [1], [1]] },
  { name: 'pentomino_cross', shape: [[0, 1, 0], [1, 1, 1], [0, 1, 0]] },
  { name: 'pentomino_3x3', shape: [[1, 1, 1], [1, 1, 1], [1, 1, 1]] },
  { name: 'corner_3x3', shape: [[1, 1, 1], [1, 0, 0], [1, 0, 0]] },
];

export const COLORS = [
  '#FF595E', // Vibrant Red
  '#FFCA3A', // Vibrant Yellow
  '#8AC926', // Vibrant Green
  '#1982C4', // Vibrant Blue
  '#6A4C93', // Vibrant Purple
  '#FF924C', // Vibrant Orange
  '#00F5D4', // Cyan
  '#F72585', // Magenta
];

export function getRandomColor(): string {
  return COLORS[Math.floor(Math.random() * COLORS.length)];
}

export function generateRandomPiece(index: number = 0): PieceDefinition {
  const shapeObj = SHAPES[Math.floor(Math.random() * SHAPES.length)];
  return {
    id: `piece_${Date.now()}_${index}_${Math.random().toString(36).substring(2, 6)}`,
    shape: shapeObj.shape,
    color: getRandomColor(),
  };
}
