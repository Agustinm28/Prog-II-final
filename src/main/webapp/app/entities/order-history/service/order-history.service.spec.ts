import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOrderHistory } from '../order-history.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../order-history.test-samples';

import { OrderHistoryService, RestOrderHistory } from './order-history.service';

const requireRestSample: RestOrderHistory = {
  ...sampleWithRequiredData,
  fechaOperacion: sampleWithRequiredData.fechaOperacion?.toJSON(),
  fechaEjecucion: sampleWithRequiredData.fechaEjecucion?.toJSON(),
};

describe('OrderHistory Service', () => {
  let service: OrderHistoryService;
  let httpMock: HttpTestingController;
  let expectedResult: IOrderHistory | IOrderHistory[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrderHistoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a OrderHistory', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const orderHistory = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(orderHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrderHistory', () => {
      const orderHistory = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(orderHistory).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OrderHistory', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrderHistory', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OrderHistory', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOrderHistoryToCollectionIfMissing', () => {
      it('should add a OrderHistory to an empty array', () => {
        const orderHistory: IOrderHistory = sampleWithRequiredData;
        expectedResult = service.addOrderHistoryToCollectionIfMissing([], orderHistory);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderHistory);
      });

      it('should not add a OrderHistory to an array that contains it', () => {
        const orderHistory: IOrderHistory = sampleWithRequiredData;
        const orderHistoryCollection: IOrderHistory[] = [
          {
            ...orderHistory,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOrderHistoryToCollectionIfMissing(orderHistoryCollection, orderHistory);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrderHistory to an array that doesn't contain it", () => {
        const orderHistory: IOrderHistory = sampleWithRequiredData;
        const orderHistoryCollection: IOrderHistory[] = [sampleWithPartialData];
        expectedResult = service.addOrderHistoryToCollectionIfMissing(orderHistoryCollection, orderHistory);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderHistory);
      });

      it('should add only unique OrderHistory to an array', () => {
        const orderHistoryArray: IOrderHistory[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const orderHistoryCollection: IOrderHistory[] = [sampleWithRequiredData];
        expectedResult = service.addOrderHistoryToCollectionIfMissing(orderHistoryCollection, ...orderHistoryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const orderHistory: IOrderHistory = sampleWithRequiredData;
        const orderHistory2: IOrderHistory = sampleWithPartialData;
        expectedResult = service.addOrderHistoryToCollectionIfMissing([], orderHistory, orderHistory2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderHistory);
        expect(expectedResult).toContain(orderHistory2);
      });

      it('should accept null and undefined values', () => {
        const orderHistory: IOrderHistory = sampleWithRequiredData;
        expectedResult = service.addOrderHistoryToCollectionIfMissing([], null, orderHistory, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderHistory);
      });

      it('should return initial array if no OrderHistory is added', () => {
        const orderHistoryCollection: IOrderHistory[] = [sampleWithRequiredData];
        expectedResult = service.addOrderHistoryToCollectionIfMissing(orderHistoryCollection, undefined, null);
        expect(expectedResult).toEqual(orderHistoryCollection);
      });
    });

    describe('compareOrderHistory', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOrderHistory(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareOrderHistory(entity1, entity2);
        const compareResult2 = service.compareOrderHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareOrderHistory(entity1, entity2);
        const compareResult2 = service.compareOrderHistory(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareOrderHistory(entity1, entity2);
        const compareResult2 = service.compareOrderHistory(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
