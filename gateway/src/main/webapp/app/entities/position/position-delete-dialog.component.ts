import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Position } from './position.model';
import { PositionPopupService } from './position-popup.service';
import { PositionService } from './position.service';

@Component({
    selector: 'jhi-position-delete-dialog',
    templateUrl: './position-delete-dialog.component.html'
})
export class PositionDeleteDialogComponent {

    position: Position;

    constructor(
        private positionService: PositionService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.positionService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'positionListModification',
                content: 'Deleted an position'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-position-delete-popup',
    template: ''
})
export class PositionDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private positionPopupService: PositionPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.positionPopupService
                .open(PositionDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
