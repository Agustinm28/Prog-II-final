import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'successful-orders',
        data: { pageTitle: 'complementariesApp.successfulOrders.home.title' },
        loadChildren: () => import('./successful-orders/successful-orders.module').then(m => m.SuccessfulOrdersModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
