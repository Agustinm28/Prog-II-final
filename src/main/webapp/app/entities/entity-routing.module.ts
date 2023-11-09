import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'client-stocks',
        data: { pageTitle: 'progIiFinalApp.clientStocks.home.title' },
        loadChildren: () => import('./client-stocks/client-stocks.module').then(m => m.ClientStocksModule),
      },
      {
        path: 'order-history',
        data: { pageTitle: 'progIiFinalApp.orderHistory.home.title' },
        loadChildren: () => import('./order-history/order-history.module').then(m => m.OrderHistoryModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
