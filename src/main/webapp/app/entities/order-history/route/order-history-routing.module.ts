import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { OrderHistoryComponent } from '../list/order-history.component';
import { OrderHistoryDetailComponent } from '../detail/order-history-detail.component';
import { OrderHistoryUpdateComponent } from '../update/order-history-update.component';
import { OrderHistoryRoutingResolveService } from './order-history-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const orderHistoryRoute: Routes = [
  {
    path: '',
    component: OrderHistoryComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: OrderHistoryDetailComponent,
    resolve: {
      orderHistory: OrderHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: OrderHistoryUpdateComponent,
    resolve: {
      orderHistory: OrderHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: OrderHistoryUpdateComponent,
    resolve: {
      orderHistory: OrderHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(orderHistoryRoute)],
  exports: [RouterModule],
})
export class OrderHistoryRoutingModule {}
