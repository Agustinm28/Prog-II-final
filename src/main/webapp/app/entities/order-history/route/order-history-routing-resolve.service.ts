import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrderHistory } from '../order-history.model';
import { OrderHistoryService } from '../service/order-history.service';

@Injectable({ providedIn: 'root' })
export class OrderHistoryRoutingResolveService implements Resolve<IOrderHistory | null> {
  constructor(protected service: OrderHistoryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IOrderHistory | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((orderHistory: HttpResponse<IOrderHistory>) => {
          if (orderHistory.body) {
            return of(orderHistory.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
