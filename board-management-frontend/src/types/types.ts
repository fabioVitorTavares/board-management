export type UUID = string;

export type TBoard  = {
  id?: UUID;
  name: string;
  columns: TColumn[];
}

export type TColumn = {
  id?: UUID;
  name: string;
  cards: TCard[];
  boardId?: UUID;
}

export type TCard = {
  id?: UUID;
  title: string;
  description?: string | null;
  columnId?: UUID;
}

export type MoveCardDto  = {
  newColumnId: UUID;
}

export type ApiError = {
  status: number;
  message: string;
}
