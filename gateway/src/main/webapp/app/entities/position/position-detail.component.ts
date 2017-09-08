import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';

import { Position } from './position.model';
import { PositionService } from './position.service';

@Component({
    selector: 'jhi-position-detail',
    templateUrl: './position-detail.component.html'
})
export class PositionDetailComponent implements OnInit, OnDestroy {

    position: Position;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private positionService: PositionService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInPositions();
    }

    load(id) {
        this.positionService.find(id).subscribe((position) => {
            this.position = position;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInPositions() {
        this.eventSubscriber = this.eventManager.subscribe(
            'positionListModification',
            (response) => this.load(this.position.id)
        );
    }
}
