import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IClientStocks } from '../client-stocks.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../client-stocks.test-samples';

import { ClientStocksService } from './client-stocks.service';

const requireRestSample: IClientStocks = {
  ...sampleWithRequiredData,
};

describe('ClientStocks Service', () => {
  let service: ClientStocksService;
  let httpMock: HttpTestingController;
  let expectedResult: IClientStocks | IClientStocks[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ClientStocksService);
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

    it('should create a ClientStocks', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const clientStocks = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(clientStocks).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ClientStocks', () => {
      const clientStocks = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(clientStocks).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ClientStocks', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ClientStocks', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ClientStocks', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addClientStocksToCollectionIfMissing', () => {
      it('should add a ClientStocks to an empty array', () => {
        const clientStocks: IClientStocks = sampleWithRequiredData;
        expectedResult = service.addClientStocksToCollectionIfMissing([], clientStocks);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientStocks);
      });

      it('should not add a ClientStocks to an array that contains it', () => {
        const clientStocks: IClientStocks = sampleWithRequiredData;
        const clientStocksCollection: IClientStocks[] = [
          {
            ...clientStocks,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addClientStocksToCollectionIfMissing(clientStocksCollection, clientStocks);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ClientStocks to an array that doesn't contain it", () => {
        const clientStocks: IClientStocks = sampleWithRequiredData;
        const clientStocksCollection: IClientStocks[] = [sampleWithPartialData];
        expectedResult = service.addClientStocksToCollectionIfMissing(clientStocksCollection, clientStocks);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientStocks);
      });

      it('should add only unique ClientStocks to an array', () => {
        const clientStocksArray: IClientStocks[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const clientStocksCollection: IClientStocks[] = [sampleWithRequiredData];
        expectedResult = service.addClientStocksToCollectionIfMissing(clientStocksCollection, ...clientStocksArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const clientStocks: IClientStocks = sampleWithRequiredData;
        const clientStocks2: IClientStocks = sampleWithPartialData;
        expectedResult = service.addClientStocksToCollectionIfMissing([], clientStocks, clientStocks2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clientStocks);
        expect(expectedResult).toContain(clientStocks2);
      });

      it('should accept null and undefined values', () => {
        const clientStocks: IClientStocks = sampleWithRequiredData;
        expectedResult = service.addClientStocksToCollectionIfMissing([], null, clientStocks, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(clientStocks);
      });

      it('should return initial array if no ClientStocks is added', () => {
        const clientStocksCollection: IClientStocks[] = [sampleWithRequiredData];
        expectedResult = service.addClientStocksToCollectionIfMissing(clientStocksCollection, undefined, null);
        expect(expectedResult).toEqual(clientStocksCollection);
      });
    });

    describe('compareClientStocks', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareClientStocks(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareClientStocks(entity1, entity2);
        const compareResult2 = service.compareClientStocks(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareClientStocks(entity1, entity2);
        const compareResult2 = service.compareClientStocks(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareClientStocks(entity1, entity2);
        const compareResult2 = service.compareClientStocks(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
