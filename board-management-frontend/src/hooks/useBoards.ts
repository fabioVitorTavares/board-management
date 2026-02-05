import { useCallback, useEffect, useState } from "react";
import { BoardService } from "../service/boardService";
import type { TBoard, UUID } from "../types/types";

export function useBoads() {
  const [boards, setBoards] = useState<TBoard[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    setIsLoading(true);
    setError(null);

    try {
      const boards = await BoardService.getAll();

      setBoards(boards);
    } catch (err) {
      console.log("Failed to fetch boards:", err);
      setError("Erro ao buscar boards");
    } finally {
      setIsLoading(false);
    }
  }, []);

  async function fetchBoardById(id: UUID) {
    setIsLoading(true);
    setError(null);

    try {
      const board = await BoardService.getById(id);
      return board;
    } catch (err) {
      console.log("Failed to fetch board by id:", err);
      setError("Erro ao buscar board");
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    let active = true;

    if (active) {
      fetchData();
    }

    return () => {
      active = false;
    };
  }, [fetchData]);

  return {
    boards,
    isLoading,
    error,
    refetch: fetchData,
    fetchBoardById,
  };
}
