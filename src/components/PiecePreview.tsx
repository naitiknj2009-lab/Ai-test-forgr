import React from 'react';
import type { Shape } from '../types';

interface PiecePreviewProps {
  shape: Shape;
  color: string;
  size?: 'sm' | 'md' | 'lg';
  isSelected?: boolean;
}

export const PiecePreview: React.FC<PiecePreviewProps> = ({
  shape,
  color,
  size = 'md',
  isSelected = false,
}) => {
  const cellSize = size === 'sm' ? 14 : size === 'md' ? 20 : 26;

  return (
    <div
      className={`inline-flex flex-col gap-[2px] p-2 rounded-xl transition-transform ${
        isSelected ? 'scale-110 ring-2 ring-cyan-400 bg-white/10 shadow-lg shadow-cyan-500/20' : 'bg-black/20'
      }`}
    >
      {shape.map((row, rIdx) => (
        <div key={rIdx} className="flex gap-[2px]">
          {row.map((cell, cIdx) => (
            <div
              key={cIdx}
              style={{
                width: cellSize,
                height: cellSize,
                backgroundColor: cell === 1 ? color : 'transparent',
                borderRadius: 4,
                boxShadow: cell === 1 ? 'inset 0 1px 2px rgba(255,255,255,0.4), 0 2px 4px rgba(0,0,0,0.3)' : 'none',
              }}
            />
          ))}
        </div>
      ))}
    </div>
  );
};
