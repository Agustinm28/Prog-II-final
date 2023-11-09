import dayjs from 'dayjs/esm';

import { Language } from 'app/entities/enumerations/language.model';

import { IOrderHistory, NewOrderHistory } from './order-history.model';

export const sampleWithRequiredData: IOrderHistory = {
  id: 47374,
};

export const sampleWithPartialData: IOrderHistory = {
  id: 42291,
  stockId: 65544,
  amount: 4918,
  operationDate: dayjs('2023-11-08T13:59'),
  mode: 'Borders Ladrillo Baleares',
  state: 'Estratega strategize de',
  info: 'Juguetería',
  language: Language['SPANISH'],
};

export const sampleWithFullData: IOrderHistory = {
  id: 73205,
  clientId: 58943,
  stockId: 44162,
  operationType: false,
  price: 49518,
  amount: 52726,
  operationDate: dayjs('2023-11-08T12:10'),
  mode: 'out-of-the-box Digitalizado recíproca',
  state: 'leading-edge Mobilidad',
  info: 'Botswana Relacciones Azul',
  language: Language['SPANISH'],
};

export const sampleWithNewData: NewOrderHistory = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
