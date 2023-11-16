import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ISuccessfulOrders } from '../successful-orders.model';
import { SuccessfulOrdersService } from '../service/successful-orders.service';

import { SuccessfulOrdersRoutingResolveService } from './successful-orders-routing-resolve.service';

describe('SuccessfulOrders routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: SuccessfulOrdersRoutingResolveService;
  let service: SuccessfulOrdersService;
  let resultSuccessfulOrders: ISuccessfulOrders | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(SuccessfulOrdersRoutingResolveService);
    service = TestBed.inject(SuccessfulOrdersService);
    resultSuccessfulOrders = undefined;
  });

  describe('resolve', () => {
    it('should return ISuccessfulOrders returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSuccessfulOrders = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultSuccessfulOrders).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSuccessfulOrders = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultSuccessfulOrders).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<ISuccessfulOrders>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSuccessfulOrders = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultSuccessfulOrders).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
