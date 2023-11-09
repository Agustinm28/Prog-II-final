import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOrderHistory } from '../order-history.model';

@Component({
  selector: 'jhi-order-history-detail',
  templateUrl: './order-history-detail.component.html',
})
export class OrderHistoryDetailComponent implements OnInit {
  orderHistory: IOrderHistory | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ orderHistory }) => {
      this.orderHistory = orderHistory;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
