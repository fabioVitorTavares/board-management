import type { TBoard } from "../types/types";
import Column from "./Column";

export default function Board({ board }: { board: TBoard }) {
  const columns = board.columns;

  return (
    <div className="h-full w-full">
      <div className="mb-4">
        <h2 className="text-xl font-semibold text-zinc-900">{board.name}</h2>
      </div>

      <div className="flex gap-4 overflow-x-auto pb-4">
        {columns.map((col) => (
          <Column key={col.id} column={col} />
        ))}
      </div>
    </div>
  );
}
