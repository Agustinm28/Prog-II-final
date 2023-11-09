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
type FormValueOf<T extends IOrderHistory | NewOrderHistory> = Omit<T, 'operationDate'> & {
  operationDate?: string | null;
};

type OrderHistoryFormRawValue = FormValueOf<IOrderHistory>;

type NewOrderHistoryFormRawValue = FormValueOf<NewOrderHistory>;

type OrderHistoryFormDefaults = Pick<NewOrderHistory, 'id' | 'operationType' | 'operationDate'>;

type OrderHistoryFormGroupContent = {
  id: FormControl<OrderHistoryFormRawValue['id'] | NewOrderHistory['id']>;
  clientId: FormControl<OrderHistoryFormRawValue['clientId']>;
  stockId: FormControl<OrderHistoryFormRawValue['stockId']>;
  operationType: FormControl<OrderHistoryFormRawValue['operationType']>;
  price: FormControl<OrderHistoryFormRawValue['price']>;
  amount: FormControl<OrderHistoryFormRawValue['amount']>;
  operationDate: FormControl<OrderHistoryFormRawValue['operationDate']>;
  mode: FormControl<OrderHistoryFormRawValue['mode']>;
  state: FormControl<OrderHistoryFormRawValue['state']>;
  info: FormControl<OrderHistoryFormRawValue['info']>;
  language: FormControl<OrderHistoryFormRawValue['language']>;
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
      clientId: new FormControl(orderHistoryRawValue.clientId),
      stockId: new FormControl(orderHistoryRawValue.stockId),
      operationType: new FormControl(orderHistoryRawValue.operationType),
      price: new FormControl(orderHistoryRawValue.price),
      amount: new FormControl(orderHistoryRawValue.amount),
      operationDate: new FormControl(orderHistoryRawValue.operationDate),
      mode: new FormControl(orderHistoryRawValue.mode),
      state: new FormControl(orderHistoryRawValue.state),
      info: new FormControl(orderHistoryRawValue.info),
      language: new FormControl(orderHistoryRawValue.language),
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
      operationType: false,
      operationDate: currentTime,
    };
  }

  private convertOrderHistoryRawValueToOrderHistory(
    rawOrderHistory: OrderHistoryFormRawValue | NewOrderHistoryFormRawValue
  ): IOrderHistory | NewOrderHistory {
    return {
      ...rawOrderHistory,
      operationDate: dayjs(rawOrderHistory.operationDate, DATE_TIME_FORMAT),
    };
  }

  private convertOrderHistoryToOrderHistoryRawValue(
    orderHistory: IOrderHistory | (Partial<NewOrderHistory> & OrderHistoryFormDefaults)
  ): OrderHistoryFormRawValue | PartialWithRequiredKeyOf<NewOrderHistoryFormRawValue> {
    return {
      ...orderHistory,
      operationDate: orderHistory.operationDate ? orderHistory.operationDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
