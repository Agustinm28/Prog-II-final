import dayjs from 'dayjs/esm';

import { Operacion } from 'app/entities/enumerations/operacion.model';
import { Modo } from 'app/entities/enumerations/modo.model';

import { ISuccessfulOrders, NewSuccessfulOrders } from './successful-orders.model';

export const sampleWithRequiredData: ISuccessfulOrders = {
  id: 66583,
};

export const sampleWithPartialData: ISuccessfulOrders = {
  id: 2664,
  accion: 'Contabilidad la',
  operacion: Operacion['VENTA'],
  cantidad: 75545,
  modo: Modo['INICIODIA'],
};

export const sampleWithFullData: ISuccessfulOrders = {
  id: 82322,
  cliente: 86173,
  accionId: 41013,
  accion: 'invoice synthesize',
  operacion: Operacion['VENTA'],
  precio: 98234,
  cantidad: 52506,
  fechaOperacion: dayjs('2023-11-15T04:40'),
  modo: Modo['AHORA'],
  operacionExitosa: true,
  operacionObservaciones: 'web-readiness Buckinghamshire',
  estado: true,
};

export const sampleWithNewData: NewSuccessfulOrders = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
