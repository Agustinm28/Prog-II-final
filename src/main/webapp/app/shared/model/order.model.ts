import dayjs from 'dayjs';

export interface IOrder {
  id?: number;
  cliente?: number | null;
  accionId?: number | null;
  accion?: string | null;
  operacion?: string | null;
  precio?: number | null;
  cantidad?: number | null;
  fechaOperacion?: string | null;
  modo?: string | null;
}

export const defaultValue: Readonly<IOrder> = {};
