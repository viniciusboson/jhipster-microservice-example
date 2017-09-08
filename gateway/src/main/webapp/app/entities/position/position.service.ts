import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils } from 'ng-jhipster';

import { Position } from './position.model';
import { ResponseWrapper, createRequestOption } from '../../shared';

@Injectable()
export class PositionService {

    private resourceUrl = 'orderbook/api/positions';

    constructor(private http: Http, private dateUtils: JhiDateUtils) { }

    create(position: Position): Observable<Position> {
        const copy = this.convert(position);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    update(position: Position): Observable<Position> {
        const copy = this.convert(position);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    find(id: number): Observable<Position> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            const jsonResponse = res.json();
            this.convertItemFromServer(jsonResponse);
            return jsonResponse;
        });
    }

    query(req?: any): Observable<ResponseWrapper> {
        const options = createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
            .map((res: Response) => this.convertResponse(res));
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
    }

    private convertResponse(res: Response): ResponseWrapper {
        const jsonResponse = res.json();
        for (let i = 0; i < jsonResponse.length; i++) {
            this.convertItemFromServer(jsonResponse[i]);
        }
        return new ResponseWrapper(res.headers, jsonResponse, res.status);
    }

    private convertItemFromServer(entity: any) {
        entity.buyAt = this.dateUtils
            .convertLocalDateFromServer(entity.buyAt);
        entity.sellAt = this.dateUtils
            .convertLocalDateFromServer(entity.sellAt);
    }

    private convert(position: Position): Position {
        const copy: Position = Object.assign({}, position);
        copy.buyAt = this.dateUtils
            .convertLocalDateToServer(position.buyAt);
        copy.sellAt = this.dateUtils
            .convertLocalDateToServer(position.sellAt);
        return copy;
    }
}
