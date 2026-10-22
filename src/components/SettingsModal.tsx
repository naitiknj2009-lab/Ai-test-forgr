import React from 'react';

interface SettingsModalProps {
  onClose: () => void;
  onResetHighScore: () => void;
}

export const SettingsModal: React.FC<SettingsModalProps> = ({
  onClose,
  onResetHighScore,
}) => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm animate-fade-in">
      <div className="w-full max-w-sm p-6 bg-slate-900 border border-slate-800 rounded-3xl shadow-2xl flex flex-col gap-5">
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-black text-white tracking-tight">Settings</h2>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-xl bg-slate-800 text-slate-300 font-bold hover:bg-slate-700 flex items-center justify-center transition"
          >
            ✕
          </button>
        </div>

        <div className="flex flex-col gap-3">
          <div className="p-4 bg-slate-800/50 rounded-2xl border border-slate-700/50 flex items-center justify-between">
            <div>
              <h3 className="text-sm font-bold text-slate-200">Reset High Score</h3>
              <p className="text-xs text-slate-400">Clear saved high score from storage</p>
            </div>
            <button
              onClick={() => {
                if (window.confirm('Are you sure you want to reset your high score?')) {
                  onResetHighScore();
                }
              }}
              className="px-3 py-1.5 bg-red-500/20 hover:bg-red-500/30 text-red-400 font-bold text-xs rounded-xl border border-red-500/30 transition"
            >
              Reset
            </button>
          </div>

          <div className="p-4 bg-slate-800/50 rounded-2xl border border-slate-700/50">
            <h3 className="text-sm font-bold text-slate-200 mb-1">How to Play</h3>
            <ul className="text-xs text-slate-400 space-y-1 list-disc list-inside">
              <li>Select a piece from the bottom tray.</li>
              <li>Tap any cell on the board to place it.</li>
              <li>Complete rows or columns to clear them.</li>
              <li>Game over when no active pieces can fit.</li>
            </ul>
          </div>
        </div>

        <button
          onClick={onClose}
          className="w-full py-3 bg-slate-800 hover:bg-slate-700 text-white font-bold rounded-2xl transition"
        >
          Close
        </button>
      </div>
    </div>
  );
};
