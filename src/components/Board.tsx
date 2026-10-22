import { useState } from 'react';
import type { BoardState, Shape } from '../types';
import { BOARD_SIZE, canPlacePiece } from '../engine';

interface BoardProps {
  board: BoardState;
  selectedPieceShape: Shape | null;
  selectedPieceColor: string | null;
  onCellClick: (row: number, col: number) => void;
}

export const Board: React.FC<BoardProps> = ({
  board,
  selectedPieceShape,
  selectedPieceColor,
  onCellClick,
}) => {
  const [hoverCoord, setHoverCoord] = useState<{ row: number; col: number } | null>(null);

  const handleCellMouseEnter = (r: number, c: number) => {
    if (selectedPieceShape) {
      setHoverCoord({ row: r, col: c });
    }
  };

  const handleBoardMouseLeave = () => {
    setHoverCoord(null);
  };

  // Determine if cell is part of the placement preview
  const isPreviewCell = (r: number, c: number): boolean => {
    if (!selectedPieceShape || !hoverCoord) return false;
    const { row: targetR, col: targetC } = hoverCoord;
    const shapeR = r - targetR;
    const shapeC = c - targetC;

    if (
      shapeR >= 0 &&
      shapeR < selectedPieceShape.length &&
      shapeC >= 0 &&
      shapeC < selectedPieceShape[0].length
    ) {
      return selectedPieceShape[shapeR][shapeC] === 1;
    }
    return false;
  };

  const isPreviewValid = (): boolean => {
    if (!selectedPieceShape || !hoverCoord) return false;
    return canPlacePiece(board, selectedPieceShape, hoverCoord.row, hoverCoord.col);
  };

  const validPreview = isPreviewValid();

  return (
    <div
      className="relative p-3 bg-slate-900/90 backdrop-blur-md rounded-3xl border-2 border-slate-800 shadow-2xl touch-none select-none"
      onMouseLeave={handleBoardMouseLeave}
    >
      <div
        className="grid gap-1.5"
        style={{
          gridTemplateColumns: `repeat(${BOARD_SIZE}, minmax(0, 1fr))`,
        }}
      >
        {board.map((row, r) =>
          row.map((cellColor, c) => {
            const preview = isPreviewCell(r, c);
            const showColor = cellColor !== null;

            let bgColor = 'bg-slate-800/80 border border-slate-700/50';
            let boxShadow = 'inset 0 1px 2px rgba(255,255,255,0.05)';

            if (showColor) {
              bgColor = '';
              boxShadow = 'inset 0 2px 4px rgba(255,255,255,0.3), 0 2px 6px rgba(0,0,0,0.4)';
            } else if (preview) {
              if (validPreview) {
                bgColor = '';
                boxShadow = `inset 0 0 12px ${selectedPieceColor}, 0 0 8px ${selectedPieceColor}`;
              } else {
                bgColor = 'bg-red-500/30 border border-red-500/50';
                boxShadow = 'inset 0 0 8px rgba(239, 68, 68, 0.4)';
              }
            }

            return (
              <div
                key={`${r}-${c}`}
                onClick={() => onCellClick(r, c)}
                onMouseEnter={() => handleCellMouseEnter(r, c)}
                style={{
                  backgroundColor: showColor ? cellColor : preview && validPreview ? selectedPieceColor! : undefined,
                  boxShadow,
                }}
                className={`w-7 h-7 sm:w-8 sm:h-8 md:w-9 md:h-9 rounded-lg transition-colors duration-100 cursor-pointer flex items-center justify-center ${bgColor}`}
              />
            );
          })
        )}
      </div>
    </div>
  );
};
