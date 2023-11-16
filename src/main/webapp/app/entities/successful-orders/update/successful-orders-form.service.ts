import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISuccessfulOrders, NewSuccessfulOrders } from '../successful-orders.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISuccessfulOrders for edit and NewSuccessfulOrdersFormGroupInput for create.
 */
type SuccessfulOrdersFormGroupInput = ISuccessfulOrders | PartialWithRequiredKeyOf<NewSuccessfulOrders>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ISuccessfulOrders | NewSuccessfulOrders> = Omit<T, 'fechaOperacion'> & {
  fechaOperacion?: string | null;
};

type SuccessfulOrdersFormRawValue = FormValueOf<ISuccessfulOrders>;

type NewSuccessfulOrdersFormRawValue = FormValueOf<NewSuccessfulOrders>;

type SuccessfulOrdersFormDefaults = Pick<NewSuccessfulOrders, 'id' | 'fechaOperacion' | 'operacionExitosa'>;

type SuccessfulOrdersFormGroupContent = {
  id: FormControl<SuccessfulOrdersFormRawValue['id'] | NewSuccessfulOrders['id']>;
  cliente: FormControl<SuccessfulOrdersFormRawValue['cliente']>;
  accionId: FormControl<SuccessfulOrdersFormRawValue['accionId']>;
  accion: FormControl<SuccessfulOrdersFormRawValue['accion']>;
  operacion: FormControl<SuccessfulOrdersFormRawValue['operacion']>;
  precio: FormControl<SuccessfulOrdersFormRawValue['precio']>;
  cantidad: FormControl<SuccessfulOrdersFormRawValue['cantidad']>;
  fechaOperacion: FormControl<SuccessfulOrdersFormRawValue['fechaOperacion']>;
  modo: FormControl<SuccessfulOrdersFormRawValue['modo']>;
  operacionExitosa: FormControl<SuccessfulOrdersFormRawValue['operacionExitosa']>;
  operacionObservaciones: FormControl<SuccessfulOrdersFormRawValue['operacionObservaciones']>;
};

export type SuccessfulOrdersFormGroup = FormGroup<SuccessfulOrdersFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SuccessfulOrdersFormService {
  createSuccessfulOrdersFormGroup(successfulOrders: SuccessfulOrdersFormGroupInput = { id: null }): SuccessfulOrdersFormGroup {
    const successfulOrdersRawValue = this.convertSuccessfulOrdersToSuccessfulOrdersRawValue({
      ...this.getFormDefaults(),
      ...successfulOrders,
    });
    return new FormGroup<SuccessfulOrdersFormGroupContent>({
      id: new FormControl(
        { value: successfulOrdersRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      cliente: new FormControl(successfulOrdersRawValue.cliente),
      accionId: new FormControl(successfulOrdersRawValue.accionId),
      accion: new FormControl(successfulOrdersRawValue.accion),
      operacion: new FormControl(successfulOrdersRawValue.operacion),
      precio: new FormControl(successfulOrdersRawValue.precio),
      cantidad: new FormControl(successfulOrdersRawValue.cantidad),
      fechaOperacion: new FormControl(successfulOrdersRawValue.fechaOperacion),
      modo: new FormControl(successfulOrdersRawValue.modo),
      operacionExitosa: new FormControl(successfulOrdersRawValue.operacionExitosa),
      operacionObservaciones: new FormControl(successfulOrdersRawValue.operacionObservaciones),
    });
  }

  getSuccessfulOrders(form: SuccessfulOrdersFormGroup): ISuccessfulOrders | NewSuccessfulOrders {
    return this.convertSuccessfulOrdersRawValueToSuccessfulOrders(
      form.getRawValue() as SuccessfulOrdersFormRawValue | NewSuccessfulOrdersFormRawValue
    );
  }

  resetForm(form: SuccessfulOrdersFormGroup, successfulOrders: SuccessfulOrdersFormGroupInput): void {
    const successfulOrdersRawValue = this.convertSuccessfulOrdersToSuccessfulOrdersRawValue({
      ...this.getFormDefaults(),
      ...successfulOrders,
    });
    form.reset(
      {
        ...successfulOrdersRawValue,
        id: { value: successfulOrdersRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): SuccessfulOrdersFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaOperacion: currentTime,
      operacionExitosa: false,
    };
  }

  private convertSuccessfulOrdersRawValueToSuccessfulOrders(
    rawSuccessfulOrders: SuccessfulOrdersFormRawValue | NewSuccessfulOrdersFormRawValue
  ): ISuccessfulOrders | NewSuccessfulOrders {
    return {
      ...rawSuccessfulOrders,
      fechaOperacion: dayjs(rawSuccessfulOrders.fechaOperacion, DATE_TIME_FORMAT),
    };
  }

  private convertSuccessfulOrdersToSuccessfulOrdersRawValue(
    successfulOrders: ISuccessfulOrders | (Partial<NewSuccessfulOrders> & SuccessfulOrdersFormDefaults)
  ): SuccessfulOrdersFormRawValue | PartialWithRequiredKeyOf<NewSuccessfulOrdersFormRawValue> {
    return {
      ...successfulOrders,
      fechaOperacion: successfulOrders.fechaOperacion ? successfulOrders.fechaOperacion.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
