export interface IClientStocks {
  id: number;
  clientId?: number | null;
  stockCode?: string | null;
  stockAmount?: number | null;
}

export type NewClientStocks = Omit<IClientStocks, 'id'> & { id: null };
