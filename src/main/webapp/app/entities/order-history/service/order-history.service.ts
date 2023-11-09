import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrderHistory, NewOrderHistory } from '../order-history.model';

export type PartialUpdateOrderHistory = Partial<IOrderHistory> & Pick<IOrderHistory, 'id'>;

type RestOf<T extends IOrderHistory | NewOrderHistory> = Omit<T, 'operationDate'> & {
  operationDate?: string | null;
};

export type RestOrderHistory = RestOf<IOrderHistory>;

export type NewRestOrderHistory = RestOf<NewOrderHistory>;

export type PartialUpdateRestOrderHistory = RestOf<PartialUpdateOrderHistory>;

export type EntityResponseType = HttpResponse<IOrderHistory>;
export type EntityArrayResponseType = HttpResponse<IOrderHistory[]>;

@Injectable({ providedIn: 'root' })
export class OrderHistoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/order-histories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(orderHistory: NewOrderHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orderHistory);
    return this.http
      .post<RestOrderHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(orderHistory: IOrderHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orderHistory);
    return this.http
      .put<RestOrderHistory>(`${this.resourceUrl}/${this.getOrderHistoryIdentifier(orderHistory)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(orderHistory: PartialUpdateOrderHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(orderHistory);
    return this.http
      .patch<RestOrderHistory>(`${this.resourceUrl}/${this.getOrderHistoryIdentifier(orderHistory)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOrderHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOrderHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOrderHistoryIdentifier(orderHistory: Pick<IOrderHistory, 'id'>): number {
    return orderHistory.id;
  }

  compareOrderHistory(o1: Pick<IOrderHistory, 'id'> | null, o2: Pick<IOrderHistory, 'id'> | null): boolean {
    return o1 && o2 ? this.getOrderHistoryIdentifier(o1) === this.getOrderHistoryIdentifier(o2) : o1 === o2;
  }

  addOrderHistoryToCollectionIfMissing<Type extends Pick<IOrderHistory, 'id'>>(
    orderHistoryCollection: Type[],
    ...orderHistoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const orderHistories: Type[] = orderHistoriesToCheck.filter(isPresent);
    if (orderHistories.length > 0) {
      const orderHistoryCollectionIdentifiers = orderHistoryCollection.map(
        orderHistoryItem => this.getOrderHistoryIdentifier(orderHistoryItem)!
      );
      const orderHistoriesToAdd = orderHistories.filter(orderHistoryItem => {
        const orderHistoryIdentifier = this.getOrderHistoryIdentifier(orderHistoryItem);
        if (orderHistoryCollectionIdentifiers.includes(orderHistoryIdentifier)) {
          return false;
        }
        orderHistoryCollectionIdentifiers.push(orderHistoryIdentifier);
        return true;
      });
      return [...orderHistoriesToAdd, ...orderHistoryCollection];
    }
    return orderHistoryCollection;
  }

  protected convertDateFromClient<T extends IOrderHistory | NewOrderHistory | PartialUpdateOrderHistory>(orderHistory: T): RestOf<T> {
    return {
      ...orderHistory,
      operationDate: orderHistory.operationDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restOrderHistory: RestOrderHistory): IOrderHistory {
    return {
      ...restOrderHistory,
      operationDate: restOrderHistory.operationDate ? dayjs(restOrderHistory.operationDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOrderHistory>): HttpResponse<IOrderHistory> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOrderHistory[]>): HttpResponse<IOrderHistory[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
