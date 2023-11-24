import dayjs from 'dayjs/esm';

import { Operacion } from 'app/entities/enumerations/operacion.model';
import { Modo } from 'app/entities/enumerations/modo.model';
import { Estado } from 'app/entities/enumerations/estado.model';

import { IOrder, NewOrder } from './order.model';

export const sampleWithRequiredData: IOrder = {
  id: 47761,
};

export const sampleWithPartialData: IOrder = {
  id: 68170,
  accionId: 93694,
  accion: 'Algodón Account',
  operacion: Operacion['COMPRA'],
  fechaOperacion: dayjs('2023-11-09T13:52'),
  modo: Modo['INICIODIA'],
};

export const sampleWithFullData: IOrder = {
  id: 80079,
  cliente: 12831,
  accionId: 73312,
  accion: 'markets',
  operacion: Operacion['COMPRA'],
  precio: 32103,
  cantidad: 92419,
  fechaOperacion: dayjs('2023-11-09T20:27'),
  modo: Modo['AHORA'],
  estado: Estado['PENDIENTE'],
};

export const sampleWithNewData: NewOrder = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
