import type { ApiError, TColumn, UUID } from "../types/types";
import { api, extractError } from "./service";

export const ColumnService = {
  async create(boardId: UUID, payload: TColumn): Promise<TColumn> {
    try {
      const { data } = await api.post<TColumn>(
        `/boards/${boardId}/columns`,
        payload
      );
      return data;
    } catch (error) {
      extractError(error as ApiError);
    }
  },
};
