import { useContext, useEffect } from "react";
import Board from "./components/Board";
import BoardList from "./components/BoardList";
import AppDataContext from "./context/AppDataContext";

export default function App() {
  const { boardSelected, setBoardSelected, boards } =
    useContext(AppDataContext)!;

  useEffect(() => {
    setBoardSelected(boards[0]);
  }, [boards]);

  return (
    <div className="flex h-screen bg-zinc-100">
      {/* Sidebar */}
      <aside className="w-72 shrink-0 border-r border-zinc-200 bg-white">
        <div className="flex h-full flex-col">
          {/* Header da sidebar */}
          <div className="border-b border-zinc-200 px-4 py-3">
            <h1 className="text-lg font-semibold text-zinc-900">Boards</h1>
          </div>

          {/* Lista */}
          <div className="flex-1 overflow-y-auto p-3">
            <BoardList data={boards} />
          </div>
        </div>
      </aside>

      {/* Conteúdo principal */}
      <main className="flex-1 p-6">
        {!boardSelected?.id && (
          <div className="flex h-full items-center justify-center text-zinc-400">
            Selecione um board
          </div>
        )}
        {boardSelected?.id && <Board board={boardSelected} />}
      </main>
    </div>
  );
}
