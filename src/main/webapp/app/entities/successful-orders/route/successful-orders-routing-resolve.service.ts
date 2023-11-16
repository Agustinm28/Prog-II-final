import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISuccessfulOrders } from '../successful-orders.model';
import { SuccessfulOrdersService } from '../service/successful-orders.service';

@Injectable({ providedIn: 'root' })
export class SuccessfulOrdersRoutingResolveService implements Resolve<ISuccessfulOrders | null> {
  constructor(protected service: SuccessfulOrdersService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISuccessfulOrders | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((successfulOrders: HttpResponse<ISuccessfulOrders>) => {
          if (successfulOrders.body) {
            return of(successfulOrders.body);
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
