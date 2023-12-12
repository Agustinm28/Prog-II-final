import dayjs from 'dayjs/esm';

import { Operacion } from 'app/entities/enumerations/operacion.model';
import { Modo } from 'app/entities/enumerations/modo.model';
import { Estado } from 'app/entities/enumerations/estado.model';

import { IOrderHistory, NewOrderHistory } from './order-history.model';

export const sampleWithRequiredData: IOrderHistory = {
  id: 47374,
  cliente: 45404,
  accionId: 95903,
  accion: 'estructura',
  operacion: Operacion['VENTA'],
  modo: Modo['AHORA'],
};

export const sampleWithPartialData: IOrderHistory = {
  id: 49781,
  cliente: 57635,
  accionId: 91983,
  accion: 'Comunidad',
  operacion: Operacion['COMPRA'],
  cantidad: 56266,
  precio: 20416,
  modo: Modo['AHORA'],
  estado: Estado['FALLIDA'],
  fechaEjecucion: dayjs('2023-11-26T15:59'),
};

export const sampleWithFullData: IOrderHistory = {
  id: 98467,
  cliente: 70204,
  accionId: 37363,
  accion: 'Joyería Baleares',
  operacion: Operacion['COMPRA'],
  cantidad: 22879,
  precio: 94218,
  fechaOperacion: dayjs('2023-11-26T21:49'),
  modo: Modo['AHORA'],
  estado: Estado['EXITOSA'],
  reportada: false,
  operacionObservaciones: 'Kazajistan out-of-the-box',
  fechaEjecucion: dayjs('2023-11-27T09:13'),
};

export const sampleWithNewData: NewOrderHistory = {
  cliente: 17805,
  accionId: 28147,
  accion: 'digital',
  operacion: Operacion['COMPRA'],
  modo: Modo['PRINCIPIODIA'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
