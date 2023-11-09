import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ClientStocksComponent } from './list/client-stocks.component';
import { ClientStocksDetailComponent } from './detail/client-stocks-detail.component';
import { ClientStocksUpdateComponent } from './update/client-stocks-update.component';
import { ClientStocksDeleteDialogComponent } from './delete/client-stocks-delete-dialog.component';
import { ClientStocksRoutingModule } from './route/client-stocks-routing.module';

@NgModule({
  imports: [SharedModule, ClientStocksRoutingModule],
  declarations: [ClientStocksComponent, ClientStocksDetailComponent, ClientStocksUpdateComponent, ClientStocksDeleteDialogComponent],
})
export class ClientStocksModule {}
