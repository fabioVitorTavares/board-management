import { useContext, useEffect, useRef, useState } from "react";
import type { TBoard } from "../types/types";
import AppDataContext from "../context/AppDataContext";
import { ColumnService } from "../service/columnService";

export default function BoardListItem({ board }: { board: TBoard }) {
  const numberOfCards = board.columns.reduce(
    (acc, column) => acc + column.cards.length,
    0,
  );

  const cardRef = useRef<HTMLDivElement>(null);
  const { boardSelected, setBoardSelected, fetchBoardById } =
    useContext(AppDataContext)!;

  const isSelected = boardSelected?.id === board.id;

  const [adding, setAdding] = useState(false);
  const [columnName, setColumnName] = useState("");

  function handleSelectBoard() {
    setBoardSelected(board);
  }

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (cardRef.current && !cardRef.current.contains(event.target as Node)) {
        setAdding(false);
      }
    }

    if (adding) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [adding]);

  async function handleCreateColumn() {
    const name = columnName.trim();
    if (!name || !board?.id) return;

    await ColumnService.create(board.id, { name, cards: [] });
    const currentBoard =  await fetchBoardById(board.id);
    if (currentBoard) {
      setBoardSelected(currentBoard);
    }

    setColumnName("");
    setAdding(false);
  }

  const baseClass =
    "group flex items-start justify-between gap-4 rounded-xl p-4 transition cursor-pointer";

  const selectedClass = "border-2 border-blue-600 bg-blue-50 shadow-md";

  const normalClass =
    "border border-zinc-200 bg-white shadow-sm hover:border-zinc-300 hover:shadow-md";

  return (
    <div
      ref={cardRef}
      onClick={handleSelectBoard}
      className={`${baseClass} ${isSelected ? selectedClass : normalClass}`}
    >
      <div className="min-w-0 flex-1">
        <div className="flex items-start justify-between gap-3">
          <h2 className="truncate text-lg font-semibold text-zinc-900">
            {board.name}
          </h2>

          {/* botão discreto */}
          <button
            type="button"
            onClick={() => {
              setAdding((v) => !v);
              setColumnName("");
            }}
            className="opacity-0 group-hover:opacity-100 rounded-md border border-zinc-200 bg-white px-2 py-1 text-xs text-zinc-600 shadow-sm transition hover:bg-zinc-50 hover:text-zinc-900"
            aria-label="Adicionar coluna"
            title="Adicionar coluna"
          >
            + coluna
          </button>
        </div>

        <div className="mt-2 flex flex-wrap items-center gap-2 text-sm text-zinc-600">
          <span className="rounded-full bg-zinc-100 px-3 py-1">
            Columns:{" "}
            <span className="font-medium text-zinc-900">
              {board.columns.length}
            </span>
          </span>

          <span className="rounded-full bg-zinc-100 px-3 py-1">
            Cards:{" "}
            <span className="font-medium text-zinc-900">{numberOfCards}</span>
          </span>
        </div>

        {/* input inline para criar coluna */}
        {adding && (
          <div className="mt-3 flex gap-2" onClick={(e) => e.stopPropagation()}>
            <input
              value={columnName}
              onChange={(e) => setColumnName(e.target.value)}
              placeholder="Nome da coluna"
              className="w-full rounded-lg border border-zinc-300 px-3 py-2 text-sm outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900"
              autoFocus
              onKeyDown={(e) => {
                if (e.key === "Enter") handleCreateColumn();
                if (e.key === "Escape") {
                  setAdding(false);
                  setColumnName("");
                }
              }}
            />

            <button
              type="button"
              onClick={handleCreateColumn}
              disabled={!columnName.trim()}
              className="rounded-lg bg-blue-600 px-3 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Add
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
