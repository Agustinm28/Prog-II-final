import { IClientStocks, NewClientStocks } from './client-stocks.model';

export const sampleWithRequiredData: IClientStocks = {
  id: 62657,
};

export const sampleWithPartialData: IClientStocks = {
  id: 38886,
};

export const sampleWithFullData: IClientStocks = {
  id: 27424,
  stockAmount: 84675,
};

export const sampleWithNewData: NewClientStocks = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
