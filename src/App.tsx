import { useState } from 'react';
import type { BoardState, TrayState, TrayPiece } from './types';
import {
  createEmptyBoard,
  canPlacePiece,
  placePieceOnBoard,
  checkAndClearLines,
  countPieceBlocks,
  canAnyPieceFit,
  generateInitialTray,
} from './engine';
import { getStoredHighScore, saveHighScore } from './storage';
import { Header } from './components/Header';
import { Board } from './components/Board';
import { Tray } from './components/Tray';
import { GameOverModal } from './components/GameOverModal';
import { SettingsModal } from './components/SettingsModal';

export function App() {
  const [board, setBoard] = useState<BoardState>(createEmptyBoard);
  const [tray, setTray] = useState<TrayState>(generateInitialTray);
  const [score, setScore] = useState<number>(0);
  const [highScore, setHighScore] = useState<number>(getStoredHighScore);
  const [selectedPiece, setSelectedPiece] = useState<TrayPiece | null>(null);
  const [gameOver, setGameOver] = useState<boolean>(false);
  const [settingsOpen, setSettingsOpen] = useState<boolean>(false);

  // Restart game
  const handleRestart = () => {
    const newBoard = createEmptyBoard();
    const newTray = generateInitialTray();
    setBoard(newBoard);
    setTray(newTray);
    setScore(0);
    setSelectedPiece(null);
    setGameOver(false);
  };

  // Select piece from tray
  const handleSelectPiece = (piece: TrayPiece) => {
    if (!piece || gameOver) return;
    if (selectedPiece?.instanceId === piece.instanceId) {
      setSelectedPiece(null); // deselect on second tap
    } else {
      setSelectedPiece(piece);
    }
  };

  // Handle cell click on board to place selected piece
  const handleCellClick = (row: number, col: number) => {
    if (!selectedPiece || gameOver) return;

    const shape = selectedPiece.definition.shape;
    const color = selectedPiece.definition.color;

    if (!canPlacePiece(board, shape, row, col)) {
      return; // Invalid placement
    }

    // 1. Place piece
    let updatedBoard = placePieceOnBoard(board, shape, color, row, col);
    const placedBlocks = countPieceBlocks(shape);
    let earnedScore = placedBlocks;

    // 2. Check and clear lines
    const clearResult = checkAndClearLines(updatedBoard);
    updatedBoard = clearResult.newBoard;

    if (clearResult.totalCleared > 0) {
      const lineBonus = clearResult.totalCleared * 10 * (clearResult.rowsCleared + clearResult.colsCleared);
      earnedScore += lineBonus;
    }

    const newScore = score + earnedScore;
    setScore(newScore);

    if (newScore > highScore) {
      setHighScore(newScore);
      saveHighScore(newScore);
    }

    // 3. Update tray (remove placed piece)
    const updatedTray: TrayState = tray.map(p =>
      p?.instanceId === selectedPiece.instanceId ? null : p
    ) as TrayState;

    setSelectedPiece(null);

    // 4. Check if tray is empty -> refill
    const allEmpty = updatedTray.every(p => p === null);
    const finalTray = allEmpty ? generateInitialTray() : updatedTray;

    setBoard(updatedBoard);
    setTray(finalTray);

    // 5. Check game over
    if (canAnyPieceFit(updatedBoard, finalTray) === false) {
      setGameOver(true);
    }
  };

  const handleResetHighScore = () => {
    localStorage.removeItem('block_puzzle_high_score');
    setHighScore(0);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col items-center justify-between p-4 selection:bg-cyan-500 selection:text-white">
      <div className="w-full flex flex-col items-center max-w-md my-auto">
        <h1 className="text-xl font-black text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-blue-500 tracking-wider uppercase mb-2">
          Block Puzzle Arcade
        </h1>

        <Header
          score={score}
          highScore={highScore}
          onRestart={handleRestart}
          onOpenSettings={() => setSettingsOpen(true)}
        />

        <Board
          board={board}
          selectedPieceShape={selectedPiece ? selectedPiece.definition.shape : null}
          selectedPieceColor={selectedPiece ? selectedPiece.definition.color : null}
          onCellClick={handleCellClick}
        />

        <Tray
          tray={tray}
          selectedInstanceId={selectedPiece ? selectedPiece.instanceId : null}
          onSelectPiece={handleSelectPiece}
        />
      </div>

      {gameOver && (
        <GameOverModal
          score={score}
          highScore={highScore}
          onRestart={handleRestart}
        />
      )}

      {settingsOpen && (
        <SettingsModal
          onClose={() => setSettingsOpen(false)}
          onResetHighScore={handleResetHighScore}
        />
      )}
    </div>
  );
}

export default App;
