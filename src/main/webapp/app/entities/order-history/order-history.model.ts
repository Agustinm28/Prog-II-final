import dayjs from 'dayjs/esm';
import { Language } from 'app/entities/enumerations/language.model';

export interface IOrderHistory {
  id: number;
  clientId?: number | null;
  stockCode?: string | null;
  operationType?: boolean | null;
  price?: number | null;
  amount?: number | null;
  creationDate?: dayjs.Dayjs | null;
  executionDate?: dayjs.Dayjs | null;
  mode?: string | null;
  state?: string | null;
  info?: string | null;
  language?: Language | null;
}

export type NewOrderHistory = Omit<IOrderHistory, 'id'> & { id: null };
