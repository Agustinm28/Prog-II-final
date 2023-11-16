import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISuccessfulOrders } from '../successful-orders.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../successful-orders.test-samples';

import { SuccessfulOrdersService, RestSuccessfulOrders } from './successful-orders.service';

const requireRestSample: RestSuccessfulOrders = {
  ...sampleWithRequiredData,
  fechaOperacion: sampleWithRequiredData.fechaOperacion?.toJSON(),
};

describe('SuccessfulOrders Service', () => {
  let service: SuccessfulOrdersService;
  let httpMock: HttpTestingController;
  let expectedResult: ISuccessfulOrders | ISuccessfulOrders[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SuccessfulOrdersService);
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

    it('should create a SuccessfulOrders', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const successfulOrders = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(successfulOrders).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SuccessfulOrders', () => {
      const successfulOrders = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(successfulOrders).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SuccessfulOrders', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SuccessfulOrders', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SuccessfulOrders', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSuccessfulOrdersToCollectionIfMissing', () => {
      it('should add a SuccessfulOrders to an empty array', () => {
        const successfulOrders: ISuccessfulOrders = sampleWithRequiredData;
        expectedResult = service.addSuccessfulOrdersToCollectionIfMissing([], successfulOrders);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(successfulOrders);
      });

      it('should not add a SuccessfulOrders to an array that contains it', () => {
        const successfulOrders: ISuccessfulOrders = sampleWithRequiredData;
        const successfulOrdersCollection: ISuccessfulOrders[] = [
          {
            ...successfulOrders,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSuccessfulOrdersToCollectionIfMissing(successfulOrdersCollection, successfulOrders);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SuccessfulOrders to an array that doesn't contain it", () => {
        const successfulOrders: ISuccessfulOrders = sampleWithRequiredData;
        const successfulOrdersCollection: ISuccessfulOrders[] = [sampleWithPartialData];
        expectedResult = service.addSuccessfulOrdersToCollectionIfMissing(successfulOrdersCollection, successfulOrders);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(successfulOrders);
      });

      it('should add only unique SuccessfulOrders to an array', () => {
        const successfulOrdersArray: ISuccessfulOrders[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const successfulOrdersCollection: ISuccessfulOrders[] = [sampleWithRequiredData];
        expectedResult = service.addSuccessfulOrdersToCollectionIfMissing(successfulOrdersCollection, ...successfulOrdersArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const successfulOrders: ISuccessfulOrders = sampleWithRequiredData;
        const successfulOrders2: ISuccessfulOrders = sampleWithPartialData;
        expectedResult = service.addSuccessfulOrdersToCollectionIfMissing([], successfulOrders, successfulOrders2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(successfulOrders);
        expect(expectedResult).toContain(successfulOrders2);
      });

      it('should accept null and undefined values', () => {
        const successfulOrders: ISuccessfulOrders = sampleWithRequiredData;
        expectedResult = service.addSuccessfulOrdersToCollectionIfMissing([], null, successfulOrders, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(successfulOrders);
      });

      it('should return initial array if no SuccessfulOrders is added', () => {
        const successfulOrdersCollection: ISuccessfulOrders[] = [sampleWithRequiredData];
        expectedResult = service.addSuccessfulOrdersToCollectionIfMissing(successfulOrdersCollection, undefined, null);
        expect(expectedResult).toEqual(successfulOrdersCollection);
      });
    });

    describe('compareSuccessfulOrders', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSuccessfulOrders(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSuccessfulOrders(entity1, entity2);
        const compareResult2 = service.compareSuccessfulOrders(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSuccessfulOrders(entity1, entity2);
        const compareResult2 = service.compareSuccessfulOrders(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSuccessfulOrders(entity1, entity2);
        const compareResult2 = service.compareSuccessfulOrders(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
