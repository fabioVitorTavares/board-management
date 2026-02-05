import type { ApiError, TCard, MoveCardDto, UUID } from "../types/types";
import { api, extractError } from "./service";

export const CardService = {
  async create(columnId: UUID, payload: TCard): Promise<TCard> {
    try {
      const { data } = await api.post<TCard>(
        `/columns/${columnId}/cards`,
        payload
      );
      return data;
    } catch (error) {
      extractError(error as  ApiError);
    }
  },

  async update(cardId: UUID, payload: TCard): Promise<TCard> {
    try {
      const { data } = await api.put<TCard>(`/cards/${cardId}`, payload);

      return data;
    } catch (error) {
      extractError(error as ApiError);
    }
  },

  async move(cardId: UUID, payload: MoveCardDto): Promise<TCard> {
    try {
      const { data } = await api.patch<TCard>(`/cards/${cardId}/move`, payload);

      return data;
    } catch (error) {
      extractError(error as ApiError);
    }
  },

  async delete(cardId: UUID): Promise<void> {
    try {
      await api.delete(`/cards/${cardId}`);
    } catch (error) {
      extractError(error as ApiError);
    }
  },
};
