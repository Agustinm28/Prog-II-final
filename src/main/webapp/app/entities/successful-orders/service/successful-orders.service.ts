import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISuccessfulOrders, NewSuccessfulOrders } from '../successful-orders.model';

export type PartialUpdateSuccessfulOrders = Partial<ISuccessfulOrders> & Pick<ISuccessfulOrders, 'id'>;

type RestOf<T extends ISuccessfulOrders | NewSuccessfulOrders> = Omit<T, 'fechaOperacion'> & {
  fechaOperacion?: string | null;
};

export type RestSuccessfulOrders = RestOf<ISuccessfulOrders>;

export type NewRestSuccessfulOrders = RestOf<NewSuccessfulOrders>;

export type PartialUpdateRestSuccessfulOrders = RestOf<PartialUpdateSuccessfulOrders>;

export type EntityResponseType = HttpResponse<ISuccessfulOrders>;
export type EntityArrayResponseType = HttpResponse<ISuccessfulOrders[]>;

@Injectable({ providedIn: 'root' })
export class SuccessfulOrdersService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/successful-orders');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(successfulOrders: NewSuccessfulOrders): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(successfulOrders);
    return this.http
      .post<RestSuccessfulOrders>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(successfulOrders: ISuccessfulOrders): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(successfulOrders);
    return this.http
      .put<RestSuccessfulOrders>(`${this.resourceUrl}/${this.getSuccessfulOrdersIdentifier(successfulOrders)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(successfulOrders: PartialUpdateSuccessfulOrders): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(successfulOrders);
    return this.http
      .patch<RestSuccessfulOrders>(`${this.resourceUrl}/${this.getSuccessfulOrdersIdentifier(successfulOrders)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestSuccessfulOrders>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestSuccessfulOrders[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSuccessfulOrdersIdentifier(successfulOrders: Pick<ISuccessfulOrders, 'id'>): number {
    return successfulOrders.id;
  }

  compareSuccessfulOrders(o1: Pick<ISuccessfulOrders, 'id'> | null, o2: Pick<ISuccessfulOrders, 'id'> | null): boolean {
    return o1 && o2 ? this.getSuccessfulOrdersIdentifier(o1) === this.getSuccessfulOrdersIdentifier(o2) : o1 === o2;
  }

  addSuccessfulOrdersToCollectionIfMissing<Type extends Pick<ISuccessfulOrders, 'id'>>(
    successfulOrdersCollection: Type[],
    ...successfulOrdersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const successfulOrders: Type[] = successfulOrdersToCheck.filter(isPresent);
    if (successfulOrders.length > 0) {
      const successfulOrdersCollectionIdentifiers = successfulOrdersCollection.map(
        successfulOrdersItem => this.getSuccessfulOrdersIdentifier(successfulOrdersItem)!
      );
      const successfulOrdersToAdd = successfulOrders.filter(successfulOrdersItem => {
        const successfulOrdersIdentifier = this.getSuccessfulOrdersIdentifier(successfulOrdersItem);
        if (successfulOrdersCollectionIdentifiers.includes(successfulOrdersIdentifier)) {
          return false;
        }
        successfulOrdersCollectionIdentifiers.push(successfulOrdersIdentifier);
        return true;
      });
      return [...successfulOrdersToAdd, ...successfulOrdersCollection];
    }
    return successfulOrdersCollection;
  }

  protected convertDateFromClient<T extends ISuccessfulOrders | NewSuccessfulOrders | PartialUpdateSuccessfulOrders>(
    successfulOrders: T
  ): RestOf<T> {
    return {
      ...successfulOrders,
      fechaOperacion: successfulOrders.fechaOperacion?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restSuccessfulOrders: RestSuccessfulOrders): ISuccessfulOrders {
    return {
      ...restSuccessfulOrders,
      fechaOperacion: restSuccessfulOrders.fechaOperacion ? dayjs(restSuccessfulOrders.fechaOperacion) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestSuccessfulOrders>): HttpResponse<ISuccessfulOrders> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestSuccessfulOrders[]>): HttpResponse<ISuccessfulOrders[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
