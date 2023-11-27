import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOrderHistory, NewOrderHistory } from '../order-history.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrderHistory for edit and NewOrderHistoryFormGroupInput for create.
 */
type OrderHistoryFormGroupInput = IOrderHistory | PartialWithRequiredKeyOf<NewOrderHistory>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOrderHistory | NewOrderHistory> = Omit<T, 'fechaOperacion' | 'fechaEjecucion'> & {
  fechaOperacion?: string | null;
  fechaEjecucion?: string | null;
};

type OrderHistoryFormRawValue = FormValueOf<IOrderHistory>;

type NewOrderHistoryFormRawValue = FormValueOf<NewOrderHistory>;

type OrderHistoryFormDefaults = Pick<NewOrderHistory, 'id' | 'operacion' | 'fechaOperacion' | 'fechaEjecucion'>;

type OrderHistoryFormGroupContent = {
  id: FormControl<OrderHistoryFormRawValue['id'] | NewOrderHistory['id']>;
  cliente: FormControl<OrderHistoryFormRawValue['cliente']>;
  accionId: FormControl<OrderHistoryFormRawValue['accionId']>;
  accion: FormControl<OrderHistoryFormRawValue['accion']>;
  operacion: FormControl<OrderHistoryFormRawValue['operacion']>;
  cantidad: FormControl<OrderHistoryFormRawValue['cantidad']>;
  precio: FormControl<OrderHistoryFormRawValue['precio']>;
  fechaOperacion: FormControl<OrderHistoryFormRawValue['fechaOperacion']>;
  modo: FormControl<OrderHistoryFormRawValue['modo']>;
  estado: FormControl<OrderHistoryFormRawValue['estado']>;
  operacionObservaciones: FormControl<OrderHistoryFormRawValue['operacionObservaciones']>;
  fechaEjecucion: FormControl<OrderHistoryFormRawValue['fechaEjecucion']>;
};

export type OrderHistoryFormGroup = FormGroup<OrderHistoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrderHistoryFormService {
  createOrderHistoryFormGroup(orderHistory: OrderHistoryFormGroupInput = { id: null }): OrderHistoryFormGroup {
    const orderHistoryRawValue = this.convertOrderHistoryToOrderHistoryRawValue({
      ...this.getFormDefaults(),
      ...orderHistory,
    });
    return new FormGroup<OrderHistoryFormGroupContent>({
      id: new FormControl(
        { value: orderHistoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      cliente: new FormControl(orderHistoryRawValue.cliente, {
        validators: [Validators.required],
      }),
      accionId: new FormControl(orderHistoryRawValue.accionId, {
        validators: [Validators.required],
      }),
      accion: new FormControl(orderHistoryRawValue.accion, {
        validators: [Validators.required],
      }),
      operacion: new FormControl(orderHistoryRawValue.operacion, {
        validators: [Validators.required],
      }),
      cantidad: new FormControl(orderHistoryRawValue.cantidad),
      precio: new FormControl(orderHistoryRawValue.precio),
      fechaOperacion: new FormControl(orderHistoryRawValue.fechaOperacion),
      modo: new FormControl(orderHistoryRawValue.modo, {
        validators: [Validators.required],
      }),
      estado: new FormControl(orderHistoryRawValue.estado),
      operacionObservaciones: new FormControl(orderHistoryRawValue.operacionObservaciones),
      fechaEjecucion: new FormControl(orderHistoryRawValue.fechaEjecucion),
    });
  }

  getOrderHistory(form: OrderHistoryFormGroup): IOrderHistory | NewOrderHistory {
    return this.convertOrderHistoryRawValueToOrderHistory(form.getRawValue() as OrderHistoryFormRawValue | NewOrderHistoryFormRawValue);
  }

  resetForm(form: OrderHistoryFormGroup, orderHistory: OrderHistoryFormGroupInput): void {
    const orderHistoryRawValue = this.convertOrderHistoryToOrderHistoryRawValue({ ...this.getFormDefaults(), ...orderHistory });
    form.reset(
      {
        ...orderHistoryRawValue,
        id: { value: orderHistoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrderHistoryFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      operacion: false,
      fechaOperacion: currentTime,
      fechaEjecucion: currentTime,
    };
  }

  private convertOrderHistoryRawValueToOrderHistory(
    rawOrderHistory: OrderHistoryFormRawValue | NewOrderHistoryFormRawValue
  ): IOrderHistory | NewOrderHistory {
    return {
      ...rawOrderHistory,
      fechaOperacion: dayjs(rawOrderHistory.fechaOperacion, DATE_TIME_FORMAT),
      fechaEjecucion: dayjs(rawOrderHistory.fechaEjecucion, DATE_TIME_FORMAT),
    };
  }

  private convertOrderHistoryToOrderHistoryRawValue(
    orderHistory: IOrderHistory | (Partial<NewOrderHistory> & OrderHistoryFormDefaults)
  ): OrderHistoryFormRawValue | PartialWithRequiredKeyOf<NewOrderHistoryFormRawValue> {
    return {
      ...orderHistory,
      fechaOperacion: orderHistory.fechaOperacion ? orderHistory.fechaOperacion.format(DATE_TIME_FORMAT) : undefined,
      fechaEjecucion: orderHistory.fechaEjecucion ? orderHistory.fechaEjecucion.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
