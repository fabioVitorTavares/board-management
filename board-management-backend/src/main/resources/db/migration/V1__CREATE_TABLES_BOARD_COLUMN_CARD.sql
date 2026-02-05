CREATE TABLE board (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE columns (
  id UUID PRIMARY KEY,
  name     VARCHAR(255) NOT NULL,
  board_id UUID NOT NULL,
  CONSTRAINT fk_column_board
    FOREIGN KEY (board_id) REFERENCES board(id)
    ON DELETE CASCADE
);

CREATE TABLE card (
  id UUID PRIMARY KEY,
  title       VARCHAR(255) NOT NULL,
  description TEXT,
  column_id   UUID NOT NULL,
  CONSTRAINT fk_card_column
    FOREIGN KEY (column_id) REFERENCES columns(id)
    ON DELETE CASCADE
);

CREATE INDEX idx_column_board_id ON columns(board_id);
CREATE INDEX idx_card_column_id  ON card(column_id);