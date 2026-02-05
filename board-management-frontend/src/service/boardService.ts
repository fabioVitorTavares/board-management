import type { ApiError, TBoard, UUID } from "../types/types";
import { api, extractError } from "./service";

export const BoardService = {
  async create(payload: TBoard): Promise<TBoard> {
    try {
      const { data } = await api.post<TBoard>("/boards", payload);
      return data;
    } catch (error) {
      extractError(error as ApiError);
    }
  },

  async getAll(): Promise<TBoard[]> {
    try {
      const { data } = await api.get<TBoard[]>("/boards");
      return data;
    } catch (error) {
      extractError(error as   ApiError);
    }
  },

  async getById(boardId: UUID): Promise<TBoard> {
    try {
      const { data } = await api.get<TBoard>(`/boards/${boardId}`);
      return data;
    } catch (error) {
      extractError(error as   ApiError);
    }
  },

  async getFull(boardId: UUID): Promise<TBoard> {
    try {
      const { data } = await api.get<TBoard>(`/boards/${boardId}/full`);
      return data;
    } catch (error) {
      extractError(error as ApiError);
    }
  },

  async delete(boardId: UUID): Promise<void> {
    try {
      await api.delete(`/boards/${boardId}`);
    } catch (error) {
      extractError(error as ApiError);
    }
  },
};