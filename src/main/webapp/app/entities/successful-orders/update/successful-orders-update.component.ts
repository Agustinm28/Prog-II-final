import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { SuccessfulOrdersFormService, SuccessfulOrdersFormGroup } from './successful-orders-form.service';
import { ISuccessfulOrders } from '../successful-orders.model';
import { SuccessfulOrdersService } from '../service/successful-orders.service';
import { Operacion } from 'app/entities/enumerations/operacion.model';
import { Modo } from 'app/entities/enumerations/modo.model';

@Component({
  selector: 'jhi-successful-orders-update',
  templateUrl: './successful-orders-update.component.html',
})
export class SuccessfulOrdersUpdateComponent implements OnInit {
  isSaving = false;
  successfulOrders: ISuccessfulOrders | null = null;
  operacionValues = Object.keys(Operacion);
  modoValues = Object.keys(Modo);

  editForm: SuccessfulOrdersFormGroup = this.successfulOrdersFormService.createSuccessfulOrdersFormGroup();

  constructor(
    protected successfulOrdersService: SuccessfulOrdersService,
    protected successfulOrdersFormService: SuccessfulOrdersFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ successfulOrders }) => {
      this.successfulOrders = successfulOrders;
      if (successfulOrders) {
        this.updateForm(successfulOrders);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const successfulOrders = this.successfulOrdersFormService.getSuccessfulOrders(this.editForm);
    if (successfulOrders.id !== null) {
      this.subscribeToSaveResponse(this.successfulOrdersService.update(successfulOrders));
    } else {
      this.subscribeToSaveResponse(this.successfulOrdersService.create(successfulOrders));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISuccessfulOrders>>): void {
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

  protected updateForm(successfulOrders: ISuccessfulOrders): void {
    this.successfulOrders = successfulOrders;
    this.successfulOrdersFormService.resetForm(this.editForm, successfulOrders);
  }
}
