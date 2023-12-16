import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../successful-orders.test-samples';

import { SuccessfulOrdersFormService } from './successful-orders-form.service';

describe('SuccessfulOrders Form Service', () => {
  let service: SuccessfulOrdersFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SuccessfulOrdersFormService);
  });

  describe('Service methods', () => {
    describe('createSuccessfulOrdersFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSuccessfulOrdersFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            cliente: expect.any(Object),
            accionId: expect.any(Object),
            accion: expect.any(Object),
            operacion: expect.any(Object),
            precio: expect.any(Object),
            cantidad: expect.any(Object),
            fechaOperacion: expect.any(Object),
            modo: expect.any(Object),
            operacionExitosa: expect.any(Object),
            operacionObservaciones: expect.any(Object),
            estado: expect.any(Object),
          })
        );
      });

      it('passing ISuccessfulOrders should create a new form with FormGroup', () => {
        const formGroup = service.createSuccessfulOrdersFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            cliente: expect.any(Object),
            accionId: expect.any(Object),
            accion: expect.any(Object),
            operacion: expect.any(Object),
            precio: expect.any(Object),
            cantidad: expect.any(Object),
            fechaOperacion: expect.any(Object),
            modo: expect.any(Object),
            operacionExitosa: expect.any(Object),
            operacionObservaciones: expect.any(Object),
            estado: expect.any(Object),
          })
        );
      });
    });

    describe('getSuccessfulOrders', () => {
      it('should return NewSuccessfulOrders for default SuccessfulOrders initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createSuccessfulOrdersFormGroup(sampleWithNewData);

        const successfulOrders = service.getSuccessfulOrders(formGroup) as any;

        expect(successfulOrders).toMatchObject(sampleWithNewData);
      });

      it('should return NewSuccessfulOrders for empty SuccessfulOrders initial value', () => {
        const formGroup = service.createSuccessfulOrdersFormGroup();

        const successfulOrders = service.getSuccessfulOrders(formGroup) as any;

        expect(successfulOrders).toMatchObject({});
      });

      it('should return ISuccessfulOrders', () => {
        const formGroup = service.createSuccessfulOrdersFormGroup(sampleWithRequiredData);

        const successfulOrders = service.getSuccessfulOrders(formGroup) as any;

        expect(successfulOrders).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISuccessfulOrders should not enable id FormControl', () => {
        const formGroup = service.createSuccessfulOrdersFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSuccessfulOrders should disable id FormControl', () => {
        const formGroup = service.createSuccessfulOrdersFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
