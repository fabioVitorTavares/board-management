import axios from "axios";
import type { ApiError } from "../types/types";

export const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

export function extractError(error: ApiError): never {
  const message =
    error?.message ??
    "Erro inesperado";
  throw new Error(message);
}




