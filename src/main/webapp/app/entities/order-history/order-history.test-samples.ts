import dayjs from 'dayjs/esm';

import { Modo } from 'app/entities/enumerations/modo.model';
import { Estado } from 'app/entities/enumerations/estado.model';

import { IOrderHistory, NewOrderHistory } from './order-history.model';

export const sampleWithRequiredData: IOrderHistory = {
  id: 47374,
  cliente: 45404,
  accionId: 95903,
  accion: 'estructura',
  operacion: true,
  modo: Modo['AHORA'],
};

export const sampleWithPartialData: IOrderHistory = {
  id: 79451,
  cliente: 49781,
  accionId: 57635,
  accion: 'Ladrillo Baleares parse',
  operacion: true,
  cantidad: 37363,
  precio: 42465,
  modo: Modo['PRINCIODIA'],
  estado: Estado['EXITOSA'],
};

export const sampleWithFullData: IOrderHistory = {
  id: 44458,
  cliente: 58097,
  accionId: 19766,
  accion: 'communities',
  operacion: true,
  cantidad: 58943,
  precio: 44162,
  fechaOperacion: dayjs('2023-11-27T08:41'),
  modo: Modo['AHORA'],
  estado: Estado['FALLIDA'],
  operacionObservaciones: 'array Canarias',
  fechaEjecucion: dayjs('2023-11-27T08:37'),
};

export const sampleWithNewData: NewOrderHistory = {
  cliente: 28936,
  accionId: 80130,
  accion: 'Berkshire',
  operacion: false,
  modo: Modo['FINDIA'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
