import dayjs from 'dayjs/esm';

import { Operacion } from 'app/entities/enumerations/operacion.model';
import { Modo } from 'app/entities/enumerations/modo.model';

import { ISuccessfulOrders, NewSuccessfulOrders } from './successful-orders.model';

export const sampleWithRequiredData: ISuccessfulOrders = {
  id: 66583,
};

export const sampleWithPartialData: ISuccessfulOrders = {
  id: 15751,
  accion: 'Cambridgeshire',
  operacion: Operacion['VENTA'],
  cantidad: 26501,
  modo: Modo['INICIODIA'],
};

export const sampleWithFullData: ISuccessfulOrders = {
  id: 62551,
  cliente: 57768,
  accionId: 75545,
  accion: 'transmitting',
  operacion: Operacion['VENTA'],
  precio: 63713,
  cantidad: 81257,
  fechaOperacion: dayjs('2023-11-15T11:18'),
  modo: Modo['AHORA'],
  operacionExitosa: true,
  operacionObservaciones: 'Métricas sensor Lado',
};

export const sampleWithNewData: NewSuccessfulOrders = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
