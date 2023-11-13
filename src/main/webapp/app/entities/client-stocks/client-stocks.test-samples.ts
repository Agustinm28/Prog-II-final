import { IClientStocks, NewClientStocks } from './client-stocks.model';

export const sampleWithRequiredData: IClientStocks = {
  id: 62657,
};

export const sampleWithPartialData: IClientStocks = {
  id: 84675,
};

export const sampleWithFullData: IClientStocks = {
  id: 25043,
  clientId: 4342,
  stockCode: 'Director ROI Corporativo',
  stockAmount: 81192,
};

export const sampleWithNewData: NewClientStocks = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
