import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App.tsx";
import { AppDataContextProvider } from "./context/AppDataContext.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <AppDataContextProvider>
      <App />
    </AppDataContextProvider>
  </StrictMode>,
);
