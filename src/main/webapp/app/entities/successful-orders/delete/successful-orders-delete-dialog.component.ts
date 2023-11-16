import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISuccessfulOrders } from '../successful-orders.model';
import { SuccessfulOrdersService } from '../service/successful-orders.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './successful-orders-delete-dialog.component.html',
})
export class SuccessfulOrdersDeleteDialogComponent {
  successfulOrders?: ISuccessfulOrders;

  constructor(protected successfulOrdersService: SuccessfulOrdersService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.successfulOrdersService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
