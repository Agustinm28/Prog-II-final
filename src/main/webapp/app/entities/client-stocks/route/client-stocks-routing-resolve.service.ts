import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IClientStocks } from '../client-stocks.model';
import { ClientStocksService } from '../service/client-stocks.service';

@Injectable({ providedIn: 'root' })
export class ClientStocksRoutingResolveService implements Resolve<IClientStocks | null> {
  constructor(protected service: ClientStocksService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IClientStocks | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((clientStocks: HttpResponse<IClientStocks>) => {
          if (clientStocks.body) {
            return of(clientStocks.body);
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
