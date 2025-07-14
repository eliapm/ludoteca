import type { Game } from "./Game";
import type { Client } from "./Client";


export interface Loan {
    id: string;
    game?: Game;
    client?: Client;
    startDate: string;
    endDate: string;
}   

export interface LoanResponse {
    content: Loan[];
    totalElements: number;
}