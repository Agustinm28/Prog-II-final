import dayjs from 'dayjs/esm';

import { Language } from 'app/entities/enumerations/language.model';

import { IOrderHistory, NewOrderHistory } from './order-history.model';

export const sampleWithRequiredData: IOrderHistory = {
  id: 47374,
};

export const sampleWithPartialData: IOrderHistory = {
  id: 65544,
  stockCode: 'schemas',
  amount: 57635,
  creationDate: dayjs('2023-11-08T01:45'),
  executionDate: dayjs('2023-11-08T22:12'),
  mode: 'compressing Baleares',
  state: 'Estratega strategize de',
  info: 'Juguetería',
};

export const sampleWithFullData: IOrderHistory = {
  id: 94218,
  clientId: 73205,
  stockCode: 'solutions Kazajistan',
  operationType: false,
  price: 74715,
  amount: 58267,
  creationDate: dayjs('2023-11-08T17:40'),
  executionDate: dayjs('2023-11-08T19:33'),
  mode: 'recíproca',
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
