import { useState } from "react";

export default function BoardListHeader({
  onCreate,
}: {
  onCreate: (name: string) => Promise<void> | void;
}) {
  const [open, setOpen] = useState(false);
  const [name, setName] = useState("");

  async function handleCreate() {
    const trimmed = name.trim();
    if (!trimmed) return;

    await onCreate(trimmed);
    setName("");
    setOpen(false);
  }

  return (
    <div className="mb-3 rounded-xl border border-zinc-200 bg-white p-3 shadow-sm">
      {!open ? (
        <button
          type="button"
          onClick={() => setOpen(true)}
          className="w-full rounded-lg bg-zinc-900 px-3 py-2 text-sm font-medium text-white hover:bg-zinc-800"
        >
          + Novo board
        </button>
      ) : (
        <div className="flex flex-col gap-2">
          <input
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="Nome do board"
            className="w-full rounded-lg border border-zinc-300 px-3 py-2 text-sm outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900"
            autoFocus
            onKeyDown={(e) => {
              if (e.key === "Enter") handleCreate();
              if (e.key === "Escape") {
                setOpen(false);
                setName("");
              }
            }}
          />

          <div className="flex gap-2">
            <button
              type="button"
              onClick={handleCreate}
              disabled={!name.trim()}
              className="flex-1 rounded-lg bg-blue-600 px-3 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Criar
            </button>

            <button
              type="button"
              onClick={() => {
                setOpen(false);
                setName("");
              }}
              className="flex-1 rounded-lg border border-zinc-300 bg-white px-3 py-2 text-sm font-medium text-zinc-900 hover:bg-zinc-50"
            >
              Cancelar
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
