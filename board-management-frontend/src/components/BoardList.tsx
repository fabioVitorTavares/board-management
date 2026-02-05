import { useContext } from "react";
import AppDataContext from "../context/AppDataContext";
import { BoardService } from "../service/boardService";
import type { TBoard } from "../types/types";
import BoardListHeader from "./BoardListHeader";
import BoardListItem from "./BoardListItem";

export default function BoardList({ data }: { data: TBoard[] }) {
  const { refetch } = useContext(AppDataContext)!;

  async function handleCreateBoard(name: string) {
    await BoardService.create({ name, columns: [] });
    await refetch();
  }

  return (
    <div className="mx-auto w-full max-w-3xl">
      <div className="flex flex-col gap-3">
        <BoardListHeader onCreate={handleCreateBoard} />
        {data.map((item) => (
          <BoardListItem key={item.id} board={item} />
        ))}
      </div>
    </div>
  );
}
