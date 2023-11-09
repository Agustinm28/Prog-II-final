import dayjs from 'dayjs/esm';
import { Language } from 'app/entities/enumerations/language.model';

export interface IOrderHistory {
  id: number;
  clientId?: number | null;
  stockId?: number | null;
  operationType?: boolean | null;
  price?: number | null;
  amount?: number | null;
  operationDate?: dayjs.Dayjs | null;
  mode?: string | null;
  state?: string | null;
  info?: string | null;
  language?: Language | null;
}

export type NewOrderHistory = Omit<IOrderHistory, 'id'> & { id: null };
