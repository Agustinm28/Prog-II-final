import dayjs from 'dayjs/esm';
import { Modo } from 'app/entities/enumerations/modo.model';
import { Estado } from 'app/entities/enumerations/estado.model';

export interface IOrderHistory {
  id: number;
  cliente?: number | null;
  accionId?: number | null;
  accion?: string | null;
  operacion?: boolean | null;
  cantidad?: number | null;
  precio?: number | null;
  fechaOperacion?: dayjs.Dayjs | null;
  modo?: Modo | null;
  estado?: Estado | null;
  operacionObservaciones?: string | null;
  fechaEjecucion?: dayjs.Dayjs | null;
}

export type NewOrderHistory = Omit<IOrderHistory, 'id'> & { id: null };
