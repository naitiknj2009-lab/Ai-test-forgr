import React from 'react';

interface HeaderProps {
  score: number;
  highScore: number;
  onRestart: () => void;
  onOpenSettings: () => void;
}

export const Header: React.FC<HeaderProps> = ({
  score,
  highScore,
  onRestart,
  onOpenSettings,
}) => {
  return (
    <header className="w-full max-w-md flex items-center justify-between px-4 py-3 bg-slate-900/80 backdrop-blur-md rounded-2xl border border-slate-800 shadow-xl mb-4">
      <div className="flex items-center gap-4">
        <div className="flex flex-col">
          <span className="text-xs uppercase tracking-wider text-slate-400 font-semibold">Score</span>
          <span className="text-2xl font-black text-cyan-400 tracking-tight">{score}</span>
        </div>
        <div className="w-px h-8 bg-slate-800" />
        <div className="flex flex-col">
          <span className="text-xs uppercase tracking-wider text-slate-400 font-semibold">Best</span>
          <span className="text-2xl font-black text-amber-400 tracking-tight">{highScore}</span>
        </div>
      </div>

      <div className="flex items-center gap-2">
        <button
          onClick={onRestart}
          className="px-3 py-2 bg-slate-800 hover:bg-slate-700 active:scale-95 text-slate-200 text-sm font-bold rounded-xl transition border border-slate-700 shadow-sm flex items-center gap-1.5"
          title="New Game"
        >
          🔄 Restart
        </button>
        <button
          onClick={onOpenSettings}
          className="p-2 bg-slate-800 hover:bg-slate-700 active:scale-95 text-slate-200 text-sm font-bold rounded-xl transition border border-slate-700 shadow-sm"
          title="Settings"
        >
          ⚙️
        </button>
      </div>
    </header>
  );
};
