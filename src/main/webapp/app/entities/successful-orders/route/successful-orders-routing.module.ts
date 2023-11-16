import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SuccessfulOrdersComponent } from '../list/successful-orders.component';
import { SuccessfulOrdersDetailComponent } from '../detail/successful-orders-detail.component';
import { SuccessfulOrdersUpdateComponent } from '../update/successful-orders-update.component';
import { SuccessfulOrdersRoutingResolveService } from './successful-orders-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const successfulOrdersRoute: Routes = [
  {
    path: '',
    component: SuccessfulOrdersComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SuccessfulOrdersDetailComponent,
    resolve: {
      successfulOrders: SuccessfulOrdersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SuccessfulOrdersUpdateComponent,
    resolve: {
      successfulOrders: SuccessfulOrdersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SuccessfulOrdersUpdateComponent,
    resolve: {
      successfulOrders: SuccessfulOrdersRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(successfulOrdersRoute)],
  exports: [RouterModule],
})
export class SuccessfulOrdersRoutingModule {}
