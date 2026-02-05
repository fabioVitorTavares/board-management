import { useContext, useEffect, useRef, useState } from "react";
import { CardService } from "../service/cardService";
import type { TColumn } from "../types/types";
import Card from "./Card";
import AppDataContext from "../context/AppDataContext";

type DragPayload =
  | { type: "column"; columnId: string }
  | { type: "card"; cardId: string; fromColumnId: string };

const MIME = "application/x-kanban";

export default function Column({ column }: { column: TColumn }) {
  const { setBoardSelected, fetchBoardById, refetch } = useContext(AppDataContext)!;
  const [adding, setAdding] = useState(false);
  const [cardTitle, setCardTitle] = useState("");
  const [cardDescription, setCardDescription] = useState("");

  const inputRef = useRef<HTMLInputElement>(null);
  const addRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    function handleClickOutside(e: MouseEvent) {
      if (
        adding &&
        addRef.current &&
        !addRef.current.contains(e.target as Node)
      ) {
        setAdding(false);
        setCardTitle("");
      }
    }

    if (adding) {
      document.addEventListener("mousedown", handleClickOutside);
    }

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [adding]);

  async function handleCreateCard() {
    const title = cardTitle.trim();
    const description = cardDescription.trim();
    if (!title || !column.id) return;

    await CardService.create(column.id, {
      title,
      description,
    });

    await updateBoard();

    setAdding(false);
    setCardTitle("");
    setCardDescription("");
  }

  async function updateBoard() {
    await refetch();
    if (!column.boardId) return;
    const updatedBoard = await fetchBoardById(column.boardId);
    if (updatedBoard && setBoardSelected) {
      setBoardSelected(updatedBoard);
    }
  }

  async function onDropCardInColunm(e: React.DragEvent<HTMLDivElement>) {
    e.preventDefault();
    const raw = e.dataTransfer.getData(MIME);
    if (!raw) return;

    const payload: DragPayload = JSON.parse(raw);

    if (payload?.type === "card" && payload?.fromColumnId === column?.id) {
      return;
    }

    if (payload?.type === "card" && payload?.cardId && column?.id) {
      await CardService.move(payload?.cardId, { newColumnId: column?.id });
      await updateBoard();
    }
  }

  return (
    <div className="w-80 shrink-0 rounded-xl border border-zinc-200 bg-zinc-50 p-3 shadow-sm h-auto">
      <div className="mb-3 flex cursor-grab items-center justify-between rounded-lg px-2 py-2 active:cursor-grabbing">
        <h3 className="text-sm font-semibold text-zinc-900">{column.name}</h3>
        <span className="text-xs text-zinc-500">{column.cards.length}</span>
      </div>

      <div
        onDragOver={(e) => e.preventDefault()}
        onDrop={onDropCardInColunm}
        className="w-full h-full mt-2 p-3 text-center text-xs text-zinc-500"
        draggable
      >
        <div className="flex flex-col gap-2">
          {column.cards.map((card) => (
            <Card key={card.id} card={card} />
          ))}
        </div>
        <div ref={addRef} className="mt-2">
          {!adding ? (
            <button
              type="button"
              onClick={(e) => {
                e.stopPropagation();
                setAdding(true);
                setTimeout(() => inputRef.current?.focus(), 0);
              }}
              className="w-full rounded-lg px-2 py-2 text-left text-sm text-zinc-500 hover:bg-zinc-100 hover:text-zinc-900"
            >
              + Adicionar card
            </button>
          ) : (
            <div className="flex flex-col gap-2">
              <input
                ref={inputRef}
                value={cardTitle}
                onChange={(e) => setCardTitle(e.target.value)}
                placeholder="Título do card"
                className="w-full rounded-md border border-zinc-300 px-2 py-1 text-sm outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900"
                onKeyDown={(e) => {
                  if (e.key === "Escape") {
                    setAdding(false);
                    setCardTitle("");
                  }
                }}
                autoFocus
              />
              <input
                ref={inputRef}
                value={cardDescription}
                onChange={(e) => setCardDescription(e.target.value)}
                placeholder="Descrição do card"
                className="w-full rounded-md border border-zinc-300 px-2 py-1 text-sm outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900"
                onKeyDown={(e) => {
                  if (e.key === "Enter") handleCreateCard();
                  if (e.key === "Escape") {
                    setAdding(false);
                    setCardTitle("");
                  }
                }}
                autoFocus
              />

              <button
                type="button"
                onClick={handleCreateCard}
                disabled={!cardTitle.trim()}
                className="rounded-md bg-blue-600 px-3 py-1 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
              >
                Add
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
