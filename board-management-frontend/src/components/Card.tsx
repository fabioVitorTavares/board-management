import { useContext, useState } from "react";
import type { TCard } from "../types/types";
import { CardService } from "../service/cardService";
import AppDataContext from "../context/AppDataContext";

const MIME = "application/x-kanban";

export default function Card({ card }: { card: TCard }) {
  const { boardSelected, setBoardSelected, fetchBoardById } =
    useContext(AppDataContext)!;
  const [hidden, setHidden] = useState(false);
  function onDragStartCard(e: React.DragEvent<HTMLDivElement>) {
    setHidden(true);

    const cardEl = e.currentTarget;
    const ghost = cardEl.cloneNode(true) as HTMLElement;

    const rect = cardEl.getBoundingClientRect();
    ghost.style.width = `${rect.width}px`;
    ghost.style.height = `${rect.height}px`;

    ghost.style.pointerEvents = "none";

    ghost.style.position = "fixed";
    ghost.style.top = "-1000px";
    ghost.style.left = "-1000px";
    ghost.style.margin = "0";

    ghost.style.background = "white";

    document.body.appendChild(ghost);

    e.dataTransfer.setDragImage(ghost, 20, 20);

    requestAnimationFrame(() => {
      requestAnimationFrame(() => {
        document.body.removeChild(ghost);
      });
    });

    e.dataTransfer.setData(
      MIME,
      JSON.stringify({
        type: "card",
        cardId: card.id,
        fromColumnId: card.columnId,
      }),
    );
  }

  function onDragEnd(e: React.DragEvent<HTMLDivElement>) {
    setTimeout(() => setHidden(false), 100);
    console.log("[event]: ", e);
  }

  function onDragEnter(e: React.DragEvent<HTMLDivElement>) {
    e.preventDefault();
  }

  async function handleDeleteCard(e: React.MouseEvent) {
    e.stopPropagation();
    e.preventDefault();

    if (!card.id) return;

    await CardService.delete(card.id);
    await updateBoard();
  }

  async function updateBoard() {
    if (!boardSelected?.id) return;
    const updatedBoard = await fetchBoardById(boardSelected.id);
    if (updatedBoard && setBoardSelected) {
      setBoardSelected(updatedBoard);
    }
  }

  return (
    <div
      draggable
      onDragStart={onDragStartCard}
      onDragEnd={onDragEnd}
      onDragEnter={onDragEnter}
      className={`
      relative group
      cursor-grab rounded-lg border border-zinc-200 bg-white p-3 shadow-sm
      transition hover:shadow-md active:cursor-grabbing
      ${hidden ? "opacity-0" : "opacity-100"}
    `}
    >
      <button
        type="button"
        onClick={handleDeleteCard}
        className="
          absolute right-2 top-2
          opacity-0 group-hover:opacity-100
          rounded p-1 text-zinc-400
          hover:bg-red-50 hover:text-red-600
          transition
        "
        title="Excluir card"
        aria-label="Excluir card"
      >
        🗑️
      </button>
      <div className="text-sm font-semibold text-zinc-900">{card.title}</div>
      {card.description ? (
        <div className="mt-1 text-xs text-zinc-600 line-clamp-2">
          {card.description}
        </div>
      ) : null}
    </div>
  );
}
