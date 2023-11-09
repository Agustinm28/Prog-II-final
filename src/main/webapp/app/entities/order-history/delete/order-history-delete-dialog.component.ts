import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrderHistory } from '../order-history.model';
import { OrderHistoryService } from '../service/order-history.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './order-history-delete-dialog.component.html',
})
export class OrderHistoryDeleteDialogComponent {
  orderHistory?: IOrderHistory;

  constructor(protected orderHistoryService: OrderHistoryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.orderHistoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
