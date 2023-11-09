export interface IClientStocks {
  id: number;
  stockAmount?: number | null;
}

export type NewClientStocks = Omit<IClientStocks, 'id'> & { id: null };
