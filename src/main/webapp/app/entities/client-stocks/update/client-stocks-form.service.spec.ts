import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../client-stocks.test-samples';

import { ClientStocksFormService } from './client-stocks-form.service';

describe('ClientStocks Form Service', () => {
  let service: ClientStocksFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClientStocksFormService);
  });

  describe('Service methods', () => {
    describe('createClientStocksFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createClientStocksFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            clientId: expect.any(Object),
            stockCode: expect.any(Object),
            stockAmount: expect.any(Object),
          })
        );
      });

      it('passing IClientStocks should create a new form with FormGroup', () => {
        const formGroup = service.createClientStocksFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            clientId: expect.any(Object),
            stockCode: expect.any(Object),
            stockAmount: expect.any(Object),
          })
        );
      });
    });

    describe('getClientStocks', () => {
      it('should return NewClientStocks for default ClientStocks initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createClientStocksFormGroup(sampleWithNewData);

        const clientStocks = service.getClientStocks(formGroup) as any;

        expect(clientStocks).toMatchObject(sampleWithNewData);
      });

      it('should return NewClientStocks for empty ClientStocks initial value', () => {
        const formGroup = service.createClientStocksFormGroup();

        const clientStocks = service.getClientStocks(formGroup) as any;

        expect(clientStocks).toMatchObject({});
      });

      it('should return IClientStocks', () => {
        const formGroup = service.createClientStocksFormGroup(sampleWithRequiredData);

        const clientStocks = service.getClientStocks(formGroup) as any;

        expect(clientStocks).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IClientStocks should not enable id FormControl', () => {
        const formGroup = service.createClientStocksFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewClientStocks should disable id FormControl', () => {
        const formGroup = service.createClientStocksFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
