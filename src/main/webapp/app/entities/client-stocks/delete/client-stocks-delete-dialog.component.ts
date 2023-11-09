import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IClientStocks } from '../client-stocks.model';
import { ClientStocksService } from '../service/client-stocks.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './client-stocks-delete-dialog.component.html',
})
export class ClientStocksDeleteDialogComponent {
  clientStocks?: IClientStocks;

  constructor(protected clientStocksService: ClientStocksService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.clientStocksService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
