import dayjs from 'dayjs/esm';
import { Operacion } from 'app/entities/enumerations/operacion.model';
import { Modo } from 'app/entities/enumerations/modo.model';

export interface IOrder {
  id: number;
  cliente?: number | null;
  accionId?: number | null;
  accion?: string | null;
  operacion?: Operacion | null;
  precio?: number | null;
  cantidad?: number | null;
  fechaOperacion?: dayjs.Dayjs | null;
  modo?: Modo | null;
}

export type NewOrder = Omit<IOrder, 'id'> & { id: null };
