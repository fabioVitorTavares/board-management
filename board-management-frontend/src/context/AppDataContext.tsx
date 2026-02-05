import { createContext, useState } from "react";
import type { TBoard } from "../types/types";
import { useBoads } from "../hooks/useBoards";

type AppDataContextType = {
  boardSelected: TBoard | null;
  setBoardSelected: React.Dispatch<React.SetStateAction<TBoard | null>>;
  boards: TBoard[]; 
  refetch: () => Promise<void>;
  fetchBoardById: (id: string) => Promise<TBoard | undefined>
};

const AppDataContext = createContext<AppDataContextType | null>(null);

export function AppDataContextProvider({ children }: { children: React.ReactNode }) {

  const { boards, refetch, fetchBoardById } = useBoads();
  const [boardSelected, setBoardSelected] = useState<TBoard | null>(null);
  
  return (
    <AppDataContext.Provider value={{ boardSelected, setBoardSelected, boards, refetch, fetchBoardById }}>
      {children}
    </AppDataContext.Provider>
  );
}

export default AppDataContext;