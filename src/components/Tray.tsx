import React from 'react';
import type { TrayState, TrayPiece } from '../types';
import { PiecePreview } from './PiecePreview';

interface TrayProps {
  tray: TrayState;
  selectedInstanceId: string | null;
  onSelectPiece: (piece: TrayPiece) => void;
}

export const Tray: React.FC<TrayProps> = ({
  tray,
  selectedInstanceId,
  onSelectPiece,
}) => {
  return (
    <div className="w-full max-w-md flex items-center justify-around gap-3 p-4 bg-slate-900/80 backdrop-blur-md rounded-3xl border border-slate-800 shadow-xl mt-4">
      {tray.map((trayPiece, idx) => {
        if (!trayPiece) {
          return (
            <div
              key={`empty_${idx}`}
              className="w-24 h-24 rounded-2xl border-2 border-dashed border-slate-800 flex items-center justify-center text-slate-700 text-xs font-bold"
            >
              EMPTY
            </div>
          );
        }

        const isSelected = selectedInstanceId === trayPiece.instanceId;

        return (
          <div
            key={trayPiece.instanceId}
            onClick={() => onSelectPiece(trayPiece)}
            className={`cursor-pointer transition-all duration-200 transform hover:scale-105 active:scale-95 p-2 rounded-2xl flex items-center justify-center min-w-[80px] min-h-[80px] ${
              isSelected ? 'bg-cyan-500/10 border-2 border-cyan-400 shadow-lg shadow-cyan-500/30' : 'bg-slate-800/50 border border-slate-700/50 hover:border-slate-600'
            }`}
          >
            <PiecePreview
              shape={trayPiece.definition.shape}
              color={trayPiece.definition.color}
              size="sm"
              isSelected={isSelected}
            />
          </div>
        );
      })}
    </div>
  );
};
