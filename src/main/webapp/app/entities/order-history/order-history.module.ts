import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { OrderHistoryComponent } from './list/order-history.component';
import { OrderHistoryDetailComponent } from './detail/order-history-detail.component';
import { OrderHistoryUpdateComponent } from './update/order-history-update.component';
import { OrderHistoryDeleteDialogComponent } from './delete/order-history-delete-dialog.component';
import { OrderHistoryRoutingModule } from './route/order-history-routing.module';

@NgModule({
  imports: [SharedModule, OrderHistoryRoutingModule],
  declarations: [OrderHistoryComponent, OrderHistoryDetailComponent, OrderHistoryUpdateComponent, OrderHistoryDeleteDialogComponent],
})
export class OrderHistoryModule {}
