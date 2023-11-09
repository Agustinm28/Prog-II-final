import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IClientStocks, NewClientStocks } from '../client-stocks.model';

export type PartialUpdateClientStocks = Partial<IClientStocks> & Pick<IClientStocks, 'id'>;

export type EntityResponseType = HttpResponse<IClientStocks>;
export type EntityArrayResponseType = HttpResponse<IClientStocks[]>;

@Injectable({ providedIn: 'root' })
export class ClientStocksService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/client-stocks');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(clientStocks: NewClientStocks): Observable<EntityResponseType> {
    return this.http.post<IClientStocks>(this.resourceUrl, clientStocks, { observe: 'response' });
  }

  update(clientStocks: IClientStocks): Observable<EntityResponseType> {
    return this.http.put<IClientStocks>(`${this.resourceUrl}/${this.getClientStocksIdentifier(clientStocks)}`, clientStocks, {
      observe: 'response',
    });
  }

  partialUpdate(clientStocks: PartialUpdateClientStocks): Observable<EntityResponseType> {
    return this.http.patch<IClientStocks>(`${this.resourceUrl}/${this.getClientStocksIdentifier(clientStocks)}`, clientStocks, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IClientStocks>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClientStocks[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getClientStocksIdentifier(clientStocks: Pick<IClientStocks, 'id'>): number {
    return clientStocks.id;
  }

  compareClientStocks(o1: Pick<IClientStocks, 'id'> | null, o2: Pick<IClientStocks, 'id'> | null): boolean {
    return o1 && o2 ? this.getClientStocksIdentifier(o1) === this.getClientStocksIdentifier(o2) : o1 === o2;
  }

  addClientStocksToCollectionIfMissing<Type extends Pick<IClientStocks, 'id'>>(
    clientStocksCollection: Type[],
    ...clientStocksToCheck: (Type | null | undefined)[]
  ): Type[] {
    const clientStocks: Type[] = clientStocksToCheck.filter(isPresent);
    if (clientStocks.length > 0) {
      const clientStocksCollectionIdentifiers = clientStocksCollection.map(
        clientStocksItem => this.getClientStocksIdentifier(clientStocksItem)!
      );
      const clientStocksToAdd = clientStocks.filter(clientStocksItem => {
        const clientStocksIdentifier = this.getClientStocksIdentifier(clientStocksItem);
        if (clientStocksCollectionIdentifiers.includes(clientStocksIdentifier)) {
          return false;
        }
        clientStocksCollectionIdentifiers.push(clientStocksIdentifier);
        return true;
      });
      return [...clientStocksToAdd, ...clientStocksCollection];
    }
    return clientStocksCollection;
  }
}
