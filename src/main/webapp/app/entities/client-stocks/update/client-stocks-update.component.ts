import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ClientStocksFormService, ClientStocksFormGroup } from './client-stocks-form.service';
import { IClientStocks } from '../client-stocks.model';
import { ClientStocksService } from '../service/client-stocks.service';

@Component({
  selector: 'jhi-client-stocks-update',
  templateUrl: './client-stocks-update.component.html',
})
export class ClientStocksUpdateComponent implements OnInit {
  isSaving = false;
  clientStocks: IClientStocks | null = null;

  editForm: ClientStocksFormGroup = this.clientStocksFormService.createClientStocksFormGroup();

  constructor(
    protected clientStocksService: ClientStocksService,
    protected clientStocksFormService: ClientStocksFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clientStocks }) => {
      this.clientStocks = clientStocks;
      if (clientStocks) {
        this.updateForm(clientStocks);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const clientStocks = this.clientStocksFormService.getClientStocks(this.editForm);
    if (clientStocks.id !== null) {
      this.subscribeToSaveResponse(this.clientStocksService.update(clientStocks));
    } else {
      this.subscribeToSaveResponse(this.clientStocksService.create(clientStocks));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IClientStocks>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(clientStocks: IClientStocks): void {
    this.clientStocks = clientStocks;
    this.clientStocksFormService.resetForm(this.editForm, clientStocks);
  }
}
