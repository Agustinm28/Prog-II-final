import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IClientStocks } from '../client-stocks.model';

@Component({
  selector: 'jhi-client-stocks-detail',
  templateUrl: './client-stocks-detail.component.html',
})
export class ClientStocksDetailComponent implements OnInit {
  clientStocks: IClientStocks | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clientStocks }) => {
      this.clientStocks = clientStocks;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
