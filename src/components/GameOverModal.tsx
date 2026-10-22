import React from 'react';

interface GameOverModalProps {
  score: number;
  highScore: number;
  onRestart: () => void;
}

export const GameOverModal: React.FC<GameOverModalProps> = ({
  score,
  highScore,
  onRestart,
}) => {
  const isNewBest = score >= highScore && score > 0;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-sm p-6 bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl text-center flex flex-col items-center gap-5">
        <div className="w-16 h-16 bg-red-500/20 text-red-400 rounded-2xl flex items-center justify-center text-3xl font-bold border border-red-500/30">
          💀
        </div>

        <div>
          <h2 className="text-2xl font-black text-white tracking-tight">Game Over</h2>
          <p className="text-sm text-slate-400 mt-1">No more moves available!</p>
        </div>

        {isNewBest && (
          <div className="px-4 py-1.5 bg-amber-500/20 border border-amber-500/40 rounded-full text-amber-300 text-xs font-bold tracking-wide uppercase animate-pulse">
            🎉 New High Score! 🎉
          </div>
        )}

        <div className="w-full grid grid-cols-2 gap-3 p-4 bg-slate-800/50 rounded-2xl border border-slate-700/50">
          <div className="flex flex-col">
            <span className="text-xs uppercase text-slate-400 font-semibold">Final Score</span>
            <span className="text-2xl font-black text-cyan-400">{score}</span>
          </div>
          <div className="flex flex-col">
            <span className="text-xs uppercase text-slate-400 font-semibold">Best Score</span>
            <span className="text-2xl font-black text-amber-400">{highScore}</span>
          </div>
        </div>

        <button
          onClick={onRestart}
          className="w-full py-3.5 bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-400 hover:to-blue-500 active:scale-98 text-white font-black rounded-2xl shadow-lg shadow-cyan-500/25 transition duration-150"
        >
          Play Again
        </button>
      </div>
    </div>
  );
};
