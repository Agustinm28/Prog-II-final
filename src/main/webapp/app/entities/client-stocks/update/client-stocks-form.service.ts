import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IClientStocks, NewClientStocks } from '../client-stocks.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClientStocks for edit and NewClientStocksFormGroupInput for create.
 */
type ClientStocksFormGroupInput = IClientStocks | PartialWithRequiredKeyOf<NewClientStocks>;

type ClientStocksFormDefaults = Pick<NewClientStocks, 'id'>;

type ClientStocksFormGroupContent = {
  id: FormControl<IClientStocks['id'] | NewClientStocks['id']>;
  stockAmount: FormControl<IClientStocks['stockAmount']>;
};

export type ClientStocksFormGroup = FormGroup<ClientStocksFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClientStocksFormService {
  createClientStocksFormGroup(clientStocks: ClientStocksFormGroupInput = { id: null }): ClientStocksFormGroup {
    const clientStocksRawValue = {
      ...this.getFormDefaults(),
      ...clientStocks,
    };
    return new FormGroup<ClientStocksFormGroupContent>({
      id: new FormControl(
        { value: clientStocksRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      stockAmount: new FormControl(clientStocksRawValue.stockAmount),
    });
  }

  getClientStocks(form: ClientStocksFormGroup): IClientStocks | NewClientStocks {
    return form.getRawValue() as IClientStocks | NewClientStocks;
  }

  resetForm(form: ClientStocksFormGroup, clientStocks: ClientStocksFormGroupInput): void {
    const clientStocksRawValue = { ...this.getFormDefaults(), ...clientStocks };
    form.reset(
      {
        ...clientStocksRawValue,
        id: { value: clientStocksRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ClientStocksFormDefaults {
    return {
      id: null,
    };
  }
}
