import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISuccessfulOrders } from '../successful-orders.model';

@Component({
  selector: 'jhi-successful-orders-detail',
  templateUrl: './successful-orders-detail.component.html',
})
export class SuccessfulOrdersDetailComponent implements OnInit {
  successfulOrders: ISuccessfulOrders | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ successfulOrders }) => {
      this.successfulOrders = successfulOrders;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
