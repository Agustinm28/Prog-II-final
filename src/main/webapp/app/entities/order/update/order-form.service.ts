import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IOrder, NewOrder } from '../order.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrder for edit and NewOrderFormGroupInput for create.
 */
type OrderFormGroupInput = IOrder | PartialWithRequiredKeyOf<NewOrder>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IOrder | NewOrder> = Omit<T, 'fechaOperacion'> & {
  fechaOperacion?: string | null;
};

type OrderFormRawValue = FormValueOf<IOrder>;

type NewOrderFormRawValue = FormValueOf<NewOrder>;

type OrderFormDefaults = Pick<NewOrder, 'id' | 'fechaOperacion'>;

type OrderFormGroupContent = {
  id: FormControl<OrderFormRawValue['id'] | NewOrder['id']>;
  cliente: FormControl<OrderFormRawValue['cliente']>;
  accionId: FormControl<OrderFormRawValue['accionId']>;
  accion: FormControl<OrderFormRawValue['accion']>;
  operacion: FormControl<OrderFormRawValue['operacion']>;
  precio: FormControl<OrderFormRawValue['precio']>;
  cantidad: FormControl<OrderFormRawValue['cantidad']>;
  fechaOperacion: FormControl<OrderFormRawValue['fechaOperacion']>;
  modo: FormControl<OrderFormRawValue['modo']>;
  estado: FormControl<OrderFormRawValue['estado']>;
};

export type OrderFormGroup = FormGroup<OrderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrderFormService {
  createOrderFormGroup(order: OrderFormGroupInput = { id: null }): OrderFormGroup {
    const orderRawValue = this.convertOrderToOrderRawValue({
      ...this.getFormDefaults(),
      ...order,
    });
    return new FormGroup<OrderFormGroupContent>({
      id: new FormControl(
        { value: orderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      cliente: new FormControl(orderRawValue.cliente),
      accionId: new FormControl(orderRawValue.accionId),
      accion: new FormControl(orderRawValue.accion),
      operacion: new FormControl(orderRawValue.operacion),
      precio: new FormControl(orderRawValue.precio),
      cantidad: new FormControl(orderRawValue.cantidad),
      fechaOperacion: new FormControl(orderRawValue.fechaOperacion),
      modo: new FormControl(orderRawValue.modo),
      estado: new FormControl(orderRawValue.estado),
    });
  }

  getOrder(form: OrderFormGroup): IOrder | NewOrder {
    return this.convertOrderRawValueToOrder(form.getRawValue() as OrderFormRawValue | NewOrderFormRawValue);
  }

  resetForm(form: OrderFormGroup, order: OrderFormGroupInput): void {
    const orderRawValue = this.convertOrderToOrderRawValue({ ...this.getFormDefaults(), ...order });
    form.reset(
      {
        ...orderRawValue,
        id: { value: orderRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): OrderFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      fechaOperacion: currentTime,
    };
  }

  private convertOrderRawValueToOrder(rawOrder: OrderFormRawValue | NewOrderFormRawValue): IOrder | NewOrder {
    return {
      ...rawOrder,
      fechaOperacion: dayjs(rawOrder.fechaOperacion, DATE_TIME_FORMAT),
    };
  }

  private convertOrderToOrderRawValue(
    order: IOrder | (Partial<NewOrder> & OrderFormDefaults)
  ): OrderFormRawValue | PartialWithRequiredKeyOf<NewOrderFormRawValue> {
    return {
      ...order,
      fechaOperacion: order.fechaOperacion ? order.fechaOperacion.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
