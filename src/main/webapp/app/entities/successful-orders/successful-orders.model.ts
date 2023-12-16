import dayjs from 'dayjs/esm';
import { Operacion } from 'app/entities/enumerations/operacion.model';
import { Modo } from 'app/entities/enumerations/modo.model';

export interface ISuccessfulOrders {
  id: number;
  cliente?: number | null;
  accionId?: number | null;
  accion?: string | null;
  operacion?: Operacion | null;
  precio?: number | null;
  cantidad?: number | null;
  fechaOperacion?: dayjs.Dayjs | null;
  modo?: Modo | null;
  operacionExitosa?: boolean | null;
  operacionObservaciones?: string | null;
  estado?: boolean | null;
}

export type NewSuccessfulOrders = Omit<ISuccessfulOrders, 'id'> & { id: null };
