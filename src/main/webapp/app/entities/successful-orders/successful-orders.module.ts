import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SuccessfulOrdersComponent } from './list/successful-orders.component';
import { SuccessfulOrdersDetailComponent } from './detail/successful-orders-detail.component';
import { SuccessfulOrdersUpdateComponent } from './update/successful-orders-update.component';
import { SuccessfulOrdersDeleteDialogComponent } from './delete/successful-orders-delete-dialog.component';
import { SuccessfulOrdersRoutingModule } from './route/successful-orders-routing.module';

@NgModule({
  imports: [SharedModule, SuccessfulOrdersRoutingModule],
  declarations: [
    SuccessfulOrdersComponent,
    SuccessfulOrdersDetailComponent,
    SuccessfulOrdersUpdateComponent,
    SuccessfulOrdersDeleteDialogComponent,
  ],
})
export class SuccessfulOrdersModule {}
