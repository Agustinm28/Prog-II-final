import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ClientStocksComponent } from '../list/client-stocks.component';
import { ClientStocksDetailComponent } from '../detail/client-stocks-detail.component';
import { ClientStocksUpdateComponent } from '../update/client-stocks-update.component';
import { ClientStocksRoutingResolveService } from './client-stocks-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const clientStocksRoute: Routes = [
  {
    path: '',
    component: ClientStocksComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ClientStocksDetailComponent,
    resolve: {
      clientStocks: ClientStocksRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ClientStocksUpdateComponent,
    resolve: {
      clientStocks: ClientStocksRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ClientStocksUpdateComponent,
    resolve: {
      clientStocks: ClientStocksRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(clientStocksRoute)],
  exports: [RouterModule],
})
export class ClientStocksRoutingModule {}
